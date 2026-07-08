package com.fanmes.common.id;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private IdGenerator() {
    }

    public static long nextId() {
        int sequence = SEQUENCE.updateAndGet(value -> value >= 999 ? 0 : value + 1);
        return Instant.now().toEpochMilli() * 1000 + sequence;
    }
}
