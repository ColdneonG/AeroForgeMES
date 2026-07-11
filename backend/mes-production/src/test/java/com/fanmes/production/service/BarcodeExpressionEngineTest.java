package com.fanmes.production.service;

import com.fanmes.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BarcodeExpressionEngineTest {
    private final BarcodeExpressionEngine engine = new BarcodeExpressionEngine();

    @Test
    void rendersDateContextAndSerialTokens() {
        String value = engine.render(
                "SN-${itemCode}-${yyyyMMdd}-${####}",
                LocalDate.of(2026, 7, 10),
                27,
                6,
                Map.of("itemCode", "FAN500")
        );

        assertEquals("SN-FAN500-20260710-0027", value);
    }

    @Test
    void recognizesBothSequenceTokenStyles() {
        assertTrue(engine.requiresSequence("SN-${####}"));
        assertTrue(engine.requiresSequence("SN-${serial}"));
        assertFalse(engine.requiresSequence("MAT-${itemCode}-${yyyyMMdd}"));
    }

    @Test
    void rejectsUnresolvedTokens() {
        assertThrows(BadRequestException.class, () -> engine.render(
                "SN-${missing}-${####}",
                LocalDate.of(2026, 7, 10),
                1,
                4,
                Map.of()
        ));
    }
}
