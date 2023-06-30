package com.override.orchestrator_service.utils;

import com.override.dto.*;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.http.client.methods.RequestBuilder.put;

public class TestFieldsUtil {

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

        return Category.builder()
                .id(12345L)
                .name("продукты")
                .type(Type.EXPENSE)
                .keywords(keywordSet)
                .build();
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
                    put(1, 1d);
                    put(2, 2d);
                    put(3, 3d);
                    put(4, 4d);
                    put(5, 0d);
                    put(6, 0d);
                    put(7, 0d);
                    put(8, 8d);
                    put(9, 9d);
                    put(10, 10d);
                    put(11, 11d);
                    put(12, 12d);
                }});
        return dto1;
    }

    public static AnalyticsMonthlyReportForYearDTO generateTestAnalyticsMonthlyReportForYearDTOWithoutNullFields() {
        AnalyticsMonthlyReportForYearDTO dto1 = new AnalyticsMonthlyReportForYearDTO("categoryWithoutNullFields",
                new HashMap<>() {{
                    put(1, 1d);
                    put(2, 2d);
                    put(3, 3d);
                    put(4, 4d);
                    put(5, 5d);
                    put(6, 6d);
                    put(7, 7d);
                    put(8, 8d);
                    put(9, 9d);
                    put(10, 10d);
                    put(11, 11d);
                    put(12, 12d);
                }});
        return dto1;
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithoutNullFields() {
        return List.of(new AnalyticsMonthlyIncomeForCategoryDTO(1d, "categoryWithoutNullFields", 1),
                new AnalyticsMonthlyIncomeForCategoryDTO(2d, "categoryWithoutNullFields", 2),
                new AnalyticsMonthlyIncomeForCategoryDTO(3d, "categoryWithoutNullFields", 3),
                new AnalyticsMonthlyIncomeForCategoryDTO(4d, "categoryWithoutNullFields", 4),
                new AnalyticsMonthlyIncomeForCategoryDTO(5d, "categoryWithoutNullFields", 5),
                new AnalyticsMonthlyIncomeForCategoryDTO(6d, "categoryWithoutNullFields", 6),
                new AnalyticsMonthlyIncomeForCategoryDTO(7d, "categoryWithoutNullFields", 7),
                new AnalyticsMonthlyIncomeForCategoryDTO(8d, "categoryWithoutNullFields", 8),
                new AnalyticsMonthlyIncomeForCategoryDTO(9d, "categoryWithoutNullFields", 9),
                new AnalyticsMonthlyIncomeForCategoryDTO(10d, "categoryWithoutNullFields", 10),
                new AnalyticsMonthlyIncomeForCategoryDTO(11d, "categoryWithoutNullFields", 11),
                new AnalyticsMonthlyIncomeForCategoryDTO(12d, "categoryWithoutNullFields", 12));
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithNullFields() {
        return List.of(new AnalyticsMonthlyIncomeForCategoryDTO(1d, "categoryWithNullFields", 1),
                new AnalyticsMonthlyIncomeForCategoryDTO(2d, "categoryWithNullFields", 2),
                new AnalyticsMonthlyIncomeForCategoryDTO(3d, "categoryWithNullFields", 3),
                new AnalyticsMonthlyIncomeForCategoryDTO(4d, "categoryWithNullFields", 4),
                new AnalyticsMonthlyIncomeForCategoryDTO(8d, "categoryWithNullFields", 8),
                new AnalyticsMonthlyIncomeForCategoryDTO(9d, "categoryWithNullFields", 9),
                new AnalyticsMonthlyIncomeForCategoryDTO(10d, "categoryWithNullFields", 10),
                new AnalyticsMonthlyIncomeForCategoryDTO(11d, "categoryWithNullFields", 11),
                new AnalyticsMonthlyIncomeForCategoryDTO(12d, "categoryWithNullFields", 12));
    }

    public static List<AnalyticsMonthlyIncomeForCategoryDTO> generateTestAnalyticsMonthlyIncomeForCategoryWithMixedFields() {
        List<AnalyticsMonthlyIncomeForCategoryDTO> result = new ArrayList<>(generateTestAnalyticsMonthlyIncomeForCategoryWithNullFields());
        result.addAll(generateTestAnalyticsMonthlyIncomeForCategoryWithoutNullFields());
        return result;
    }




}
