package com.override.invest_service.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class MOEXService {
    private final Map<String, Double> tickerToWeight = new HashMap<>();
    //todo
    // стянуть веса индекса мосбиржи с картинки  https://informer.moex.com/ru/index/constituents-IMOEX-20230721.gif?page=0
    // можно просто совать актуальную дату в имя файла

    @PostConstruct
    public void init() {
        tickerToWeight.put("AFKS", 0.60d);
        tickerToWeight.put("AFLT", 0.62d);
        tickerToWeight.put("AGRO", 0.76d);
        tickerToWeight.put("ALRS", 1.45d);
        tickerToWeight.put("CBOM", 0.65d);
        tickerToWeight.put("CHMF", 1.96d);
        tickerToWeight.put("ENPG", 0.86d);
        tickerToWeight.put("FEES", 0.54d);
        tickerToWeight.put("FIVE", 0.46d);
        tickerToWeight.put("GAZP", 12.52d);
        tickerToWeight.put("GLTR", 0.26d);
        tickerToWeight.put("GMKN", 7.07d);
        tickerToWeight.put("HYDR", 0.34d);
        tickerToWeight.put("IRAO", 1.63d);
        tickerToWeight.put("LKOH", 15.00d);
        tickerToWeight.put("MAGN", 1.32d);
        tickerToWeight.put("MGNT", 1.66d);
        tickerToWeight.put("MOEX", 0.90d);
        tickerToWeight.put("MTSS", 1.24d);
        tickerToWeight.put("NLMK", 1.82d);
        tickerToWeight.put("NVTK", 3.84d);
        tickerToWeight.put("OZON", 0.88d);
        tickerToWeight.put("PHOR", 0.88d);
        tickerToWeight.put("PIKK", 2.15d);
        tickerToWeight.put("PLZL", 2.50d);
        tickerToWeight.put("POLY", 0.34d);
        tickerToWeight.put("ROSN", 2.31d);
        tickerToWeight.put("RTKM", 0.90d);
        tickerToWeight.put("RUAL", 2.04d);
        tickerToWeight.put("SBER", 12.26d);
        tickerToWeight.put("SBERP", 2.36d);
        tickerToWeight.put("SGZH", 0.37d);
        tickerToWeight.put("SNGS", 3.38d);
        tickerToWeight.put("SNGSP", 2.41d);
        tickerToWeight.put("TATN", 4.89d);
        tickerToWeight.put("TATNP", 0.89d);
        tickerToWeight.put("TCSG", 0.75d);
        tickerToWeight.put("TRNFP", 0.54d);
        tickerToWeight.put("VKCO", 0.47d);
        tickerToWeight.put("VTBR", 1.17d);
        tickerToWeight.put("YNDX", 1.52d);
        tickerToWeight.put("SELG", 0.41d);
        tickerToWeight.put("QIWI", 0.37d);
        tickerToWeight.put("POSI", 0.36d);
        tickerToWeight.put("UPRO", 0.34d);
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }
}
