package org.example.adapter;

import org.example.Word;

/** Adapter that converts the external "word|translation" result into Word. */
public class DictionaryAdapter implements DictionaryService {
    private final ExternalDictionaryApi externalApi;

    public DictionaryAdapter(ExternalDictionaryApi externalApi) {
        this.externalApi = externalApi;
    }

    @Override
    public Word findWord(String key) {
        String entry = externalApi.getEntry(key);
        String[] parts = entry.split("\\|", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid dictionary entry: " + entry);
        }
        return new Word(parts[0].trim(), parts[1].trim());
    }
}
