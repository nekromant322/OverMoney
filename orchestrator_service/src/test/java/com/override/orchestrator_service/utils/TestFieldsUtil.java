package com.override.orchestrator_service.utils;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.dto.KeywordIdDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.*;

import java.time.LocalDateTime;
import java.util.*;

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
}
