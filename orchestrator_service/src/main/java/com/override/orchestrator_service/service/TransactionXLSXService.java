package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.exception.XLSXProcessingException;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
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
import org.thymeleaf.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TransactionXLSXService {

    @Autowired
    private TelegramUtils telegramUtils;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    public ResponseEntity<String> loadingTransactionsFromXLSX(Principal principal, MultipartFile file) {
        List<TransactionDTO> transactionDTOS;
        transactionDTOS = getTransactionDTOFromXLSXFile(file);
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(telegramUtils.getTelegramId(principal));

        saveAllCategoriesFromXLSX(transactionDTOS, overMoneyAccount);
        saveAllTransactionFromXLSX(transactionDTOS, overMoneyAccount);
        log.info("Произведена загрузка транзакций");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<TransactionDTO> getTransactionDTOFromXLSXFile(MultipartFile file) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Number amount = row.getCell(1).getNumericCellValue();
                TransactionDTO transactionDTO = TransactionDTO.builder()
                        .categoryName(StringUtils.capitalize(row.getCell(0).getStringCellValue()))
                        .message(row.getCell(2).getStringCellValue())
                        .amount(amount.doubleValue())
                        .date(row.getCell(4).getLocalDateTimeCellValue())
                        .build();
                if (!transactionDTO.getCategoryName().isEmpty()) {
                    transactionDTOS.add(transactionDTO);
                }
            }
        } catch (Exception e) {
            throw new XLSXProcessingException("Не удалось распознать транзакции из XLSX");
        }
        return transactionDTOS;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO,
                                         OverMoneyAccount overMoneyAccount, Set<Category> categories) {
        Category category =
                transactionProcessingService.getMatchingCategory(categories, transactionDTO.getCategoryName());
        return Transaction.builder()
                .message(transactionDTO.getMessage())
                .category(category)
                .amount(transactionDTO.getAmount())
                .date(transactionDTO.getDate())
                .account(overMoneyAccount)
                .telegramUserId(overMoneyAccount.getChatId())
                .build();
    }

    public Category createCategory(TransactionDTO transactionDTO, OverMoneyAccount overMoneyAccount) {
        return Category.builder()
                .name(StringUtils.capitalize(transactionDTO.getCategoryName()))
                .type(Type.EXPENSE)
                .account(overMoneyAccount)
                .build();
    }

    public void saveAllTransactionFromXLSX(List<TransactionDTO> transactionDTOS, OverMoneyAccount overMoneyAccount) {
        Set<Category> categories = categoryService.getCategoriesByUserId(overMoneyAccount.getId());
        List<Transaction> transactions = new ArrayList<>();
        transactionDTOS.forEach(tr -> transactions.add(createTransaction(tr, overMoneyAccount, categories)));
        transactionService.saveAllTransactions(transactions);
    }

    public void saveAllCategoriesFromXLSX(List<TransactionDTO> transactionDTOS, OverMoneyAccount overMoneyAccount) {
        Set<Category> categories = new HashSet<>();
        Set<Category> existingCategories = overMoneyAccount.getCategories();
        List<String> existingCategoriesName = new ArrayList<>();
        if (!existingCategories.isEmpty()) {
            existingCategories.forEach(category -> existingCategoriesName.add(category.getName()));
        }
        for (TransactionDTO transactionDTO : transactionDTOS) {
            if (!existingCategoriesName.contains(transactionDTO.getCategoryName())) {
                categories.add(createCategory(transactionDTO, overMoneyAccount));
            }
        }
        categoryService.saveAllCategories(categories);
    }
}
