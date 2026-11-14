package org.example.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Simulates a third-party dictionary with a pipe-delimited entry format.
 */
public class ExternalDictionaryApi {
    private static final int MINIMUM_FIELD_COUNT = 2;
    private static final int MAXIMUM_FIELD_COUNT = 3;
    private static final int WORD_FIELD_INDEX = 0;
    private static final String FIELD_SEPARATOR = "\\|";

    private final Map<String, String> entries;

    public ExternalDictionaryApi() {
        this(ExternalDictionaryApi.class.getResourceAsStream("/dictionary.txt"));
    }

    public ExternalDictionaryApi(InputStream source) {
        if (source == null) {
            throw new IllegalStateException("Dictionary resource is missing");
        }
        entries = readEntries(source);
    }

    private Map<String, String> readEntries(InputStream source) {
        Map<String, String> loadedEntries = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                String[] fields = line.split(FIELD_SEPARATOR, -1);
                validateFields(fields, number, line);
                String word = normalizeWord(fields[WORD_FIELD_INDEX]);
                if (loadedEntries.putIfAbsent(word, line) != null) {
                    throw new IllegalArgumentException("Duplicate dictionary word at line " + number + ": " + word);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read dictionary", e);
        }
        return Collections.unmodifiableMap(loadedEntries);
    }

    private void validateFields(String[] fields, int lineNumber, String line) {
        boolean invalidFieldCount = fields.length < MINIMUM_FIELD_COUNT || fields.length > MAXIMUM_FIELD_COUNT;
        boolean blankField = Arrays.stream(fields).anyMatch(String::isBlank);
        if (invalidFieldCount || blankField) {
            throw new IllegalArgumentException("Malformed dictionary line " + lineNumber + ": " + line);
        }
    }

    private String normalizeWord(String word) {
        return word.trim().toLowerCase(Locale.ROOT);
    }

    public String getEntry(String key) {
        String entry = entries.get(normalizeWord(key));
        if (entry == null) {
            throw new WordNotFoundException(key);
        }
        return entry;
    }

    public List<String> getEntries() {
        return List.copyOf(entries.values());
    }
}
