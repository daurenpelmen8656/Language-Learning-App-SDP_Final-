package org.example.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyTest {

    @Test
    void leitnerPromotesOnCorrectAndResetsOnWrong() {
        ReviewStrategy leitner = new LeitnerStrategy();

        // 1-й верный ответ (из коробки 0 в коробку 1) -> 2 дня
        assertEquals(2, leitner.nextReviewDays(0, 1, true));

        // 2-й верный ответ (из коробки 1 в коробку 2) -> 4 дня
        assertEquals(4, leitner.nextReviewDays(1, 2, true));

        // 3-й верный ответ (из коробки 2 в коробку 3) -> 8 дней
        assertEquals(8, leitner.nextReviewDays(2, 3, true));

        // Ошибка на любом уровне сбрасывает на 1 день (коробка 0)
        assertEquals(1, leitner.nextReviewDays(3, 0, false));
    }

    @Test
    void sm2GrowsExponentiallyOnCorrect() {
        ReviewStrategy sm2 = new Sm2Strategy();

        assertEquals(1, sm2.nextReviewDays(0, 1, true));
        assertEquals(3, sm2.nextReviewDays(1, 2, true));
        assertTrue(sm2.nextReviewDays(2, 3, true) >= 7);

        // Ошибка сбрасывает на 1 день
        assertEquals(1, sm2.nextReviewDays(2, 0, false));
    }

    @Test
    void simpleIntervalIncrementsLinearly() {
        ReviewStrategy simple = new SimpleIntervalStrategy();

        assertEquals(4, simple.nextReviewDays(0, 1, true));
        assertEquals(6, simple.nextReviewDays(1, 2, true));
        assertEquals(8, simple.nextReviewDays(2, 3, true));

        // Ошибка сбрасывает на 1 день
        assertEquals(1, simple.nextReviewDays(2, 0, false));
    }
}
