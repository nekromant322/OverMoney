package com.override.recognizer_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WordsToNumbersServiceTest {

    @InjectMocks
    public WordsToNumbersService wordsToNumbersService;

    @BeforeEach
    public void setUp() {
        wordsToNumbersService.fillCurrencies();
        wordsToNumbersService.fillVocabulary();
    }

    @ParameterizedTest
    @MethodSource("provideVoiceMessageAndCorrectAnswers")
    public void wordsToNumbersTest(String voiceMessage, String correctAnswers) {
        String processedMessage = wordsToNumbersService.wordsToNumbers(voiceMessage);
        assertEquals(processedMessage, correctAnswers);
    }

    private static Stream<Arguments> provideVoiceMessageAndCorrectAnswers() {
        return Stream.of(
                Arguments.of("пиво. двести рублей", "пиво 200"),
                Arguments.of("пиво, триста один рубль!", "пиво 301"),
                Arguments.of("пиво две тысячи. рублей", "пиво 2000"),
                Arguments.of("пиво пятьсот два рубля.", "пиво 502"),
                Arguments.of("коньяк сорок девять .", "коньяк 49"),
                Arguments.of("Телефон десять . тысяч сорок пять, рублей .", "Телефон 10045"),
                Arguments.of("Вино тысяча ", "Вино 1000"),
                Arguments.of("пиво, четырнадцать!", "пиво 14"),
                Arguments.of("пиво, двадцать два", "пиво 22"),
                Arguments.of("пиво триста пятьдесят девять", "пиво 359"),
                Arguments.of("пиво тысяча девятьсот  девяносто шесть", "пиво 1996"),
                Arguments.of("очень дорогое/ пиво! сто тридцать три тысячи двести семьдесят восемь",
                        "очень дорогое пиво 133278"),
                Arguments.of("машина два миллиона четыреста одиннадцать тысяч шестьсот двадцать один рубль",
                        "машина 2411621")
        );
    }
}
