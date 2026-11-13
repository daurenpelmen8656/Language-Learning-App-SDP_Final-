package org.example.adapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simulates a third-party dictionary with a format we cannot change ("word|translation").
 */
public class ExternalDictionaryApi {
    private final Map<String, String> entries = new HashMap<>();

    public ExternalDictionaryApi() {
        this(ExternalDictionaryApi.class.getResourceAsStream("/dictionary.txt"));
    }

    public ExternalDictionaryApi(InputStream source) {
        if (source == null) throw new IllegalStateException("Dictionary resource is missing");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                String[] fields = line.split("\\|", -1);
                if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank())
                    throw new IllegalArgumentException("Malformed dictionary line " + number + ": " + line);
                String word = fields[0].trim().toLowerCase(Locale.ROOT);
                if (entries.putIfAbsent(word, line) != null)
                    throw new IllegalArgumentException("Duplicate dictionary word at line " + number + ": " + word);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read dictionary", e);
        }
    }

    public String getEntry(String key) {
        String entry = entries.get(key.toLowerCase(Locale.ROOT));
        if (entry == null) throw new WordNotFoundException(key);
        return entry;
    }
}
