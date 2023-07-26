package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.service.calc.*;
import com.override.orchestrator_service.util.TelegramUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.override.dto.TransactionMessageDTO;

import javax.annotation.PostConstruct;
import javax.management.InstanceNotFoundException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionProcessingService {

    @Autowired
    private OverMoneyAccountService overMoneyAccountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecognizerFeign recognizerFeign;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TelegramUtils telegramUtils;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    private static class TransactionDetails {
        private float amount;
        private String message;
    }

    private final List<TransactionHandler> transactionHandlers = new LinkedList<>();

    @PostConstruct
    public void init() {
        transactionHandlers.add(new TransactionHandlerImplSumAmountAtFront());
        transactionHandlers.add(new TransactionHandlerImplSingleAmountAtFront());
        transactionHandlers.add(new TransactionHandlerImplSumAmountAtEnd());
        transactionHandlers.add(new TransactionHandlerImplSingleAmountAtEnd());
        transactionHandlers.add(new TransactionHandlerImplInvalidTransaction());
    }

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(transactionMessageDTO.getChatId());
        String transactionMessage = transactionMessageDTO.getMessage();

        /*List<TransactionHandler> transactionHandlers = new LinkedList<>();
        transactionHandlers.add(new TransactionHandlerImplSumAmountAtFront());
        transactionHandlers.add(new TransactionHandlerImplSingleAmountAtFront());
        transactionHandlers.add(new TransactionHandlerImplSumAmountAtEnd());
        transactionHandlers.add(new TransactionHandlerImplSingleAmountAtEnd());
        transactionHandlers.add(new TransactionHandlerImplInvalidTransaction());*/

        TransactionDetails transactionDetails = new TransactionDetails();
        for (TransactionHandler t : transactionHandlers) {
            Pattern pattern = Pattern.compile(t.getRegExp());
            Matcher matcher = pattern.matcher(transactionMessage);
            if (matcher.find()) {
                transactionDetails.setAmount(t.calculateAmount(transactionMessage));
                transactionDetails.setMessage(t.getTransactionComment(transactionMessage));
                break;
            }
        }

        return Transaction.builder()
                .account(overMoneyAccount)
                .amount(transactionDetails.getAmount())
                .message(transactionDetails.getMessage())
                .category(getTransactionCategory(transactionDetails.getMessage(), overMoneyAccount))
                .date(transactionMessageDTO.getDate())
                .telegramUserId(transactionMessageDTO.getUserId())
                .build();
    }

    /**
     * Проверяет тип транзакции: веб-транзакция или транзакция в Telegram.
     *
     * @param transactionMessageDTO Объект, содержащий информацию о транзакции. Если транзакция
     *                              отправлена из веб-приложения, информация о chatId и userId может
     *                              отсутствовать. Этот метод заполняет эти поля.
     * @param principal             Объект, представляющий текущего пользователя. Присутствует только в
     *                              веб-транзакциях.
     * @return Объект транзакции с заполненными необходимыми полями.
     */
    public Transaction validateAndProcessTransaction(TransactionMessageDTO transactionMessageDTO, Principal principal) throws InstanceNotFoundException {

        if (principal != null) {
            OverMoneyAccount overMoneyAccount = overMoneyAccountService.getAccountByUserId(telegramUtils.getTelegramId(principal));

            transactionMessageDTO.setChatId(overMoneyAccount.getChatId());
            transactionMessageDTO.setUserId(telegramUtils.getTelegramId(principal));
        }

        return processTransaction(transactionMessageDTO);
    }

    public void suggestCategoryToProcessedTransaction(TransactionMessageDTO transactionMessageDTO, UUID transactionId) throws InstanceNotFoundException {
        Transaction transaction = processTransaction(transactionMessageDTO);
        List<CategoryDTO> categories = categoryService.findCategoriesListByUserId(transactionMessageDTO.getUserId());
        executorService.execute(() -> {
            if (!categories.isEmpty()) {
                recognizerFeign.recognizeCategory(transaction.getMessage(), transactionId, categories);
            }
        });
    }

    private Category getTransactionCategory(String transactionMessage,
                                            OverMoneyAccount overMoneyAccount) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) ||
                Objects.isNull(getMatchingCategory(overMoneyAccount.getCategories(),
                        transactionMessage)) &&
                        Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(),
                                transactionMessage))) {
            return null;
        }
        Category matchingCategory = getMatchingCategory(overMoneyAccount.getCategories(),
                transactionMessage);
        if (matchingCategory != null) {
            return matchingCategory;
        }
        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(),
                transactionMessage);
        return matchingKeyword.getCategory();
    }

    public Category getMatchingCategory(Set<Category> categories, String words) {
        Category matchingCategory = null;
        for (Category category : categories) {
            if (words.equalsIgnoreCase(category.getName())) {
                matchingCategory = category;
                break;
            }
        }
        return matchingCategory;
    }

    private Keyword getMatchingKeyword(Set<Category> categories, String words) {
        Keyword matchingKeyword = null;
        outer:
        for (Category category : categories) {
            Set<Keyword> keywordsSet = category.getKeywords();
            for (Keyword keyword : keywordsSet) {
                if (words.equalsIgnoreCase(keyword.getKeywordId().getName())) {
                    matchingKeyword = keyword;
                    break outer;
                }
            }
        }
        return matchingKeyword;
    }
}