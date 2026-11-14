package org.example.adapter;

import org.example.Word;

import java.util.List;
import java.util.Objects;

/** Adapter that converts a pipe-delimited external entry into Word. */
public class DictionaryAdapter implements DictionaryService {
    private final ExternalDictionaryApi externalApi;

    public DictionaryAdapter(ExternalDictionaryApi externalApi) {
        this.externalApi = Objects.requireNonNull(externalApi);
    }

    @Override
    public Word findWord(String key) {
        return convertEntry(externalApi.getEntry(key));
    }

    @Override
    public List<Word> findAllWords() {
        return externalApi.getEntries().stream().map(this::convertEntry).toList();
    }

    private Word convertEntry(String entry) {
        String[] parts = entry.split("\\|", -1);
        if ((parts.length != 2 && parts.length != 3)
                || parts[0].isBlank() || parts[1].isBlank()
                || (parts.length == 3 && parts[2].isBlank())) {
            throw new IllegalStateException("Invalid dictionary entry: " + entry);
        }
        String kazakhTranslation = parts.length == 3 ? parts[2].trim() : parts[1].trim();
        return new Word(parts[0].trim(), parts[1].trim(), kazakhTranslation, null);
    }
}
