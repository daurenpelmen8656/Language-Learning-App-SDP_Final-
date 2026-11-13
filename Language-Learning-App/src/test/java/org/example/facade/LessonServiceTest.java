package org.example.facade;

import org.example.adapter.DictionaryAdapter;
import org.example.adapter.ExternalDictionaryApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LessonServiceTest {
    @Test
    void shouldBuildLessonData_whenLessonIsRequested() {
        LessonView lesson = new LessonService(new DictionaryAdapter(new ExternalDictionaryApi())).buildLesson();

        assertEquals(3, lesson.words().size());
        assertEquals(3, lesson.cards().size());
        assertEquals(3, lesson.exercises().size());
        assertThrows(UnsupportedOperationException.class, () -> lesson.words().clear());
    }
}
