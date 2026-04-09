package com.overmoney.telegram_bot_service.service;


import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
public class TelegramMessageCheckerService {
    /**
     * Метод предназначен для того,
     * чтобы проверить является ли сообщение транзакцией. Любое сообщение, где на краях стоит - цифра, это транзакция.
     *
     * @param words Массив строк представляющий сообщение.
     * @return true если сообщение транзакция
     */
    private boolean checkForTransactionalFormat(String[] words) {
        // Проверяем массив, который приходит в метод, на пустоту и на то, чтобы в нем было больше одного элемента.
        if (words.length == 0 || words.length == 1) {
            return false;
        }

        // Если сообщение на любом из своих краев будет иметь цифру, значит это транзакция.
        return isNumeric(words[0]) || isNumeric(words[words.length - 1]);
    }

    /**
     * Метод предназначен для проверки сообщения, которое не является транзакцией,
     * на наличие символа '@', который дает понять, был ли кто-нибудь упомянут в сообщении или нет.
     *
     * @param message Строка представляющая сообщение.
     * @return true если в сообщении был кто-либо упомянут, в ином случае метод вернет false.
     */
    public boolean isNonTransactionalMessageMentionedToSomeone(String message) {
        String[] words = message.split(" ");

        // Проверяем чтобы сообщение не являлось транзакцией
        if (checkForTransactionalFormat(words)) {
            return false;
        }

        // Проверяем наличие знака '@' в начале каждого слова.
        for (String word : words) {
            if (word.startsWith("@")) {
                return true;
            }
        }

        return false;
    }
}
