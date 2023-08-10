package com.override.recognizer_service.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class WordsToNumbersService {
    private final HashMap<String, Long> vocabulary = new HashMap<>();
    private final List<String> currencies = new ArrayList<>();
    private final String SPACE = " ";

    @PostConstruct
    public void fillCurrencies() {
        currencies.add("рубль");
        currencies.add("рублей");
        currencies.add("рубля");
    }

    @PostConstruct
    public void fillVocabulary() {
        vocabulary.put("ноль", 0L);
        vocabulary.put("один", 1L);
        vocabulary.put("одна", 1L);
        vocabulary.put("два", 2L);
        vocabulary.put("две", 2L);
        vocabulary.put("три", 3L);
        vocabulary.put("четыре", 4L);
        vocabulary.put("пять", 5L);
        vocabulary.put("шесть", 6L);
        vocabulary.put("семь", 7L);
        vocabulary.put("восемь", 8L);
        vocabulary.put("девять", 9L);
        vocabulary.put("десять", 10L);
        vocabulary.put("одиннадцать", 11L);
        vocabulary.put("двенадцать", 12L);
        vocabulary.put("тринадцать", 13L);
        vocabulary.put("четырнадцать", 14L);
        vocabulary.put("пятнадцать", 15L);
        vocabulary.put("шестнадцать", 16L);
        vocabulary.put("семнадцать", 17L);
        vocabulary.put("восемнадцать", 18L);
        vocabulary.put("девятнадцать", 19L);
        vocabulary.put("двадцать", 20L);
        vocabulary.put("тридцать", 30L);
        vocabulary.put("сорок", 40L);
        vocabulary.put("пятьдесят", 50L);
        vocabulary.put("шестьдесят", 60L);
        vocabulary.put("семьдесят", 70L);
        vocabulary.put("восемьдесят", 80L);
        vocabulary.put("девяносто", 90L);
        vocabulary.put("сто", 100L);
        vocabulary.put("двести", 200L);
        vocabulary.put("триста", 300L);
        vocabulary.put("четыреста", 400L);
        vocabulary.put("пятьсот", 500L);
        vocabulary.put("шестьсот", 600L);
        vocabulary.put("семьсот", 700L);
        vocabulary.put("восемьсот", 800L);
        vocabulary.put("девятьсот", 900L);
        vocabulary.put("тысяча", 1000L);
        vocabulary.put("тысячи", 1000L);
        vocabulary.put("тыща", 1000L);
        vocabulary.put("тыщи", 1000L);
        vocabulary.put("тысяч", 1000L);
        vocabulary.put("миллион", 1000000L);
        vocabulary.put("миллиона", 1000000L);
        vocabulary.put("миллионов", 1000000L);
        vocabulary.put("миллиард", 1000000000L);
        vocabulary.put("миллиарда", 1000000000L);
        vocabulary.put("миллиардов", 1000000000L);
    }

    public String wordsToNumbers(String words) {
        int number = 0;
        int prevNumber = 0;
        String[] splitWords = words.replaceAll("[^a-zA-Zа-яА-Я\\s]", "").split("\\s+");
        StringBuilder message = new StringBuilder();
        for (String word : splitWords) {
            if (vocabulary.containsKey(word.toLowerCase())) {
                long value = vocabulary.get(word.toLowerCase());
                if (value >= 1000) {
                    if (prevNumber == 0) {
                        prevNumber = 1;
                    }
                    number += prevNumber * value;
                    prevNumber = 0;
                } else {
                    prevNumber += value;
                }
            } else if (!currencies.contains(word.toLowerCase())) {
                message.append(word).append(SPACE);
            }
        }
        number += prevNumber;
        message.append(number);
        return message.toString();
    }
}