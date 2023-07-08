package com.override.recognizer_service.service.voice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.dto.AudioRecognizerGoRequestDTO;
import com.override.dto.AudioRecognizerGoResponseDTO;
import com.override.recognizer_service.config.AudioRecognizerGoServiceProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Service
@Profile("!dev")
@Slf4j
public class VoiceDTORecognitionServiceImplGoAudioRecognizer implements VoiceDTORecognitionService {

    @Autowired
    private AudioRecognizerGoServiceProperties audioRecognizerGoServiceProperties;

    /**
     * Метод открывает соединение  HttpURLConnection и отправляюет запрос
     * на распознавание файла в сервис AudioRecognizer
     * @param request принимает на вход ДТО для отправки в сервис AudioRecognizer
     * @return Возвращает распознанную строку (цифры приходят прописью)
     */
    @SneakyThrows
    public String voiceToText(AudioRecognizerGoRequestDTO request) {

        ObjectMapper mapper = new ObjectMapper();
        String decryptedMessageAsJSON;
        HttpURLConnection connection = getConnection();

        try(OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = mapper.writeValueAsBytes(request);
            outputStream.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            decryptedMessageAsJSON = response.toString();
        }

        return mapper.readValue(decryptedMessageAsJSON, AudioRecognizerGoResponseDTO.class).getText();
    }

    /**
     * Метод, открывающий соединение с сервисом AudioRecognizer
     * @return соединение с этим сервисом
     */
    @SneakyThrows
    private HttpURLConnection getConnection() {
        URLConnection connectionURL =
                new URL(audioRecognizerGoServiceProperties.getUrl()).openConnection();
        HttpURLConnection connection = (HttpURLConnection) connectionURL;
        connection.setRequestMethod(audioRecognizerGoServiceProperties.getMethod());
        connection.setRequestProperty("Content-Type", audioRecognizerGoServiceProperties.getContentType());
        connection.setRequestProperty("Accept", audioRecognizerGoServiceProperties.getAccept());
        connection.setDoOutput(true);
        return connection;
    }


}
