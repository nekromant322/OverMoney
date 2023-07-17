package com.override.orchestrator_service.service;

import com.override.dto.TransactionExelDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TransactionExelService {
    @Autowired
    private TelegramUtils telegramUtils;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private OverMoneyAccountService accountService;
    @Autowired
    private CategoryMapper categoryMapper;

    public ResponseEntity<String> processTransactionExel(Principal principal, MultipartFile file) {
        List<TransactionExelDTO> transactionExelDTOS;
        try {
            transactionExelDTOS = getTransactionExelDTOFromExelFile(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long id = telegramUtils.getTelegramId(principal);
        OverMoneyAccount overMoneyAccount = accountService
                .getOverMoneyAccountByChatId(id);

        saveCategoriesFromExel(transactionExelDTOS, overMoneyAccount);


        transactionExelDTOS.forEach(transactionExelDTO -> {
            saveTransactionFromExel(transactionExelDTO, id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<TransactionExelDTO> getTransactionExelDTOFromExelFile(MultipartFile file) throws IOException {
        List<TransactionExelDTO> transactionExelDTOS = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            TransactionExelDTO transactionExelDTO = TransactionExelDTO.builder()
                    .type(Type.EXPENSE)
                    .category(row.getCell(0).getStringCellValue())
                    .amount(row.getCell(1).getNumericCellValue())
                    .amount(row.getCell(1).getNumericCellValue())
                    .message(row.getCell(2).getStringCellValue())
                    .date(row.getCell(4).getLocalDateTimeCellValue())
                    .build();
            transactionExelDTOS.add(transactionExelDTO);
        }
        workbook.close();
        return transactionExelDTOS;
    }

    public void saveTransactionFromExel(TransactionExelDTO transactionExelDTO, Long accountId) {
        transactionService.saveTransaction(transactionProcessingService.processTransactionFromExel(transactionExelDTO, accountId));
    }

    public void saveCategoriesFromExel(List<TransactionExelDTO> transactionExelDTOS, OverMoneyAccount overMoneyAccount) {
        Set<Category> categories = new HashSet<>();
        transactionExelDTOS.forEach(tr -> categories.add(categoryMapper.mapTransactionExelDTOToCategory(tr, overMoneyAccount)));
        categories.removeAll(overMoneyAccount.getCategories());
        categoryService.saveCategories(categories);
    }
}
