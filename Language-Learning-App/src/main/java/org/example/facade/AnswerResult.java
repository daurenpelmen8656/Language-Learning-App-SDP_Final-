package org.example.facade;

import java.time.LocalDate;

public record AnswerResult(String wordText, boolean correct, int nextReviewDays,
                           LocalDate nextReviewDate, int xp, int streak) { }
