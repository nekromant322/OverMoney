package com.override.recognizer_service.service.voice;

import com.override.recognizer_service.config.WitAIProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Ссылка на доку распознавания голоса wit.ai (необходим vpn, чтобы зайти):
 * https://wit.ai/docs/http/20230215/#post__speech_link
 */
@Service
@Profile("!dev")
@Slf4j
public class VoiceRecognitionServiceWitAI implements VoiceRecognitionService {
    @Autowired
    private WitAIProperties witAIProperties;

    private final int BYTE_BUFFER_CAPACITY = 1024;
    private final int END_OF_FILE = -1;
    private final int ERROR_RESPONSE_CODE = 400;
    private final String START_OF_RECOGNITION = "\"text\":";
    private final String END_OF_RECOGNITION = ",";
    private final int SYMBOL_COUNT_TO_MESSAGE_BEGINNING = 9;
    private final int SYMBOL_COUNT_TO_MESSAGE_ENDING = 1;

    /**
     * Метод, открывающий соединение с помощью URLConnection и отправляющий запрос
     * на распознавание файла в wit.ai
     * референс: https://stackoverflow.com/questions/35358232/wit-ai-how-to-send-request-in-java
     * @param wavFile принимает на вход для отправки в wit.ai
     * @return Возвращает распознанную строку (цифры приходят прописью)
     */
    @Override
    @SneakyThrows
    public String voiceToText(File wavFile) {
        HttpURLConnection connection = getConnection();

        OutputStream outputStream = connection.getOutputStream();
        FileChannel fileChannel = new FileInputStream(wavFile.getAbsolutePath()).getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);

        while ((fileChannel.read(byteBuffer)) != END_OF_FILE) {
            byteBuffer.flip();
            byte[] b = new byte[byteBuffer.remaining()];
            byteBuffer.get(b);
            outputStream.write(b);
            byteBuffer.clear();
        }

        InputStream is;
        if (connection.getResponseCode() >= ERROR_RESPONSE_CODE) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }

        BufferedReader response = new BufferedReader(new InputStreamReader(is));
        StringBuilder responseLines = new StringBuilder();
        String line;
        while ((line = response.readLine()) != null) {
            responseLines.append(line).append("\n");
        }

        int firstIndexOfMessage = responseLines.lastIndexOf(START_OF_RECOGNITION) + SYMBOL_COUNT_TO_MESSAGE_BEGINNING;
        int lastIndexOfMessage = responseLines.lastIndexOf(END_OF_RECOGNITION) - SYMBOL_COUNT_TO_MESSAGE_ENDING;

        return responseLines.substring(firstIndexOfMessage, lastIndexOfMessage).trim();
    }

    /**
     * Метод, открывающий соединение с wit.ai
     * @return соединение с wit.ai
     */
    @SneakyThrows
    private HttpURLConnection getConnection() {
        String query = String.format(
                witAIProperties.getVersionParam(),
                URLEncoder.encode(witAIProperties.getVersion(), witAIProperties.getCharset())
        );

        URLConnection connectionURL =
                new URL(witAIProperties.getUrl() +
                        witAIProperties.getParamSeparator() +
                        query).openConnection();
        HttpURLConnection connection = (HttpURLConnection) connectionURL;
        connection.setRequestMethod(witAIProperties.getMethod());
        connection.setRequestProperty(witAIProperties.getAuthProperty(), witAIProperties.getToken());
        connection.setRequestProperty(witAIProperties.getContentTypeProperty(),
                witAIProperties.getContentTypeValue());
        connection.setDoOutput(true);

        return connection;
    }
}
