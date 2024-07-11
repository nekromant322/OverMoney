package com.overmoney.telegram_bot_service.service;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface RequestService {
    TransactionResponseDTO sendTransaction(TransactionMessageDTO transactionMessageDTO)
            throws ExecutionException, InterruptedException, TimeoutException;
}
