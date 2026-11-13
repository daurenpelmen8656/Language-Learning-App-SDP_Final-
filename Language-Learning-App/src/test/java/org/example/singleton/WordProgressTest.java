package org.example.singleton;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordProgressTest {

    @Test
    void correctAnswerMovesToNextBox() {
        WordProgress progress = new WordProgress();
        progress.recordAnswer(true);
        progress.recordAnswer(true);

        assertEquals(2, progress.getBox());
        assertEquals(2, progress.getConsecutiveCorrect());
    }

    @Test
    void wrongAnswerResetsBoxAndStreak() {
        WordProgress progress = new WordProgress(3, 3);
        progress.recordAnswer(false);

        assertEquals(0, progress.getBox());
        assertEquals(0, progress.getConsecutiveCorrect());
    }

    @Test
    void negativeValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new WordProgress(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new WordProgress(0, -1));
    }
}
