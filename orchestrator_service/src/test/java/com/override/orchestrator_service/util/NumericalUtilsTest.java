package com.override.orchestrator_service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NumericalUtilsTest {

    @ParameterizedTest
    @ValueSource(doubles = {123.456, 123.457})
    void testRoundAmountDouble(double input) {
        Double result = NumericalUtils.roundAmount(input);
        assertEquals(123.46, result, 0.001);
    }

    @Test
    void testRoundAmountDoubleNull() {
        assertNull(NumericalUtils.roundAmount((Double) null));
    }

    @ParameterizedTest
    @ValueSource(floats = {123.456f, 123.455f})
    void testRoundAmountFloat(float input) {
        Float result = NumericalUtils.roundAmount(input);
        assertEquals(123.46f, result, 0.001f);
    }

    @Test
    void testRoundAmountFloatNull() {
        assertNull(NumericalUtils.roundAmount((Float) null));
    }

    @Test
    void testRoundAmountBigDecimal() {
        BigDecimal input = new BigDecimal("123.456");
        BigDecimal result = NumericalUtils.roundAmount(input);
        assertEquals(new BigDecimal("123.46"), result);
    }

    @Test
    void testRoundAmountBigDecimalNull() {
        assertNull(NumericalUtils.roundAmount((BigDecimal) null));
    }
}