package com.override.recognizer_service.service.category;

import com.override.dto.CategoryDTO;
import com.override.dto.TransactionDTO;
import com.override.dto.constants.SuggestionAlgorithm;
import com.override.recognizer_service.feign.OrchestratorFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CategoryRecognizerService {

    @Value("${recognizer.min-accuracy}")
    private float minAccuracy;

    private final OrchestratorFeign orchestratorFeign;
    private final List<CategoryRecognizer> recognizers;

    public CategoryRecognizerService(OrchestratorFeign orchestratorFeign,
                                     List<CategoryRecognizer> recognizers) {
        this.orchestratorFeign = orchestratorFeign;
        this.recognizers = recognizers;
    }

    /**
     * Метод поиска подходящего алгоритма для подбора категории
     * Список алгоритмов можно задать в yml в поле recognizer.algorithms
     * Приоритеты алгоритмов указываются через аннотацию Order в каждом из классов CategoryRecognizer
     * Если какой-то алгоритм не сможет нам подобрать категорию,
     * мы пробуем запустить подбор по следующему по приоритету алгоритму
     *
     * @param message
     * @param categories
     * @param transactionId
     */
    public void sendTransactionWithSuggestedCategory(String message, List<CategoryDTO> categories, UUID transactionId) {
        RecognizerResult recognizerResult;
        SuggestionAlgorithm selectedAlgorithm;
        boolean categoryFind = false;
        for (CategoryRecognizer recognizer : recognizers) {
            selectedAlgorithm = recognizer.getAlgorithm();
            try {
                recognizerResult = recognizer.recognizeCategoryAndAccuracy(message, categories);

                if (recognizerResult != null
                        && recognizerResult.getCategory() != null
                        && recognizerResult.getAccuracy() >= minAccuracy) {
                    TransactionDTO transactionDTO = TransactionDTO.builder()
                            .accuracy(recognizerResult.getAccuracy())
                            .id(transactionId)
                            .suggestedCategoryId(recognizerResult.getCategory().getId())
                            .suggestionAlgorithm(selectedAlgorithm)
                            .build();
                    orchestratorFeign.editTransaction(transactionDTO);
                    break;
                } else {
                    log.warn("No suggested category for message: {}, used algorithm {}",
                            message, recognizer.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error("Algorithm {} failed with error: {}", recognizer.getClass().getSimpleName(), e.getMessage());
            }
        }
        if (!categoryFind){
            orchestratorFeign.notifyUncategorizedTransaction(transactionId);
        }
    }
}