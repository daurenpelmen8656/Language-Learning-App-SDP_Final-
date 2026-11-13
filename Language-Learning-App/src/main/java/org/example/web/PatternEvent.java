package org.example.web;

import java.time.Instant;

public record PatternEvent(String pattern, String message, Instant occurredAt) { }
