package com.override.orchestrator_service.controller.rest;

import com.override.orchestrator_service.service.TransactionXLSXService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Slf4j
@RestController
@Tag(name = "Контроллер загрузки транзакций из XLSX", description = "Операции по загрузке транзакций из XLSX файлов")
public class TransactionFromXLSXController {
    @Autowired
    private TransactionXLSXService transactionXLSXService;

    @GetMapping("/loading/transactions")
    @Operation(summary = "Загрузка транзакций из XLSX", description = "Загружает транзакции из данного XLSX файла")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции загружены"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат файла"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<String> loadingTransactionsFromXLSX(Principal principal,
                                                              @Parameter(description = "XLSX файл с транзакциями")
                                                              @RequestPart("data") MultipartFile file) {
        return transactionXLSXService.loadingTransactionsFromXLSX(principal, file);
    }
}
