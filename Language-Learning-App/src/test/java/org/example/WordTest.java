package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordTest {

    @Test
    void wordsWithSameDataAreEqual() {
        assertEquals(new Word("apple", "яблоко"), new Word("apple", "яблоко", null));
    }

    @Test
    void levelIsOptional() {
        assertNull(new Word("apple", "яблоко").getLevel());
        assertEquals("A1", new Word("apple", "яблоко", "A1").getLevel());
    }

    @Test
    void toStringShowsLevelOnlyWhenPresent() {
        assertEquals("apple - яблоко", new Word("apple", "яблоко").toString());
        assertEquals("apple - яблоко [A1]", new Word("apple", "яблоко", "A1").toString());
    }

    @Test
    void blankTextOrTranslationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Word(" ", "яблоко"));
        assertThrows(IllegalArgumentException.class, () -> new Word("apple", null));
    }
}
