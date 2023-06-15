package com.overmoney.telegram_bot_service;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest(classes = TelegramBotServiceApplication.class)
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

    @Test
    @DisplayName("Схема бд в миграции совпадает с моделями в коде")
    void checkLiquibase() {
    }
}
