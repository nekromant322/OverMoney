package com.override.recognizer_service.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class WordsToNumbersService {
    private static final HashMap<String, Long> wordMap = new HashMap<>();
    private final String SPACE = " ";

    static {
        wordMap.put("ноль", 0L);
        wordMap.put("один", 1L);
        wordMap.put("одна", 1L);
        wordMap.put("два", 2L);
        wordMap.put("две", 2L);
        wordMap.put("три", 3L);
        wordMap.put("четыре", 4L);
        wordMap.put("пять", 5L);
        wordMap.put("шесть", 6L);
        wordMap.put("семь", 7L);
        wordMap.put("восемь", 8L);
        wordMap.put("девять", 9L);
        wordMap.put("десять", 10L);
        wordMap.put("одиннадцать", 11L);
        wordMap.put("двенадцать", 12L);
        wordMap.put("тринадцать", 13L);
        wordMap.put("четырнадцать", 14L);
        wordMap.put("пятнадцать", 15L);
        wordMap.put("шестнадцать", 16L);
        wordMap.put("семнадцать", 17L);
        wordMap.put("восемнадцать", 18L);
        wordMap.put("девятнадцать", 19L);
        wordMap.put("двадцать", 20L);
        wordMap.put("тридцать", 30L);
        wordMap.put("сорок", 40L);
        wordMap.put("пятьдесят", 50L);
        wordMap.put("шестьдесят", 60L);
        wordMap.put("семьдесят", 70L);
        wordMap.put("восемьдесят", 80L);
        wordMap.put("девяносто", 90L);
        wordMap.put("сто", 100L);
        wordMap.put("двести", 200L);
        wordMap.put("триста", 300L);
        wordMap.put("четыреста", 400L);
        wordMap.put("пятьсот", 500L);
        wordMap.put("шестьсот", 600L);
        wordMap.put("семьсот", 700L);
        wordMap.put("восемьсот", 800L);
        wordMap.put("девятьсот", 900L);
        wordMap.put("тысяча", 1000L);
        wordMap.put("тысячи", 1000L);
        wordMap.put("тыща", 1000L);
        wordMap.put("тыщи", 1000L);
        wordMap.put("тысяч", 1000L);
        wordMap.put("миллион", 1000000L);
        wordMap.put("миллиона", 1000000L);
        wordMap.put("миллионов", 1000000L);
        wordMap.put("миллиард", 1000000000L);
        wordMap.put("миллиарда", 1000000000L);
        wordMap.put("миллиардов", 1000000000L);
    }

    public String wordsToNumbers(String words) {
        int number = 0;
        int prevNumber = 0;
        String[] splitWords = words.split("\\s+");
        StringBuilder message = new StringBuilder();
        for (String word : splitWords) {
            if (wordMap.containsKey(word)) {
                long value = wordMap.get(word);
                if (value >= 1000) {
                    number += prevNumber * value;
                    prevNumber = 0;
                } else {
                    prevNumber += value;
                }
            } else {
                message.append(word).append(SPACE);
            }
        }
        number += prevNumber;
        message.append(number);
        return message.toString();
    }
}