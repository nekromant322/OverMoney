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
        tickerToWeight.put("AFKS", 0.65d);
        tickerToWeight.put("AFLT", 0.71d);
        tickerToWeight.put("AGRO", 0.77d);
        tickerToWeight.put("ALRS", 1.63d);
        tickerToWeight.put("CBOM", 0.52d);
        tickerToWeight.put("CHMF", 1.98d);
        tickerToWeight.put("ENPG", 0.47d);
        tickerToWeight.put("FEES", 0.57d);
        tickerToWeight.put("FIVE", 0.43d);
        tickerToWeight.put("FIXP", 0.19d);
        tickerToWeight.put("GAZP", 13.47d);
        tickerToWeight.put("GLTR", 0.24d);
        tickerToWeight.put("GMKN", 6.03d);
        tickerToWeight.put("HYDR", 0.22d);
        tickerToWeight.put("IRAO", 1.83d);
        tickerToWeight.put("LKOH", 13.67d);
        tickerToWeight.put("MAGN", 1.4d);
        tickerToWeight.put("MGNT", 5.52d);
        tickerToWeight.put("MOEX", 0.37d);
        tickerToWeight.put("MTSS", 1.45d);
        tickerToWeight.put("NLMK", 1.94d);
        tickerToWeight.put("NVTK", 3.79d);
        tickerToWeight.put("OZON", 0.74d);
        tickerToWeight.put("PHOR", 1.01d);
        tickerToWeight.put("PIKK", 2.23d);
        tickerToWeight.put("PLZL", 2.8d);
        tickerToWeight.put("POLY", 0.39d);
        tickerToWeight.put("ROSN", 2.28d);
        tickerToWeight.put("RTKM", 0.97d);
        tickerToWeight.put("RUAL", 1.61d);
        tickerToWeight.put("SBER", 11.59d);
        tickerToWeight.put("SBERP", 2.24d);
        tickerToWeight.put("SGZH", 0.43d);
        tickerToWeight.put("SNGS", 3.65d);
        tickerToWeight.put("SNGSP", 2.44d);
        tickerToWeight.put("TATN", 4.58d);
        tickerToWeight.put("TATNP", 0.84d);
        tickerToWeight.put("TCSG", 0.78d);
        tickerToWeight.put("TRNFP", 0.59d);
        tickerToWeight.put("VKCO", 0.48d);
        tickerToWeight.put("VTBR", 0.86d);
        tickerToWeight.put("YNDX", 1.63d);
    }

    public Map<String, Double> getTickerToWeight() {
        return tickerToWeight;
    }
}
