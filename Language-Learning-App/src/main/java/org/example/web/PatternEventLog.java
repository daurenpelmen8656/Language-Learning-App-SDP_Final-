package org.example.web;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PatternEventLog {
    public static final int MAX_EVENTS = 30;
    private final ArrayDeque<PatternEvent> events = new ArrayDeque<>();
    private final Clock clock;

    public PatternEventLog() {
        this(Clock.systemUTC());
    }

    public PatternEventLog(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    public synchronized void record(String pattern, String message) {
        events.addFirst(new PatternEvent(pattern, message, Instant.now(clock)));
        while (events.size() > MAX_EVENTS) {
            events.removeLast();
        }
    }

    public synchronized List<PatternEvent> latest() {
        return List.copyOf(new ArrayList<>(events));
    }
}
