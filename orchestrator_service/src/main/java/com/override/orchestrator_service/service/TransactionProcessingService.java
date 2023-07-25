package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.exception.TransactionProcessingException;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.*;
import com.override.orchestrator_service.service.calc.*;
import com.override.orchestrator_service.util.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.override.dto.TransactionMessageDTO;

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

    private enum RegularExpressions {
        SINGLE_AMOUNT_AT_FRONT("^(\\d*(\\,|\\.)?\\d+)(\\s+)([a-zA-Zа-яА-я]+)([a-zA-Zа-яА-я0-9\\s\\.\\,]*)"),
        SUM_AMOUNT_AT_FRONT("^(\\d*(\\,|\\.)?\\d+)((\\s*)(\\+((\\s*)\\d*(\\,|\\.)?\\d+)(\\s+)))+([a-zA-Zа-яА-я]+)([a-zA-Zа-яА-я0-9\\s\\.\\,]*)"),
        SINGLE_AMOUNT_AT_END("^([a-zA-Zа-яА-я]+)([a-zA-Zа-яА-я0-9\\s\\.\\,]*)(\\s+)(\\d*(\\,|\\.)?\\d+)$"),
        SUM_AMOUNT_AT_END("^([a-zA-Zа-яА-я]+)([a-zA-Zа-яА-я0-9\\s\\.\\,]*)(\\s+)(\\d*(\\,|\\.)?\\d+)((\\s*)\\+(\\s*)\\d*(\\,|\\.)?\\d+(\\s*))*$");

        private final String value;

        RegularExpressions(String value) {
            this.value = value;
        }
    }

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(transactionMessageDTO.getChatId());
        String transactionMessage = transactionMessageDTO.getMessage();
        TransactionHandler transactionHandler = getTransactionHandler(transactionMessage.trim());
        return Transaction.builder()
                .account(overMoneyAccount)
                .amount(transactionHandler.calculateAmount(transactionMessage))
                .message(transactionHandler.getTransactionComment(transactionMessage))
                .category(getTransactionCategory(transactionHandler.getTransactionComment(transactionMessage),
                        overMoneyAccount))
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

    private TransactionHandler getTransactionHandler(String transaction) {
        Pattern pattern = Pattern.compile(RegularExpressions.SINGLE_AMOUNT_AT_FRONT.value);
        Matcher matcher = pattern.matcher(transaction);
        if (matcher.find()) {
            return new TransactionHandlerImplSingleAmountAtFront();
        }
        pattern = Pattern.compile(RegularExpressions.SUM_AMOUNT_AT_FRONT.value);
        matcher = pattern.matcher(transaction);
        if (matcher.find()) {
            return new TransactionHandlerImplSumAmountAtFront();
        }
        pattern = Pattern.compile(RegularExpressions.SINGLE_AMOUNT_AT_END.value);
        matcher = pattern.matcher(transaction);
        if (matcher.find()) {
            return new TransactionHandlerImplSingleAmountAtEnd();
        }
        pattern = Pattern.compile(RegularExpressions.SUM_AMOUNT_AT_END.value);
        matcher = pattern.matcher(transaction);
        if (matcher.find()) {
            return new TransactionHandlerImplSumAmountAtEnd();
        }
        throw new TransactionProcessingException("Неподдерживаемый формат транзакции");
    }
}