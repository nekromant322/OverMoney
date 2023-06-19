package com.override.recognizer_service.service.voice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Service
@Slf4j
public class WitAiRecognizeService {

    public String recognizeSpeech(File wavFile) throws Exception {
        String url = "https://api.wit.ai/speech";
        String key = "Bearer 7VG6LHATN2S5W5PCHSOA6MP7HJ3CO2LF";

        String param1 = "20230215";
        String charset = "UTF-8";

        String query = String.format("v=%s",
                URLEncoder.encode(param1, charset));

        URLConnection connectionURL = new URL(url + "?" + query).openConnection();
        HttpURLConnection connection = (HttpURLConnection) connectionURL;
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", key);
        connection.setRequestProperty("Content-Type", "audio/wav");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        FileChannel fileChannel = new FileInputStream(wavFile.getAbsolutePath()).getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while ((fileChannel.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            byte[] b = new byte[byteBuffer.remaining()];
            byteBuffer.get(b);
            outputStream.write(b);
            byteBuffer.clear();
        }

        InputStream is;
        if (connection.getResponseCode() >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }

        BufferedReader response = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = response.readLine()) != null) {
            log.info(line);
        }

        return response.toString();
    }
}
