package com.override.recognizer_service.service.voice;

import com.override.recognizer_service.config.WitAIConnectionProperties;
import com.override.recognizer_service.config.WitAISecretProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Service
@Profile("!dev")
@Slf4j
public class VoiceRecognitionServiceWitAI implements VoiceRecognitionService {
    @Autowired
    private WitAISecretProperties witAISecretProperties;
    @Autowired
    private WitAIConnectionProperties witAIConnectionProperties;

    private final int BYTE_BUFFER_CAPACITY = 1024;
    private final int END_OF_FILE = -1;
    private final int ERROR_RESPONSE_CODE = 400;
    private final String START_OF_RECOGNITION = "\"text\":";
    private final String END_OF_RECOGNITION = ",";
    private final int SYMBOL_COUNT_TO_MESSAGE_BEGINNING = 9;
    private final int SYMBOL_COUNT_TO_MESSAGE_ENDING = 1;

    @Override
    @SneakyThrows
    public String voiceToText(File wavFile) {
        String query = String.format(
                witAIConnectionProperties.getVersionParam(),
                URLEncoder.encode(witAISecretProperties.getVersion(), witAIConnectionProperties.getCharset())
        );

        URLConnection connectionURL =
                new URL(witAISecretProperties.getUrl() +
                        witAIConnectionProperties.getParamSeparator() +
                        query).openConnection();
        HttpURLConnection connection = (HttpURLConnection) connectionURL;
        connection.setRequestMethod(witAIConnectionProperties.getMethod());
        connection.setRequestProperty(witAIConnectionProperties.getAuthProperty(), witAISecretProperties.getToken());
        connection.setRequestProperty(witAIConnectionProperties.getContentTypeProperty(),
                witAIConnectionProperties.getContentTypeValue());
        connection.setDoOutput(true);
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
        log.info(responseLines.toString());

        int firstIndexOfMessage = responseLines.lastIndexOf(START_OF_RECOGNITION) + SYMBOL_COUNT_TO_MESSAGE_BEGINNING;
        int lastIndexOfMessage = responseLines.lastIndexOf(END_OF_RECOGNITION) - SYMBOL_COUNT_TO_MESSAGE_ENDING;

        String message = responseLines.substring(firstIndexOfMessage, lastIndexOfMessage).trim();

        log.info(message);

        return message;
    }
}
