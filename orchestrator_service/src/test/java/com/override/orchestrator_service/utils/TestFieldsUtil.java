package com.override.orchestrator_service.utils;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class TestFieldsUtil {

    public static BugReport generateTestBugReport() {
        return BugReport.builder()
                .userId(123L)
                .report("test")
                .localDateTime(LocalDateTime.now())
                .id(123L)
                .build();
    }

    public static BugReportDTO generateTestBugReportDTO() {
        return BugReportDTO.builder()
                .userId(123L)
                .report("test")
                .localDateTime(LocalDateTime.now())
                .id(123L)
                .build();
    }

    public static Keyword generateTestKeyword() {
        return Keyword.builder()
                .keywordId(new KeywordId("пиво", 123L))
                .category(Category.builder()
                        .id(12345L)
                        .name("продукты")
                        .type(Type.EXPENSE)
                        .account(OverMoneyAccount
                                .builder()
                                .id(123L)
                                .build())
                        .build())
                .build();
    }

    public static KeywordIdDTO generateTestKeywordIdDTO() {
        return KeywordIdDTO.builder()
                .accountId(123L)
                .name("Тест")
                .build();
    }

    public static Category generateTestCategory() {
        Set<Keyword> keywordSet = new HashSet<>();
        keywordSet.add(generateTestKeyword());

        Category category = Category.builder()
                .id(12345L)
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordSet)
                .build();
        category.setAccount(generateTestAccountNoCategory());
        return category;
    }

    public static Transaction generateTestTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .message("продукты")
                .amount(200F)
                .date(LocalDateTime.now())
                .category(generateTestCategory())
                .account(generateTestAccount())
                .build();
    }


    public static CategoryDTO generateTestCategoryDTO() {
        List<KeywordIdDTO> keywordIdDTOList = List.of(generateTestKeywordIdDTO());
        return CategoryDTO.builder()
                .id(12345L)
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordIdDTOList)
                .build();
    }

    public static TransactionDTO generateTestTransactionDTO() {
        return TransactionDTO.builder()
                .message("продукты")
                .amount(200F)
                .date(LocalDateTime.now())
                .categoryName(generateTestCategory().getName())
                .build();
    }

    public static OverMoneyAccount generateTestAccount() {
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(generateTestCategory());

        Set<User> userSet = new HashSet<>();
        userSet.add(generateTestUser());

        return OverMoneyAccount.builder()
                .id(1L)
                .chatId(404723191L)
                .categories(categorySet)
                .users(userSet)
                .build();
    }

    public static OverMoneyAccount generateTestAccountNoCategory() {
        Set<User> userSet = new HashSet<>();
        userSet.add(generateTestUser());

        return OverMoneyAccount.builder()
                .id(1L)
                .chatId(404723191L)
                .users(userSet)
                .build();
    }

    public static TelegramAuthRequest generateTestAuthRequest() {
        return TelegramAuthRequest.builder()
                .id(123L)
                .first_name("Vasilii")
                .last_name("Matveev")
                .username("kyomexd")
                .photo_url("")
                .auth_date("")
                .hash("")
                .build();
    }

    public static User generateTestUser() {
        return User.builder()
                .id(123L)
                .firstName("Vasilii")
                .lastName("Matveev")
                .username("kyomexd")
                .photoUrl("")
                .authDate("")
                .build();
    }

    public static List<AnalyticsMonthlyReportForYearDTO> generateTestListOfAnalyticsMonthlyReportForYearDTOMixed() {
        return List.of(generateTestAnalyticsMonthlyReportForYearDTOWithNullFields(),
                generateTestAnalyticsMonthlyReportForYearDTOWithoutNullFields());
    }

    public static List<AnalyticsMonthlyReportForYearDTO> generateTestListOfAnalyticsMonthlyReportForYearDTOWithNull() {
        return List.of(generateTestAnalyticsMonthlyReportForYearDTOWithNullFields());
    }

    public static List<AnalyticsMonthlyReportForYearDTO> generateTestListOfAnalyticsMonthlyReportForYearDTOWithoutNull() {
        return List.of(generateTestAnalyticsMonthlyReportForYearDTOWithoutNullFields());
    }

    public static AnalyticsMonthlyReportForYearDTO generateTestAnalyticsMonthlyReportForYearDTOWithNullFields() {
        AnalyticsMonthlyReportForYearDTO dto1 = new AnalyticsMonthlyReportForYearDTO("categoryWithNullFields",
                new HashMap<>() {{
                    put(1, 10000d);
                    put(2, 20000d);
                    put(3, 30000d);
                    put(4, 30000d);
                    put(5, 0d);
                    put(6, 0d);
                    put(7, 0d);
                    put(8, 40000d);
                    put(9, 30000d);
                    put(10, 10000d);
                    put(11, 20000d);
                    put(12, 50000d);
                }});
        return dto1;
    }

    public static AnalyticsMonthlyReportForYearDTO generateTestAnalyticsMonthlyReportForYearDTOWithoutNullFields() {
        AnalyticsMonthlyReportForYearDTO dto1 = new AnalyticsMonthlyReportForYearDTO("categoryWithoutNullFields",
                new HashMap<>() {{
                    put(1, 10000d);
                    put(2, 20000d);
                    put(3, 30000d);
                    put(4, 30000d);
                    put(5, 20000d);
                    put(6, 40000d);
                    put(7, 10000d);
                    put(8, 40000d);
                    put(9, 30000d);
                    put(10, 10000d);
                    put(11, 20000d);
                    put(12, 50000d);
                }});
        return dto1;
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithoutNullFields() {
        return List.of(new AnalyticsMonthlyIncomeForCategoryDTO(10000d, "categoryWithoutNullFields", 1),
                new AnalyticsMonthlyIncomeForCategoryDTO(20000d, "categoryWithoutNullFields", 2),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithoutNullFields", 3),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithoutNullFields", 4),
                new AnalyticsMonthlyIncomeForCategoryDTO(20000d, "categoryWithoutNullFields", 5),
                new AnalyticsMonthlyIncomeForCategoryDTO(40000d, "categoryWithoutNullFields", 6),
                new AnalyticsMonthlyIncomeForCategoryDTO(10000d, "categoryWithoutNullFields", 7),
                new AnalyticsMonthlyIncomeForCategoryDTO(40000d, "categoryWithoutNullFields", 8),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithoutNullFields", 9),
                new AnalyticsMonthlyIncomeForCategoryDTO(10000d, "categoryWithoutNullFields", 10),
                new AnalyticsMonthlyIncomeForCategoryDTO(20000d, "categoryWithoutNullFields", 11),
                new AnalyticsMonthlyIncomeForCategoryDTO(50000d, "categoryWithoutNullFields", 12));
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithNullFields() {
        return List.of(new AnalyticsMonthlyIncomeForCategoryDTO(10000d, "categoryWithNullFields", 1),
                new AnalyticsMonthlyIncomeForCategoryDTO(20000d, "categoryWithNullFields", 2),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithNullFields", 3),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithNullFields", 4),
                new AnalyticsMonthlyIncomeForCategoryDTO(40000d, "categoryWithNullFields", 8),
                new AnalyticsMonthlyIncomeForCategoryDTO(30000d, "categoryWithNullFields", 9),
                new AnalyticsMonthlyIncomeForCategoryDTO(10000d, "categoryWithNullFields", 10),
                new AnalyticsMonthlyIncomeForCategoryDTO(20000d, "categoryWithNullFields", 11),
                new AnalyticsMonthlyIncomeForCategoryDTO(50000d, "categoryWithNullFields", 12));
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithMixedFields() {
        List<AnalyticsMonthlyIncomeForCategoryDTO> result = new ArrayList<>(generateTestAnalyticsMonthlyIncomeForCategoryWithNullFields());
        result.addAll(generateTestAnalyticsMonthlyIncomeForCategoryWithoutNullFields());
        return result;
    }

    public static BackupUserDataDTO generateTestBackupUserDataDTO() {
        List<TransactionDTO> transactionList = new ArrayList<>();
        TransactionDTO transactionDTO = TestFieldsUtil.generateTestTransactionDTO();
        transactionList.add(transactionDTO);
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        CategoryDTO categoryDTO = TestFieldsUtil.generateTestCategoryDTO();
        categoryDTOList.add(categoryDTO);
        return BackupUserDataDTO.builder()
                .transactionDTOList(transactionList)
                .categoryDTOList(categoryDTOList)
                .build();
    }

    public static List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> generateTestAnalyticsAnnualAndMonthlyExpenseForCategoryWithNullFields() {
        return List.of(new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(10000d, "categoryWithNullFields", 1),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(20000d, "categoryWithNullFields", 2),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithNullFields", 3),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithNullFields", 4),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(40000d, "categoryWithNullFields", 8),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithNullFields", 9),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(10000d, "categoryWithNullFields", 10),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(20000d, "categoryWithNullFields", 11),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(50000d, "categoryWithNullFields", 12));
    }

    public static List<AnalyticsAnnualAndMonthlyExpenseForCategoryDTO> generateTestAnalyticsAnnualAndMonthlyExpenseForCategoryWithoutNullFields() {
        return List.of(new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(10000d, "categoryWithoutNullFields", 1),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(20000d, "categoryWithoutNullFields", 2),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithoutNullFields", 3),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithoutNullFields", 4),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(20000d, "categoryWithoutNullFields", 5),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(40000d, "categoryWithoutNullFields", 6),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(10000d, "categoryWithoutNullFields", 7),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(40000d, "categoryWithoutNullFields", 8),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(30000d, "categoryWithoutNullFields", 9),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(10000d, "categoryWithoutNullFields", 10),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(20000d, "categoryWithoutNullFields", 11),
                new AnalyticsAnnualAndMonthlyExpenseForCategoryDTO(50000d, "categoryWithoutNullFields", 12));
    }
    public static List<AnalyticsAnnualAndMonthlyReportDTO> generateTestListOfAnalyticsAnnualAndMonthlyReportDTOWithNull() {
        return List.of(generateTestAnalyticsAnnualAndMonthlyReportDTOWithNullFields());
    }
    public static List<AnalyticsAnnualAndMonthlyReportDTO> generateTestListOfAAnalyticsAnnualAndMonthlyReportDTOWithoutNull() {
        return List.of(generateTestAnalyticsAnnualAndMonthlyReportDTOWithoutNullFields());
    }


    public static AnalyticsAnnualAndMonthlyReportDTO generateTestAnalyticsAnnualAndMonthlyReportDTOWithNullFields() {
        return new AnalyticsAnnualAndMonthlyReportDTO("categoryWithNullFields",
                new HashMap<>() {{
                    put(1, 10000d);
                    put(2, 20000d);
                    put(3, 30000d);
                    put(4, 30000d);
                    put(5, 0d);
                    put(6, 0d);
                    put(7, 0d);
                    put(8, 40000d);
                    put(9, 30000d);
                    put(10, 10000d);
                    put(11, 20000d);
                    put(12, 50000d);
                }},
                new HashMap<>() {{
                    put(1, 1d);
                    put(2, 1d);
                    put(3, 1d);
                    put(4, 1d);
                    put(5, 0d);
                    put(6, 0d);
                    put(7, 0d);
                    put(8, 1d);
                    put(9, 1d);
                    put(10, 1d);
                    put(11, 1d);
                    put(12, 1d);
                }});
    }

    public static AnalyticsAnnualAndMonthlyReportDTO generateTestAnalyticsAnnualAndMonthlyReportDTOWithoutNullFields() {
        return new AnalyticsAnnualAndMonthlyReportDTO("categoryWithoutNullFields",
                new HashMap<>() {{
                    put(1, 10000d);
                    put(2, 20000d);
                    put(3, 30000d);
                    put(4, 30000d);
                    put(5, 20000d);
                    put(6, 40000d);
                    put(7, 10000d);
                    put(8, 40000d);
                    put(9, 30000d);
                    put(10, 10000d);
                    put(11, 20000d);
                    put(12, 50000d);
                }},
                new HashMap<>() {{
                    put(1, 1d);
                    put(2, 1d);
                    put(3, 1d);
                    put(4, 1d);
                    put(5, 1d);
                    put(6, 1d);
                    put(7, 1d);
                    put(8, 1d);
                    put(9, 1d);
                    put(10, 1d);
                    put(11, 1d);
                    put(12, 1d);
                }});
    }
}
