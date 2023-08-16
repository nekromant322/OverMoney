package com.override.orchestrator_service.service;

import com.override.dto.tinkoff.TinkoffInfoDTO;
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

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("findTinkoffInfoUser")
    public void findTinkoffInfoTest(TinkoffInfoDTO tinkoffInfoDTO) {
        OverMoneyAccount savedAccount = new OverMoneyAccount();
        TinkoffInfo build = TinkoffInfo.builder()
                .id(tinkoffInfoDTO.getId())
                .token(tinkoffInfoDTO.getToken())
                .favoriteAccountId(tinkoffInfoDTO.getFavoriteAccountId())
                .account(savedAccount)
                .build();
        when(investTinkoffInfoRepository.findTinkoffInfoById(tinkoffInfoDTO.getId())).thenReturn(Optional.ofNullable(build));
        TinkoffInfoDTO findTinkoffInfoDTO = investTinkoffInfoService.findTinkoffInfo(tinkoffInfoDTO.getId());
        assertEquals(tinkoffInfoDTO, findTinkoffInfoDTO);
    }

    private static Stream<Arguments> findTinkoffInfoUser() {
        return TestFieldsUtil.findTinkoffInfoUser();
    }
}
