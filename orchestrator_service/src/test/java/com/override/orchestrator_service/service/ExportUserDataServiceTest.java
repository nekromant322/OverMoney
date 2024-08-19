package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExportUserDataServiceTest {

    @InjectMocks
    private ExportUserDataService exportUserDataService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private OverMoneyAccountService overMoneyAccountService;
    @Test
    public void downloadExcelFileTest() throws IOException, InstanceNotFoundException {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        CategoryDTO categoryDTO = TestFieldsUtil.generateTestCategoryDTO();
        categoryDTOList.add(categoryDTO);
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        TransactionDTO transactionDTO = TestFieldsUtil.generateTestTransactionDTO();
        transactionDTOList.add(transactionDTO);
        Long testTelegramId = 1L;
        Long testChatId = 2L;
        OverMoneyAccount testAccount = new OverMoneyAccount();
        testAccount.setChatId(testChatId);
        when(overMoneyAccountService.getAccountByUserId(testTelegramId)).thenReturn(testAccount);
        when(categoryService.findCategoriesListByChatId(testChatId)).thenReturn(categoryDTOList);
        when(transactionService.findAlltransactionDTOForAcountByChatId(testChatId)).thenReturn(transactionDTOList);

        ResponseEntity<InputStreamResource> response = exportUserDataService.downloadExelFile(testTelegramId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("attachment; filename=export.xlsx", response.getHeaders().getFirst("Content-Disposition"));
        Assertions.assertNotNull(response.getBody());

    }
}
