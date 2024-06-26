package com.override.orchestrator_service.service;

import com.override.dto.BackupUserDataDTO;
import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.mapper.CategoryMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.model.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BackupUserDataService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private OverMoneyAccountService overMoneyAccountService;
    @Autowired
    TransactionProcessingService transactionProcessingService;

    public BackupUserDataDTO createBackupUserData(Long telegramId) throws InstanceNotFoundException {
        Long chatId = overMoneyAccountService.getAccountByUserId(telegramId).getChatId();
        return createBackup(chatId);
    }

    public BackupUserDataDTO createBackupRemovedUserData(Long telegramId) {
        return createBackup(telegramId);
    }

    public BackupUserDataDTO createBackup(Long id) {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByChatId(id);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByChatId(id);

        return BackupUserDataDTO.builder()
                .categoryDTOList(categoryDTOList)
                .transactionDTOList(transactionDTOList).build();
    }

    public void writingDataFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<Transaction> transactionList = createTransactionsFromBackupFile(backupUserDataDTO, telegramId);
        transactionService.saveAllTransactions(transactionList);
    }

    public List<Transaction> createTransactionsFromBackupFile(BackupUserDataDTO backupUserDataDTO, Long telegramId) {
        List<TransactionDTO> transactionDTOList = backupUserDataDTO.getTransactionDTOList();
        List<CategoryDTO> categoryDTOList = backupUserDataDTO.getCategoryDTOList();
        List<Transaction> transactionList = new ArrayList<>();
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getNewAccount(telegramId);
        Long accountId = overMoneyAccount.getId();
        transactionDTOList.stream()
                .filter(transactionDTO -> transactionDTO.getCategoryName() == null)
                .forEach(transactionDTO -> transactionDTO.setCategoryName("Нераспознанное"));
        Set<Category> categorySet = new HashSet<>();
        categoryDTOList.forEach(categoryDTO -> categorySet.add(categoryMapper.mapCategoryDTOToCategory(categoryDTO,
                overMoneyAccount)));

        overMoneyAccountService.deletingAllTransactionsCategoriesKeywordsByAccountId(accountId);
        categoryService.saveAllCategories(categorySet);

        for (TransactionDTO transactionDTO : transactionDTOList) {
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setMessage(transactionDTO.getMessage());
            Optional<CategoryDTO> categoryDTO = categoryService.findCategoryDTOByNameFromList(categoryDTOList,
                    transactionDTO.getCategoryName());
            Category category = transactionProcessingService.getMatchingCategory(categorySet, transactionDTO.getCategoryName());
            categoryDTO.ifPresent(dto -> keywordService.setKeywordsFromCategoryDTO(dto, category, accountId));
            transaction.setCategory(category);
            transaction.setAccount(overMoneyAccount);
            transaction.setDate(transactionDTO.getDate());
            transaction.setTelegramUserId(telegramId);

            transactionList.add(transaction);
        }
        return transactionList;
    }

    public ResponseEntity<InputStreamResource> downloadExelFile(Long id) throws IOException {
        ByteArrayInputStream in = createExcelBackup(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=backup.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    private ByteArrayInputStream createExcelBackup(Long id) throws IOException {
        List<CategoryDTO> categoryDTOList = categoryService.findCategoriesListByChatId(id);
        List<TransactionDTO> transactionDTOList = transactionService.findAlltransactionDTOForAcountByChatId(id);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheetCategories = workbook.createSheet("Категории");
            Sheet sheetTransactionsIncome = workbook.createSheet("Доходы");
            Sheet sheetTransactionsExpense = workbook.createSheet("Расходы");

            // Создание заголовка для столбцов
            String[] columnsCategories = {"ID категории", "Название категории", "Ключевые слова"};
            String[] columnsTransactions = {"Категория", "Сумма", "Сообщение", "Дата", "Пользователь"};
            Row headerRowCategories = sheetCategories.createRow(0);
            for (int i = 0; i < columnsCategories.length; i++) {
                Cell cell = headerRowCategories.createCell(i);
                cell.setCellValue(columnsCategories[i]);
            }
            fillTitle(sheetTransactionsIncome, columnsTransactions);
            fillTitle(sheetTransactionsExpense, columnsTransactions);

            // Заполнение листа данными
            int rowNumCategories = 1;
            for (CategoryDTO category : categoryDTOList) {
                Row row = sheetCategories.createRow(rowNumCategories++);

                String keywords = category.getKeywords().stream()
                        .map(KeywordIdDTO::getName)
                        .collect(Collectors.joining(", "));

                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(keywords);
            }
            fillDataTransactions(Type.INCOME, transactionDTOList, sheetTransactionsIncome);
            fillDataTransactions(Type.EXPENSE, transactionDTOList, sheetTransactionsExpense);

            // Авто-масштабирование столбцов
            for (int i = 0; i < columnsCategories.length; i++) {
                sheetCategories.autoSizeColumn(i);
            }
            for (int i = 0; i < columnsTransactions.length; i++) {
                sheetTransactionsIncome.autoSizeColumn(i);
                sheetTransactionsExpense.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void fillTitle(Sheet sheet, String[] columnsCategories) {
        Row headerRowCategories = sheet.createRow(0);
        for (int i = 0; i < columnsCategories.length; i++) {
            Cell cell = headerRowCategories.createCell(i);
            cell.setCellValue(columnsCategories[i]);
        }
    }

    private void fillDataTransactions(Type type, List<TransactionDTO> transactionDTOList, Sheet sheet) {
        int rowNumTransactions = 1;
        for (TransactionDTO transaction : transactionDTOList) {
            if (transaction.getType() != null && transaction.getType() == type) {
                Row rowExpense = sheet.createRow(rowNumTransactions++);

                rowExpense.createCell(0).setCellValue(transaction.getCategoryName());
                rowExpense.createCell(1).setCellValue(transaction.getAmount());
                rowExpense.createCell(2).setCellValue(transaction.getMessage());
                rowExpense.createCell(3).setCellValue(transaction.getDate().toString());
                rowExpense.createCell(4).setCellValue(transaction.getTelegramUserName());
            }
        }
    }
}


