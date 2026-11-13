package org.example.facade;

import org.example.adapter.DictionaryAdapter;
import org.example.adapter.ExternalDictionaryApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LessonServiceTest {
    private static final int EXPECTED_WORD_COUNT = 60;
    private static final int EXPECTED_DECORATED_CARD_COUNT = 3;

    @Test
    void shouldBuildLessonData_whenLessonIsRequested() {
        LessonView lesson = new LessonService(new DictionaryAdapter(new ExternalDictionaryApi())).buildLesson();

        assertEquals(EXPECTED_WORD_COUNT, lesson.words().size());
        assertEquals(EXPECTED_DECORATED_CARD_COUNT, lesson.cards().size());
        assertEquals(EXPECTED_WORD_COUNT * LessonService.EXERCISES_PER_WORD, lesson.exercises().size());
        assertEquals("алма", lesson.words().getFirst().kazakhTranslation());
        assertThrows(UnsupportedOperationException.class, () -> lesson.words().clear());
    }
}
