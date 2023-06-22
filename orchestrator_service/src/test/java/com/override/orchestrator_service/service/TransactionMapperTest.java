package com.override.orchestrator_service.service;

import com.override.dto.TransactionDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    @Test
    public void mapTransactionToDTOTest() {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        TransactionDTO transactionDTO = transactionMapper.mapTransactionToDTO(transaction);
        Assertions.assertEquals(transactionDTO.getId(), transaction.getId());
        Assertions.assertEquals(transactionDTO.getMessage(), transaction.getMessage());
        Assertions.assertEquals(transactionDTO.getDate(), transaction.getDate());
        Assertions.assertEquals(transactionDTO.getCategoryName(), transaction.getCategory().getName());
        Assertions.assertEquals(transactionDTO.getTelegramUserId(), transaction.getTelegramUserId());
        Assertions.assertEquals(transactionDTO.getSuggestedCategoryId(), transaction.getSuggestedCategoryId());
        Assertions.assertEquals(transactionDTO.getAmount(), transaction.getAmount());
    }

    @Test
    public void mapTransactionToTelegramResponseWhenCategoryUndefined() throws InstanceNotFoundException {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.setCategory(null);

        TransactionResponseDTO transactionResponseDTO = transactionMapper.mapTransactionToTelegramResponse(transaction);
        Assertions.assertEquals(transactionResponseDTO.getType(), Type.EXPENSE.getValue());
        Assertions.assertEquals(transactionResponseDTO.getComment(), transaction.getMessage());
        Assertions.assertEquals(transactionResponseDTO.getCategory(), "Нераспознанное");
        Assertions.assertEquals(transactionResponseDTO.getAmount(), transaction.getAmount().toString());
    }

    @Test
    public void mapTransactionToTelegramResponseWhenCategoryIncome() throws InstanceNotFoundException {
        Transaction transaction = TestFieldsUtil.generateTestTransaction();
        transaction.getCategory().setType(Type.INCOME);

        TransactionResponseDTO transactionResponseDTO = transactionMapper.mapTransactionToTelegramResponse(transaction);
        Assertions.assertEquals(transactionResponseDTO.getType(), Type.INCOME.getValue());
        Assertions.assertEquals(transactionResponseDTO.getComment(), transaction.getMessage());
        Assertions.assertEquals(transactionResponseDTO.getCategory(), transaction.getCategory().getName());
        Assertions.assertEquals(transactionResponseDTO.getAmount(), transaction.getAmount().toString());
    }
}
