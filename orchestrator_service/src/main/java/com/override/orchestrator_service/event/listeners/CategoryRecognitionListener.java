package com.override.orchestrator_service.event.listeners;

import com.override.dto.CategoryDTO;
import com.override.orchestrator_service.event.CategoryRecognitionEvent;
import com.override.orchestrator_service.feign.RecognizerFeign;
import com.override.orchestrator_service.service.CategoryService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryRecognitionListener {

    private final CategoryService categoryService;
    private final RecognizerFeign recognizerFeign;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onCategoryRecognition(CategoryRecognitionEvent event) {
        try {
            List<CategoryDTO> categories = categoryService
                    .findCategoriesListByUserId(event.getTelegramUserId());

            if (categories.isEmpty()) {
                log.debug("No categories for user {}, skipping recognition",
                        event.getTelegramUserId());
                return;
            }

            recognizerFeign.recognizeCategory(
                    event.getMessage(),
                    event.getTransactionId(),
                    categories
            );
        } catch (FeignException e) {
            log.error("Recognizer unavailable for transaction {}: {}",
                    event.getTransactionId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category recognition for transaction {}",
                    event.getTransactionId(), e);
        }
    }
}
