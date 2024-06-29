package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.TransactionDTO;
import com.override.dto.constants.Type;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportUserDataService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TransactionService transactionService;
    static final int WIDTH = 5837;

    public ResponseEntity<InputStreamResource> downloadExelFile(Long id) throws IOException {
        ByteArrayInputStream in = createExcelExport(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=backup.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    private ByteArrayInputStream createExcelExport(Long id) throws IOException {
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

            setWidth(columnsCategories, sheetCategories);
            setWidth(columnsTransactions, sheetTransactionsIncome);
            setWidth(columnsTransactions, sheetTransactionsExpense);

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
        transactionDTOList = transactionService.enrichTransactionsWithTgUsernames(transactionDTOList);
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

    private void setWidth(String[] columns, Sheet sheet) {
        for (int i = 0; i < columns.length; i++) {
            sheet.setColumnWidth(i, WIDTH);
        }
    }
}
