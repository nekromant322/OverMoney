package com.override.orchestrator_service.service;

import com.override.dto.tinkoff.TinkoffAccountDTO;
import com.override.dto.tinkoff.TinkoffActiveMOEXDTO;
import com.override.dto.tinkoff.TinkoffInfoDTO;
import com.override.orchestrator_service.feign.InvestFeign;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.TinkoffInfo;
import com.override.orchestrator_service.repository.InvestTinkoffInfoRepository;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvestTinkoffInfoServiceTest {

    @InjectMocks
    private InvestTinkoffInfoService investTinkoffInfoService;

    @Mock
    private InvestTinkoffInfoRepository investTinkoffInfoRepository;

    @Mock
    private InvestFeign investFeign;

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("findTinkoffInfoUser")
    public void findTinkoffInfoTest(TinkoffInfoDTO tinkoffInfoDTO) {
        OverMoneyAccount savedAccount = new OverMoneyAccount();
        TinkoffInfo build = TinkoffInfo.builder()
                .id(tinkoffInfoDTO.getTinkoffAccountId())
                .token(tinkoffInfoDTO.getToken())
                .favoriteAccountId(tinkoffInfoDTO.getFavoriteAccountId())
                .account(savedAccount)
                .build();
        when(investTinkoffInfoRepository.findTinkoffInfoById(tinkoffInfoDTO.getTinkoffAccountId())).thenReturn(Optional.ofNullable(build));
        TinkoffInfoDTO findTinkoffInfoDTO = investTinkoffInfoService.findTinkoffInfo(tinkoffInfoDTO.getTinkoffAccountId());
        assertEquals(tinkoffInfoDTO, findTinkoffInfoDTO);
    }

    private static Stream<Arguments> findTinkoffInfoUser() {
        return TestFieldsUtil.findTinkoffInfoUser();
    }

    @ParameterizedTest
    @MethodSource("dataForGetActivesMoexPercentageTest_notNull")
    public void getActivesMoexPercentageTest_notNull(List<TinkoffActiveMOEXDTO> tinkoffActiveMOEXDTOS,
                                             TinkoffInfoDTO tinkoffInfoDTO) {
    when(investFeign.getActivesMoexPercentage(tinkoffInfoDTO.getToken(),
            tinkoffInfoDTO.getTinkoffAccountId().toString(), tinkoffInfoDTO.getUserTargetInvestAmount())).thenReturn(tinkoffActiveMOEXDTOS);
        List<TinkoffActiveMOEXDTO> activesMoexPercentage = investTinkoffInfoService.getActivesMoexPercentage(tinkoffInfoDTO.getToken(), tinkoffInfoDTO.getTinkoffAccountId().toString(), tinkoffInfoDTO.getUserTargetInvestAmount());
        assertEquals(tinkoffActiveMOEXDTOS, activesMoexPercentage);
    }

    private static Stream<Arguments> dataForGetActivesMoexPercentageTest_notNull() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.tinkoffActiveMOEXDTOSData_notNull(), TestFieldsUtil.tinkoffInfoDTOData_notNull(), TestFieldsUtil.tinkoffInfoDTOData_notNull()),
                Arguments.of(TestFieldsUtil.tinkoffActiveMOEXDTOSData_withNullFields(), TestFieldsUtil.tinkoffInfoDTOData_notNull())
        );
    }

    @ParameterizedTest
    @MethodSource("dataForGetActivesMoexPercentageTest_null")
    public void getActivesMoexPercentageTest_null(List<TinkoffActiveMOEXDTO> tinkoffActiveMOEXDTOS,
                                             TinkoffInfoDTO tinkoffInfoDTO) {
        List<TinkoffActiveMOEXDTO> activesMoexPercentage = investTinkoffInfoService.getActivesMoexPercentage(tinkoffInfoDTO.getToken(), tinkoffInfoDTO.getTinkoffAccountId().toString(), tinkoffInfoDTO.getUserTargetInvestAmount());
        assertEquals(tinkoffActiveMOEXDTOS, activesMoexPercentage);
    }

    private static Stream<Arguments> dataForGetActivesMoexPercentageTest_null() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.tinkoffActiveMOEXDTOSData_null(), TestFieldsUtil.tinkoffInfoDTOData_empty())
        );
    }

    @ParameterizedTest
    @MethodSource("dataForGetUserAccountsTest_notNull")
    public void getUserAccountsTest_notNull(List<TinkoffAccountDTO> tinkoffAccountDTOS,
                                         TinkoffInfoDTO tinkoffInfoDTO) {
        when(investFeign.getUserAccounts(tinkoffInfoDTO.getToken())).thenReturn(tinkoffAccountDTOS);
        List<TinkoffAccountDTO> userAccounts = investTinkoffInfoService.getUserAccounts(tinkoffInfoDTO.getToken());
        assertEquals(tinkoffAccountDTOS, userAccounts);
    }


    private static Stream<Arguments> dataForGetUserAccountsTest_notNull() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.tinkoffAccountDTOSData_notNull(), TestFieldsUtil.tinkoffInfoDTOData_notNull())
        );
    }

    @ParameterizedTest
    @MethodSource("dataForGetUserAccountsTest_null")
    public void getUserAccountsTest_null(List<TinkoffAccountDTO> tinkoffAccountDTOS,
                                                  TinkoffInfoDTO tinkoffInfoDTO) {
        List<TinkoffAccountDTO> userAccounts = investTinkoffInfoService.getUserAccounts(tinkoffInfoDTO.getToken());
        assertEquals(tinkoffAccountDTOS, userAccounts);
    }


    private static Stream<Arguments> dataForGetUserAccountsTest_null() {
        return Stream.of(
                Arguments.of(TestFieldsUtil.tinkoffAccountDTOSData_null(), TestFieldsUtil.tinkoffInfoDTOData_empty())
        );
    }
}
