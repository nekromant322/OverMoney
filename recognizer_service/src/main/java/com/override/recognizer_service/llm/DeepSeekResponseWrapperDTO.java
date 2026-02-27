package com.override.recognizer_service.llm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekResponseWrapperDTO {

    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }
}
