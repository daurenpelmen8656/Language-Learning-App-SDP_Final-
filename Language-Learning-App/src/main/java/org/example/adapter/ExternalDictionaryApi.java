package org.example.adapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simulates a third-party dictionary with a pipe-delimited entry format.
 */
public class ExternalDictionaryApi {
    private final Map<String, String> entries = new LinkedHashMap<>();

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
                if ((fields.length != 2 && fields.length != 3)
                        || Arrays.stream(fields).anyMatch(String::isBlank))
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

    public List<String> getEntries() {
        return List.copyOf(entries.values());
    }
}
