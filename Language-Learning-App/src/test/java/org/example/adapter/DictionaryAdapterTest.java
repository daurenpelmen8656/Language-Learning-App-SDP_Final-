package org.example.adapter;

import org.example.Word;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DictionaryAdapterTest {
    private static final int EXPECTED_DICTIONARY_WORD_COUNT = 60;
    private final DictionaryService dictionary = new DictionaryAdapter(new ExternalDictionaryApi());

    @Test
    void convertsExternalFormatToWord() {
        Word word = dictionary.findWord("apple");

        assertEquals("apple", word.getText());
        assertEquals("яблоко", word.getTranslation());
    }

    @Test
    void lookupIsCaseInsensitive() {
        assertEquals("окно", dictionary.findWord("WINDOW").getTranslation());
    }

    @Test
    void includesKazakhTranslationsAndAllDictionaryEntries() {
        Word apple = dictionary.findWord("apple");

        assertEquals("алма", apple.getKazakhTranslation());
        assertEquals(EXPECTED_DICTIONARY_WORD_COUNT, dictionary.findAllWords().size());
    }

    @Test
    void unknownWordIsRejected() {
        assertThrows(WordNotFoundException.class,
                () -> dictionary.findWord("unknown-word"));
    }

    @Test
    void malformedLineReportsLocation() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new ExternalDictionaryApi(new ByteArrayInputStream(
                        "valid|верно\nbroken line".getBytes(StandardCharsets.UTF_8))));
        assertEquals("Malformed dictionary line 2: broken line", error.getMessage());
    }
}
