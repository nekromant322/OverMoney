package com.override.orchestrator_service.service;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.model.*;
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

    private enum AmountPositionType {
        AMOUNT_AT_BEGINNING,
        AMOUNT_AT_END,
        AMOUNT_AT_BEGINNING_RU_LOCALE,
        AMOUNT_AT_END_RU_LOCALE,
    }

    private enum RegularExpressions {
        SPACE(' '),
        RU_DECIMAL_DELIMITER(','),
        EN_DECIMAL_DELIMITER('.');

        private final char value;

        RegularExpressions(char value) {
            this.value = value;
        }
    }

    public Transaction processTransaction(TransactionMessageDTO transactionMessageDTO) throws InstanceNotFoundException {
        OverMoneyAccount overMoneyAccount = overMoneyAccountService
                .getOverMoneyAccountByChatId(transactionMessageDTO.getChatId());
        String transactionMessage = transactionMessageDTO.getMessage();
        if (transactionMessage.indexOf("+") != -1) {
            transactionMessage = getCalculatedTransaction(transactionMessage.trim());
        }
        AmountPositionType type = checkTransactionType(transactionMessage);

        return Transaction.builder()
                .account(overMoneyAccount)
                .amount(getAmount(transactionMessage, type))
                .message(getWords(transactionMessage, type))
                .category(getTransactionCategory(transactionMessageDTO, overMoneyAccount, type))
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

    private AmountPositionType checkTransactionType(String transactionMessage) throws InstanceNotFoundException {
        int firstIndexOfSpace = transactionMessage.indexOf(RegularExpressions.SPACE.value);
        int lastIndexOfSpace = transactionMessage.lastIndexOf(RegularExpressions.SPACE.value);
        if (firstIndexOfSpace == -1 || lastIndexOfSpace == -1) {
            throw new InstanceNotFoundException("Invalid message");
        }
        String firstWord = transactionMessage.substring(0, firstIndexOfSpace);
        String lastWord = transactionMessage.substring(lastIndexOfSpace + 1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstWord)
                .append(RegularExpressions.SPACE.value)
                .append(firstWord.replace(RegularExpressions.RU_DECIMAL_DELIMITER.value,
                        RegularExpressions.EN_DECIMAL_DELIMITER.value))
                .append(RegularExpressions.SPACE.value)
                .append(lastWord)
                .append(RegularExpressions.SPACE.value)
                .append(lastWord.replace(RegularExpressions.RU_DECIMAL_DELIMITER.value,
                        RegularExpressions.EN_DECIMAL_DELIMITER.value))
                .append(RegularExpressions.SPACE.value);
        Scanner scanner = new Scanner(stringBuilder.toString());
        scanner.useLocale(Locale.ENGLISH);
        if (scanner.hasNextFloat()) {
            return AmountPositionType.AMOUNT_AT_BEGINNING;
        } else {
            scanner.next();
        }
        if (scanner.hasNextFloat()) {
            return AmountPositionType.AMOUNT_AT_BEGINNING_RU_LOCALE;
        } else {
            scanner.next();
        }
        if (scanner.hasNextFloat()) {
            return AmountPositionType.AMOUNT_AT_END;
        } else {
            scanner.next();
        }
        if (scanner.hasNextFloat()) {
            return AmountPositionType.AMOUNT_AT_END_RU_LOCALE;
        } else {
            scanner.next();
        }
        scanner.close();

        throw new InstanceNotFoundException("Invalid message");
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

    private Category getTransactionCategory(TransactionMessageDTO transactionMessageDTO,
                                            OverMoneyAccount overMoneyAccount,
                                            AmountPositionType amountPositionType) throws InstanceNotFoundException {
        if (Objects.isNull(overMoneyAccount.getCategories()) ||
                Objects.isNull(getMatchingCategory(overMoneyAccount.getCategories(),
                        getWords(transactionMessageDTO.getMessage(), amountPositionType))) &&
                        Objects.isNull(getMatchingKeyword(overMoneyAccount.getCategories(),
                                getWords(transactionMessageDTO.getMessage(), amountPositionType)))) {
            return null;
        }
        Category matchingCategory = getMatchingCategory(overMoneyAccount.getCategories(),
                getWords(transactionMessageDTO.getMessage(), amountPositionType));
        if (matchingCategory != null) {
            return matchingCategory;
        }
        Keyword matchingKeyword = getMatchingKeyword(overMoneyAccount.getCategories(),
                getWords(transactionMessageDTO.getMessage(), amountPositionType));
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

    private String getWords(String message, AmountPositionType type) throws InstanceNotFoundException {

        String words;
        int firstIndexOfSpace;
        int lastIndexOfSpace;
        switch (type) {
            case AMOUNT_AT_BEGINNING:
            case AMOUNT_AT_BEGINNING_RU_LOCALE:
                firstIndexOfSpace = message.indexOf(RegularExpressions.SPACE.value);
                words = message.substring(firstIndexOfSpace + 1);
                return words;
            case AMOUNT_AT_END:
            case AMOUNT_AT_END_RU_LOCALE:
                lastIndexOfSpace = message.lastIndexOf(RegularExpressions.SPACE.value);
                words = message.substring(0, lastIndexOfSpace);
                return words;
            default:
                throw new InstanceNotFoundException("No keywords present in the message");
        }
    }

    private Float getAmount(String message, AmountPositionType type) throws InstanceNotFoundException {
        String amountAsString;
        int firstIndexOfSpace;
        int lastIndexOfSpace;
        switch (type) {
            case AMOUNT_AT_BEGINNING:
                firstIndexOfSpace = message.indexOf(RegularExpressions.SPACE.value);
                amountAsString = message.substring(0, firstIndexOfSpace);
                return Float.parseFloat(amountAsString);
            case AMOUNT_AT_END:
                lastIndexOfSpace = message.lastIndexOf(RegularExpressions.SPACE.value);
                amountAsString = message.substring(lastIndexOfSpace + 1);
                return Float.parseFloat(amountAsString);
            case AMOUNT_AT_BEGINNING_RU_LOCALE:
                firstIndexOfSpace = message.indexOf(RegularExpressions.SPACE.value);
                amountAsString = message.substring(0, firstIndexOfSpace);
                return Float.parseFloat(amountAsString.replace(RegularExpressions.RU_DECIMAL_DELIMITER.value,
                        RegularExpressions.EN_DECIMAL_DELIMITER.value));
            case AMOUNT_AT_END_RU_LOCALE:
                lastIndexOfSpace = message.lastIndexOf(RegularExpressions.SPACE.value);
                amountAsString = message.substring(lastIndexOfSpace + 1);
                return Float.parseFloat(amountAsString.replace(RegularExpressions.RU_DECIMAL_DELIMITER.value,
                        RegularExpressions.EN_DECIMAL_DELIMITER.value));
            default:
                throw new InstanceNotFoundException("No amount stated");
        }
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

    private String getCalculatedTransaction(String transactionMessage) throws InstanceNotFoundException {
        String substringOfExpression = getSubstringOfExpression(transactionMessage);
        String regexOfExpression = createRegexForExpression(substringOfExpression);
        String elementsOfSum = processingOfExpression(substringOfExpression);
        float sumOfTransaction = calculateSum(elementsOfSum);
        return transactionMessage.replaceAll(regexOfExpression, String.format("%.2f", sumOfTransaction));
    }

    private String getSubstringOfExpression(String str) throws InstanceNotFoundException {
        Pattern pattern = Pattern.compile("[A-Za-zА-Яа-я]+");
        Matcher matcher = pattern.matcher(str);
        String expression;
        if (matcher.find()) {
            if (matcher.end() != str.length() && matcher.start() > 0) {
                expression = str.substring(0, matcher.start()).replaceAll("[^0-9\\,\\.\\+\\s]", "");
            } else {
                Pattern pattern2 = Pattern.compile("\\d*[.,]?\\d*\\s*\\+");
                Matcher matcher2 = pattern2.matcher(str);
                matcher2.find();
                expression = str.substring(matcher2.start()).replaceAll("[^0-9\\,\\.\\+\\s]", "");
            }
            if (expression.indexOf("-") == -1
                    && expression.indexOf("*") == -1
                    && expression.indexOf("/") == -1) {
                return expression.trim();
            }
        }
        throw new InstanceNotFoundException("Invalid message");
    }

    public String processingOfExpression(String rowExpression) {
        return rowExpression
                .replaceAll("\\,", ".")
                .replaceAll("[^0-9\\.]", " ");
    }

    public String createRegexForExpression(String rowSubstring) {
        return rowSubstring.replaceAll("\\s", "\\\\s").replaceAll("\\+", "\\\\+");
    }

    public float calculateSum(String expression) {
        Scanner sc = new Scanner(expression);
        sc.useLocale(Locale.ENGLISH);
        float sum = 0;
        while (sc.hasNext()) {
            if (sc.hasNextFloat()) {
                sum += sc.nextFloat();
            } else {
                sc.next();
            }
        }
        return sum;
    }
}