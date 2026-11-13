package org.example.singleton;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** Persists the Singleton session as a UTF-8 properties file. */
public final class SessionStorage {
    public static final Path DEFAULT_FILE = Path.of("progress.properties");
    private static final String USERNAME_KEY = "username";
    private static final String XP_KEY = "xp";
    private static final String STREAK_KEY = "streak";
    private static final String WORDS_LEARNED_KEY = "wordsLearned";

    private final Path file;

    public SessionStorage() {
        this(DEFAULT_FILE);
    }

    public SessionStorage(Path file) {
        this.file = Objects.requireNonNull(file);
    }

    public boolean load(UserSession session) throws IOException {
        if (!Files.exists(file)) {
            return false;
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        Map<String, WordProgress> wordProgress = readWords(properties);
        session.restore(required(properties, USERNAME_KEY), nonNegative(properties, XP_KEY),
                nonNegative(properties, STREAK_KEY), nonNegative(properties, WORDS_LEARNED_KEY), wordProgress);
        return true;
    }

    public void save(UserSession session) throws IOException {
        Objects.requireNonNull(session);
        Properties properties = new Properties();
        properties.setProperty(USERNAME_KEY, session.getUsername());
        properties.setProperty(XP_KEY, Integer.toString(session.getXp()));
        properties.setProperty(STREAK_KEY, Integer.toString(session.getStreak()));
        properties.setProperty(WORDS_LEARNED_KEY, Integer.toString(session.getWordsLearned()));
        int index = 0;
        for (Map.Entry<String, WordProgress> entry : session.getAllProgress().entrySet()) {
            String prefix = "word." + index++ + ".";
            properties.setProperty(prefix + "text", entry.getKey());
            properties.setProperty(prefix + "box", Integer.toString(entry.getValue().getBox()));
            properties.setProperty(prefix + "consecutiveCorrect", Integer.toString(entry.getValue().getConsecutiveCorrect()));
            properties.setProperty(prefix + "nextReviewDate", entry.getValue().getNextReviewDate().toString());
        }

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temporaryFile = file.resolveSibling(file.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
            properties.store(writer, "Language Learning App progress");
        }
        Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
    }

    private Map<String, WordProgress> readWords(Properties properties) throws IOException {
        Map<String, WordProgress> progress = new LinkedHashMap<>();
        try {
            for (int index = 0; properties.containsKey("word." + index + ".text"); index++) {
                String prefix = "word." + index + ".";
                String word = required(properties, prefix + "text");
                String nextReviewDate = properties.getProperty(prefix + "nextReviewDate", LocalDate.MIN.toString());
                progress.put(word, new WordProgress(nonNegative(properties, prefix + "box"),
                        nonNegative(properties, prefix + "consecutiveCorrect"),
                        LocalDate.parse(nextReviewDate)));
            }
            return progress;
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid word progress in session file", exception);
        }
    }

    private String required(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IOException("Missing session property: " + key);
        }
        return value;
    }

    private int nonNegative(Properties properties, String key) throws IOException {
        try {
            int value = Integer.parseInt(required(properties, key));
            if (value < 0) {
                throw new NumberFormatException("negative value");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid session property: " + key, exception);
        }
    }
}
