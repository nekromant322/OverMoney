package com.override.payment_service;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.override.payment_service.kafka.config.KafkaConfig;
import com.override.payment_service.kafka.consumer.KafkaTransactionConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest(classes = PaymentServiceApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@Slf4j
public class MigrationTest {
    private static int pgPort = 8130;
    private static EmbeddedPostgres pg = null;
    private static final String createTablePayment =
            "CREATE TABLE payment (id SERIAL PRIMARY KEY, id_transaction UUID)";

    @BeforeAll
    public static void initPostgresDatabase() {
        try {
            pg = EmbeddedPostgres.builder()
                    .setPort(pgPort)
                    .start();
            pg.getPostgresDatabase().getConnection().createStatement().executeQuery(createTablePayment);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @MockBean(KafkaConfig.class)
    private KafkaConfig kafkaConfig;

    @MockBean(KafkaTransactionConsumer.class)
    private KafkaTransactionConsumer transactionResponse;

    @Test
    @DisplayName("Схема бд в миграции совпадает с моделями в коде")
    void checkLiquibase() {
    }
}
