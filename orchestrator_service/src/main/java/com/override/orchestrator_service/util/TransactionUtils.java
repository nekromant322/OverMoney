package com.override.orchestrator_service.util;

import com.override.dto.TransactionMessageDTO;
import com.override.orchestrator_service.model.Keyword;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.repository.TransactionRepository;
import com.override.orchestrator_service.service.KeywordService;
import com.override.orchestrator_service.service.OverMoneyAccountService;
import com.override.orchestrator_service.service.TransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class TransactionUtils {

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    TransactionProcessingService transactionProcessingService;

    private  final int MAX_TRANSACTION_RANDOM_AMOUNT = 10000;
    private  final int MIN_TRANSACTION_RANDOM_AMOUNT = 100;
    private  final int MAX_TRANSACTION_SECOND_RANDOM_AMOUNT = 1000;
    private  final int MIN_TRANSACTION_SECOND_RANDOM_AMOUNT = 500;
    private  final int YEARS_NUMBER = 3;
    private  final double SMALLER_TRANSACTION_PORTION = 0.25d;
    private  final double LARGER_TRANSACTION_PORTION = 0.75d;

    public void generateRandomTransactionsByPortion(long telegramId, int totalTransactionsCount) throws InstanceNotFoundException {
        generateRandomTransactions(SMALLER_TRANSACTION_PORTION, MIN_TRANSACTION_RANDOM_AMOUNT,
                MAX_TRANSACTION_RANDOM_AMOUNT, telegramId, totalTransactionsCount);
        generateRandomTransactions(LARGER_TRANSACTION_PORTION, MIN_TRANSACTION_SECOND_RANDOM_AMOUNT,
                MAX_TRANSACTION_SECOND_RANDOM_AMOUNT, telegramId, totalTransactionsCount);
    }

    void generateRandomTransactions(double transactionPortion, int minRandomAmount, int maxRandomAmount,
                                    long telegramId, int totalTransactionsCount) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService.getAccountByUserId(telegramId);
        List<Keyword> accountKeyWords = keywordService.findAllByOverMoneyAccount(overMoneyAccount);
        Random random = new Random();
        LocalDateTime periodStart = LocalDateTime.now().minusYears(YEARS_NUMBER);
        LocalDateTime periodEnd = LocalDateTime.now();

        for (int i = 0; i < totalTransactionsCount * transactionPortion; i++) {
            int amount = random.nextInt(maxRandomAmount - minRandomAmount + 1) + minRandomAmount;
            String keyword = accountKeyWords.get(random.nextInt(accountKeyWords.size())).getKeywordId().getName();
            String message = keyword + " " + amount;
            int randomSeconds = random.nextInt((int) periodStart.until(periodEnd, ChronoUnit.SECONDS));
            LocalDateTime anyTime = periodEnd.minusSeconds(randomSeconds);
            TransactionMessageDTO transactionMessageDTO = new TransactionMessageDTO(message,
                    telegramId, overMoneyAccount.getChatId(), anyTime);
            transactionRepository.save(transactionProcessingService.processTransaction(transactionMessageDTO));
        }
    }
}
