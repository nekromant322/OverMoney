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
        tickerToWeight.put("AFKS", 0.58d);
        tickerToWeight.put("AFLT", 0.53d);
        tickerToWeight.put("AGRO", 0.86d);
        tickerToWeight.put("ALRS", 1.28d);
        tickerToWeight.put("CBOM", 0.74d);
        tickerToWeight.put("CHMF", 2.22d);
        tickerToWeight.put("ENPG", 0.41d);
        tickerToWeight.put("FEES", 0.49d);
        tickerToWeight.put("FIVE", 0.97d);
        tickerToWeight.put("FLOT", 0.62d);
        tickerToWeight.put("GAZP", 11.14d);
        tickerToWeight.put("GLTR", 0.24d);
        tickerToWeight.put("GMKN", 6.05d);
        tickerToWeight.put("HYDR", 0.26d);
        tickerToWeight.put("IRAO", 1.55d);
        tickerToWeight.put("LKOH", 14.47d);
        tickerToWeight.put("MAGN", 1.28d);
        tickerToWeight.put("MGNT", 1.97d);
        tickerToWeight.put("MOEX", 1.02d);
        tickerToWeight.put("MSNG", 0.38d);
        tickerToWeight.put("MTLR", 0.53d);
        tickerToWeight.put("MTLRP",0.38d);
        tickerToWeight.put("MTSS", 1.22d);
        tickerToWeight.put("NLMK", 1.74d);
        tickerToWeight.put("NVTK", 3.06d);
        tickerToWeight.put("OZON", 0.96d);
        tickerToWeight.put("PHOR", 0.8d);
        tickerToWeight.put("PIKK", 2.06d);
        tickerToWeight.put("PLZL", 2.34d);
        tickerToWeight.put("POLY", 0.21d);
        tickerToWeight.put("POSI", 0.35d);
        tickerToWeight.put("ROSN", 2.33d);
        tickerToWeight.put("RTKM", 1.06d);
        tickerToWeight.put("RUAL", 1.66d);
        tickerToWeight.put("SBER", 13.11d);
        tickerToWeight.put("SBERP", 2.53d);
        tickerToWeight.put("SELG", 0.29d);
        tickerToWeight.put("SGZH", 0.24d);
        tickerToWeight.put("SMLT", 0.35d);
        tickerToWeight.put("SNGS", 3.13d);
        tickerToWeight.put("SNGSP", 2.98d);
        tickerToWeight.put("TATN", 5.73d);
        tickerToWeight.put("TATNP", 1.05d);
        tickerToWeight.put("TCSG", 0.62d);
        tickerToWeight.put("TRNFP", 0.66d);
        tickerToWeight.put("UPRO", 0.31d);
        tickerToWeight.put("VKCO", 0.41d);
        tickerToWeight.put("VTBR", 0.94d);
        tickerToWeight.put("YNDX", 1.9d);
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }
}
