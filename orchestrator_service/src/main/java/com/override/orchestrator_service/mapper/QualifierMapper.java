package com.override.orchestrator_service.mapper;

import com.override.dto.TransactionQualifierDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class QualifierMapper {
    public UUID getTransactionId(TransactionQualifierDTO transactionQualifierDTO) {
        return UUID.fromString(transactionQualifierDTO.getTransactionId());
    }

    public UUID getCategoryId(TransactionQualifierDTO transactionQualifierDTO) {
        return UUID.fromString(transactionQualifierDTO.getCategoryId());
    }
}