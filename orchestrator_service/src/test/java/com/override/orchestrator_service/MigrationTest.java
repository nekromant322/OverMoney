package com.override.orchestrator_service;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.override.orchestrator_service.kafka.config.KafkaConfig;
import com.override.orchestrator_service.kafka.consumerproducer.SseTransactionListener;
import com.override.orchestrator_service.kafka.consumerproducer.TransactionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest(classes = OrchestratorServiceApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@Slf4j
public class MigrationTest {
    private static int pgPort = 8123;
    private static EmbeddedPostgres pg = null;

    @BeforeAll
    public static void initPostgresDatabase() {
        try {
            pg = EmbeddedPostgres.builder()
                    .setPort(pgPort)
                    .start();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @MockBean(KafkaConfig.class)
    private KafkaConfig kafkaConfig;

    @MockBean(TransactionListener.class)
    private TransactionListener transactionListener;

    @MockBean(SseTransactionListener.class)
    private SseTransactionListener sseTransactionListener;

    @Test
    @DisplayName("Схема бд в миграции совпадает с моделями в коде")
    void checkLiquibase() {
    }
}
