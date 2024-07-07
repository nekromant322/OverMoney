package com.override.orchestrator_service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.orchestrator_service.filter.TransactionFilter;
import com.override.dto.TransactionFilterDTO;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.repository.CategoryRepository;
import com.override.orchestrator_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionFilterMapper {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;

    public TransactionFilter mapStringJsonToTransactionFilter(String filterJson, Long accID) {
        TransactionFilterDTO filterDTO;
        try {
            filterDTO = objectMapper.readValue(filterJson, TransactionFilterDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        TransactionFilter filter = new TransactionFilter();

        if (filterDTO == null) {
            return filter;
        }
        if (filterDTO.getCategory() != null) {
            Category category = categoryRepository.findCategoryByNameAndAccountId(accID, filterDTO.getCategory());
            filter.setCategory(category);
        }

        if (filterDTO.getAmount() != null) {
            filter.setAmount(filterDTO.getAmount());
        }

        if (filterDTO.getMessage() != null) {
            filter.setMessage(filterDTO.getMessage());
        }

        if (filterDTO.getDate() != null) {
            filter.setDate(filterDTO.getDate());
        }

        List<Long> telegramUsersId = new ArrayList<>();

        try {
            List<String> telegramUserNames = filterDTO.getTelegramUserNameList();
            for (String telegramUserName : telegramUserNames) {
                telegramUsersId.add(userService.getUserByUsername(telegramUserName).getId());
            }
            filter.setTelegramUsersId(telegramUsersId);
        } catch (NullPointerException e) {
            return filter;
        }
        return filter;
    }
}
