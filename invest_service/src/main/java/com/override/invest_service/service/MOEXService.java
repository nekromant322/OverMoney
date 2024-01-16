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
        tickerToWeight.put("AFKS", 0.55d);
        tickerToWeight.put("AFLT", 0.54d);
        tickerToWeight.put("AGRO", 0.84d);
        tickerToWeight.put("ALRS", 1.34d);
        tickerToWeight.put("CBOM", 0.72d);
        tickerToWeight.put("CHMF", 2.11d);
        tickerToWeight.put("ENPG", 0.44d);
        tickerToWeight.put("FEES", 0.48d);
        tickerToWeight.put("FIVE", 0.91d);
        tickerToWeight.put("FLOT", 0.67d);
        tickerToWeight.put("GAZP", 11.52d);
        tickerToWeight.put("GLTR", 0.25d);
        tickerToWeight.put("GMKN", 6.82d);
        tickerToWeight.put("HYDR", 0.27d);
        tickerToWeight.put("IRAO", 1.59d);
        tickerToWeight.put("LKOH", 14.09d);
        tickerToWeight.put("MAGN", 1.37d);
        tickerToWeight.put("MGNT", 1.89d);
        tickerToWeight.put("MOEX", 1.0d);
        tickerToWeight.put("MSNG", 0.36d);
        tickerToWeight.put("MTLR", 0.6d);
        tickerToWeight.put("MTLRP",0.43d);
        tickerToWeight.put("MTSS", 1.17d);
        tickerToWeight.put("NLMK", 1.77d);
        tickerToWeight.put("NVTK", 3.38d);
        tickerToWeight.put("OZON", 0.92d);
        tickerToWeight.put("PHOR", 0.8d);
        tickerToWeight.put("PIKK", 1.73d);
        tickerToWeight.put("PLZL", 2.534d);
        tickerToWeight.put("POLY", 0.31d);
        tickerToWeight.put("POSI", 0.33d);
        tickerToWeight.put("QIWI", 0.34d);
        tickerToWeight.put("ROSN", 2.4d);
        tickerToWeight.put("RTKM", 0.87d);
        tickerToWeight.put("RUAL", 1.73d);
        tickerToWeight.put("SBER", 12.45d);
        tickerToWeight.put("SBERP", 2.41d);
        tickerToWeight.put("SELG", 0.32d);
        tickerToWeight.put("SGZH", 0.25d);
        tickerToWeight.put("SMLT", 0.38d);
        tickerToWeight.put("SNGS", 3.22d);
        tickerToWeight.put("SNGSP", 2.67d);
        tickerToWeight.put("TATN", 5.81d);
        tickerToWeight.put("TATNP", 1.07d);
        tickerToWeight.put("TCSG", 0.69d);
        tickerToWeight.put("TRNFP", 0.61d);
        tickerToWeight.put("UPRO", 0.3d);
        tickerToWeight.put("VKCO", 0.41d);
        tickerToWeight.put("VTBR", 1.0d);
        tickerToWeight.put("YNDX", 1.51d);
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }
}
