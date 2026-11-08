package org.example.adapter;

import org.example.Word;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DictionaryAdapterTest {
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
    void unknownWordIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> dictionary.findWord("unknown-word"));
    }
}
