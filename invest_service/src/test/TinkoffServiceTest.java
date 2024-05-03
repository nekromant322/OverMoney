import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.invest_service.dto.IMOEXDataDTO.IMOEXData;
import com.override.invest_service.dto.IMOEXDataDTO.IMOEXDataDTO;
import com.override.invest_service.dto.MarketTQBRDataDTO;
import com.override.invest_service.service.MOEXService;
import com.override.invest_service.service.TinkoffService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.util.ResourceUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class TinkoffServiceTest {
    private final double TARGET_TOTAL_WEIGHT = 100d;

    private final String testMarketData = "[{\"ticker\":\"ENPG\",\"price\":480.8,\"lots\":1},{\"ticker\":\"TATN\",\"price\":718.3,\"lots\":1},{\"ticker\":\"GLTR\",\"price\":791.05,\"lots\":1},{\"ticker\":\"RTKM\",\"price\":96.77,\"lots\":10},{\"ticker\":\"MGNT\",\"price\":8405.0,\"lots\":1},{\"ticker\":\"SMLT\",\"price\":3709.5,\"lots\":1},{\"ticker\":\"RUAL\",\"price\":43.53,\"lots\":10},{\"ticker\":\"MTLR\",\"price\":252.57,\"lots\":1},{\"ticker\":\"VKCO\",\"price\":577.0,\"lots\":1},{\"ticker\":\"FLOT\",\"price\":133.76,\"lots\":10},{\"ticker\":\"PLZL\",\"price\":13226.0,\"lots\":1},{\"ticker\":\"FEES\",\"price\":0.13106,\"lots\":10000},{\"ticker\":\"FIVE\",\"price\":2798.0,\"lots\":1},{\"ticker\":\"AFKS\",\"price\":25.745,\"lots\":100},{\"ticker\":\"MAGN\",\"price\":54.97,\"lots\":10},{\"ticker\":\"SBER\",\"price\":308.24,\"lots\":10},{\"ticker\":\"NVTK\",\"price\":1235.0,\"lots\":1},{\"ticker\":\"BSPB\",\"price\":342.28,\"lots\":10},{\"ticker\":\"TRNFP\",\"price\":1597.5,\"lots\":1},{\"ticker\":\"SNGSP\",\"price\":67.415,\"lots\":100},{\"ticker\":\"CHMF\",\"price\":1927.6,\"lots\":1},{\"ticker\":\"SNGS\",\"price\":35.005,\"lots\":100},{\"ticker\":\"CBOM\",\"price\":7.454,\"lots\":100},{\"ticker\":\"MTSS\",\"price\":309.95,\"lots\":10},{\"ticker\":\"SBERP\",\"price\":308.87,\"lots\":10},{\"ticker\":\"SGZH\",\"price\":3.84,\"lots\":100},{\"ticker\":\"TCSG\",\"price\":3084.0,\"lots\":1},{\"ticker\":\"VTBR\",\"price\":0.023365,\"lots\":10000},{\"ticker\":\"MTLRP\",\"price\":272.95,\"lots\":10},{\"ticker\":\"GMKN\",\"price\":153.9,\"lots\":10},{\"ticker\":\"SELG\",\"price\":74.23,\"lots\":10},{\"ticker\":\"OZON\",\"price\":4368.5,\"lots\":1},{\"ticker\":\"POSI\",\"price\":3038.8,\"lots\":1},{\"ticker\":\"GAZP\",\"price\":163.22,\"lots\":10},{\"ticker\":\"MOEX\",\"price\":236.2,\"lots\":10},{\"ticker\":\"ALRS\",\"price\":77.4,\"lots\":10},{\"ticker\":\"AFLT\",\"price\":51.57,\"lots\":10},{\"ticker\":\"IRAO\",\"price\":4.2775,\"lots\":100},{\"ticker\":\"TATNP\",\"price\":718.4,\"lots\":1},{\"ticker\":\"NLMK\",\"price\":241.74,\"lots\":10},{\"ticker\":\"LKOH\",\"price\":8085.5,\"lots\":1},{\"ticker\":\"MSNG\",\"price\":3.3455,\"lots\":1000},{\"ticker\":\"PIKK\",\"price\":861.0,\"lots\":1},{\"ticker\":\"AGRO\",\"price\":1554.6,\"lots\":1},{\"ticker\":\"HYDR\",\"price\":0.7274,\"lots\":1000},{\"ticker\":\"PHOR\",\"price\":6610.0,\"lots\":1},{\"ticker\":\"ROSN\",\"price\":582.3,\"lots\":1},{\"ticker\":\"UPRO\",\"price\":2.195,\"lots\":1000}]";

    @Mock
    private MOEXService moexService;

    @InjectMocks
    private TinkoffService tinkoffService;

    private Map<String, MarketTQBRDataDTO> marketTQBRDataDTOMap;

    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();

        try {
            marketTQBRDataDTOMap = Arrays.stream(objectMapper.readValue(testMarketData, MarketTQBRDataDTO[].class))
                    .collect(Collectors.toMap(MarketTQBRDataDTO::getTicker, Function.identity()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Тест для проверки суммы весов в индексе,
     * сумма весов должна быть равна 100%
     */
    @ParameterizedTest
    @MethodSource("provideInvestAmounts")
    public void complianceToTotalWeightTest(double investAmount) throws IOException {
        Map<String, Double> tickerToWeight = buildRebalancedIMOEXIndexTest(investAmount);

        double totalWeight = tickerToWeight.values().stream().mapToDouble(Double::doubleValue).sum();

        assertEquals(totalWeight, TARGET_TOTAL_WEIGHT, 0.1d);
    }

    /**
     * Тест для проверки стоимости собранного портфеля на основе индекса,
     * стоимость не должна превышать заданную сумму инвестиции
     */
    @ParameterizedTest
    @MethodSource("provideInvestAmounts")
    public void portfolioValueInLimitAmountTest(double investAmount) throws IOException {
        Map<String, Double> tickerToWeight = buildRebalancedIMOEXIndexTest(investAmount);

        double currentSum = tickerToWeight
                .entrySet()
                .stream()
                .flatMapToDouble(tickerWeightPair -> {
                    int lots = marketTQBRDataDTOMap.get(tickerWeightPair.getKey()).getLots();
                    double priceForOne = marketTQBRDataDTOMap.get(tickerWeightPair.getKey()).getPrice();
                    double correctPrice = investAmount * tickerWeightPair.getValue() / 100;
                    int correctQuantity = (int) (correctPrice / priceForOne);
                    if (lots > 1) {
                        correctQuantity = correctQuantity - (correctQuantity % lots);
                    }
                    return DoubleStream.of(correctQuantity * priceForOne);
                }).sum();

        assertTrue(currentSum < investAmount);
        assertTrue(0 < currentSum);
    }

    private Map<String, Double> buildRebalancedIMOEXIndexTest(double investAmount) throws IOException {
        Mockito.when(moexService.getTickerToWeight()).thenReturn(
                objectMapper.readValue(ResourceUtils.getFile(MOEXService.IMOEX_DATA_FILENAME), IMOEXDataDTO.class)
                        .getAnalytics()
                        .getImoexData()
                        .stream()
                        .collect(Collectors.toMap(
                                IMOEXData::getSecIds,
                                IMOEXData::getWeight)));

        return tinkoffService.rebalanceIndexByAmount(
                moexService.getTickerToWeight(), marketTQBRDataDTOMap, investAmount);
    }

    private static Stream<Arguments> provideInvestAmounts() {
        return Stream.of(
                Arguments.of(30000),
                Arguments.of(50000),
                Arguments.of(500000),
                Arguments.of(5000000),
                Arguments.of(10000000),
                Arguments.of(30000000)
        );
    }
}