package org.example.facade;

import java.time.LocalDate;

public record WordProgressView(int box, int consecutiveCorrect, LocalDate nextReviewDate) { }
