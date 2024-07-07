package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionDefineDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.DefineService;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import com.override.orchestrator_service.util.TelegramUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Контроллер транзакций", description = "Операции, связанные с транзакциями")
public class TransactionController {

    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TelegramUtils telegramUtils;

    @Autowired
    private DefineService defineService;

    @GetMapping("/transactions/count")
    @Operation(summary = "Получить количество транзакций", description = "Возвращает общее количество транзакций")
    public int getTransactionsCount() {
        return transactionService.getTransactionsCount();
    }

    @PostMapping("/transaction")
    @Operation(summary = "Обработать транзакцию", description = "Обрабатывает транзакцию и сохраняет её")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные транзакции")
    })
    public TransactionResponseDTO processTransaction(
            @Parameter(description = "Данные транзакции") @RequestBody TransactionMessageDTO transactionMessage,
            Principal principal) throws InstanceNotFoundException {
        Transaction transaction = transactionProcessingService.validateAndProcessTransaction(transactionMessage, principal);
        transactionService.saveTransaction(transaction);
        transactionProcessingService.suggestCategoryToProcessedTransaction(transactionMessage, transaction.getId());
        return transactionMapper.mapTransactionToTelegramResponse(transaction);
    }

    @GetMapping("/transactions")
    @Operation(summary = "Получить список транзакций", description = "Возвращает список всех транзакций пользователя без категорий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public List<TransactionDTO> getTransactionsList(Principal principal) throws InstanceNotFoundException {
        List<Transaction> transactions =
                transactionService.findTransactionsListByUserIdWithoutCategories(telegramUtils.getTelegramId(principal));
        transactions.sort(Comparator.comparing(Transaction::getDate));

        return transactions.stream()
                .map(transaction -> transactionMapper.mapTransactionToDTO(transaction))
                .collect(Collectors.toList());
    }

    @GetMapping("/transactions/info")
    @Operation(summary = "Получить список транзакций по периоду и категории", description = "Возвращает список транзакций за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректные данные периода или категории")
    })
    public List<TransactionDTO> getTransactionsListByPeriodAndCategory(
            @Parameter(description = "Год транзакции") @RequestParam Integer year,
            @Parameter(description = "Месяц транзакции") @RequestParam Integer month,
            @Parameter(description = "ID категории транзакции") @RequestParam long categoryId) {
        return transactionService.getTransactionsListByPeriodAndCategory(year, month, categoryId);
    }

    @GetMapping("/transactions/history")
    @Operation(summary = "Получить историю транзакций", description = "Возвращает историю транзакций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public List<TransactionDTO> getTransactionsHistory(Principal principal,
                                                       @Parameter(description = "Количество транзакций на странице") @RequestParam(defaultValue = "50") Integer pageSize,
                                                       @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") Integer pageNumber)
            throws InstanceNotFoundException {
        return transactionService
                .findTransactionsByUserIdLimited(telegramUtils.getTelegramId(principal), pageSize, pageNumber);
    }

    @PostMapping("/transaction/define")
    @Operation(summary = "Установить категорию транзакции", description = "Устанавливает категорию для указанной транзакции по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректные данные для определения категории")
    })
    public ResponseEntity<String> define(
            @Parameter(description = "Данные для определения категории транзакции") @RequestBody TransactionDefineDTO transactionDefineDTO) {
        defineService.defineTransactionCategoryByTransactionIdAndCategoryId(transactionDefineDTO.getTransactionId(),
                transactionDefineDTO.getCategoryId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transaction/undefine")
    @Operation(summary = "Удаляет категорию транзакции", description = "Снимает с транзакции категорию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректные данные для удаления категории")
    })
    public ResponseEntity<String> undefine(
            @Parameter(description = "Данные для снятия категории с транзакции") @RequestBody TransactionDefineDTO transactionDefineDTO) {
        defineService.undefineTransactionCategoryAndKeywordCategory(transactionDefineDTO.getTransactionId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/transaction")
    @Operation(summary = "Редактировать транзакцию", description = "Редактирует транзакцию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректные данные транзакции")
    })
    public ResponseEntity<String> editTransaction(
            @Parameter(description = "Данные транзакции") @RequestBody TransactionDTO transactionDTO) {
        transactionService.saveTransaction(transactionService.enrichTransactionWithSuggestedCategory(transactionDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/history/{id}")
    @Operation(summary = "Получить транзакцию по ID", description = "Возвращает транзакцию по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    public TransactionDTO getTransactionById(
            @Parameter(description = "ID транзакции") @PathVariable("id") UUID id) {
        return transactionMapper.mapTransactionToDTO(transactionService.getTransactionById(id));
    }

    @PutMapping("/transactions")
    @Operation(summary = "Обновить транзакцию", description = "Обновляет существующую транзакцию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректные данные транзакции")
    })
    public void updateTransaction(
            @Parameter(description = "Данные транзакции") @RequestBody TransactionDTO transactionDTO) {
        transactionService.editTransaction(transactionDTO);
    }

    @DeleteMapping("/transaction/{id}")
    @Operation(summary = "Удалить транзакцию по ID", description = "Удаляет транзакцию по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    public void deleteTransactionById(
            @Parameter(description = "ID транзакции") @PathVariable("id") UUID id) {
        transactionService.deleteTransactionById(id);
    }

    @PatchMapping("/transaction/update/{id}")
    @Operation(summary = "Частично обновить транзакцию", description = "Обновляет поля message и amount у транзакции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные транзакции")
    })
    public TransactionResponseDTO patchTransaction(
            @Parameter(description = "Данные транзакции") @RequestBody TransactionMessageDTO transactionMessage,
            @Parameter(description = "ID транзакции") @PathVariable("id") UUID id) throws InstanceNotFoundException {
        return transactionService.patchTransaction(transactionMessage, id);
    }
}