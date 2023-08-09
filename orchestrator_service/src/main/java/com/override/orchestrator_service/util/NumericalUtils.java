package com.override.orchestrator_service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumericalUtils {

    public static Double roundAmount(Double amount) {
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static Float roundAmount(Float amount) {
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}
