package com.override.orchestrator_service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class NumericalUtils {

    public static Double roundAmount(Double amount) {
        return Optional.ofNullable(amount)
                .map(dAmount -> new BigDecimal(dAmount).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElse(null);
    }

    public static Float roundAmount(Float amount) {
        return Optional.ofNullable(amount)
                .map(fAmount -> new BigDecimal(fAmount).setScale(2, RoundingMode.HALF_UP).floatValue())
                .orElse(null);
    }

    public static BigDecimal roundAmount(BigDecimal amount) {
        return Optional.ofNullable(amount)
                .map(bigDecimal -> bigDecimal.setScale(2, RoundingMode.HALF_UP))
                .orElse(null);
    }
}
