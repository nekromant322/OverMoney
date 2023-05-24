package com.override.orchestrator_service.controller.rest;

import com.override.dto.TransactionQualifierDTO;
import com.override.orchestrator_service.mapper.QualifierMapper;
import com.override.orchestrator_service.service.CategoryService;
import com.override.orchestrator_service.service.KeywordService;
import com.override.orchestrator_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class QualifierController {

    @Autowired
    QualifierMapper qualifierMapper;

    @Autowired
    TransactionService transactionService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    KeywordService keywordService;

    @PostMapping("/qualifier")
    public void qualify(@RequestBody TransactionQualifierDTO transactionQualifierDTO) {
        System.out.println(transactionQualifierDTO);
        transactionService.setTransactionCategory(qualifierMapper.getTransactionId(transactionQualifierDTO),
                qualifierMapper.getCategoryId(transactionQualifierDTO));
        keywordService.setKeywordCategory(qualifierMapper.getTransactionId(transactionQualifierDTO),
                qualifierMapper.getCategoryId(transactionQualifierDTO));
        ;
    }
}