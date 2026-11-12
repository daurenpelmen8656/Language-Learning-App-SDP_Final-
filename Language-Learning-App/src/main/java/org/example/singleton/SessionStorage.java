package org.example.singleton;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Saves a {@link UserSession} to a text file and loads it back,
 * so the learning progress survives an application restart.
 *
 * <p>The file is a plain UTF-8 {@code .properties} text file, for example:</p>
 * <pre>
 * username=Aigerim
 * xp=30
 * streak=3
 * wordsLearned=3
 * word.0.text=apple
 * word.0.box=1
 * word.0.consecutiveCorrect=1
 * </pre>
 *
 * Saving is safe against crashes (written to a temp file first, then moved),
 * and loading is all-or-nothing: a damaged file never leaves a half-loaded session.
 */
public class SessionStorage {
    public static final Path DEFAULT_FILE = Path.of("progress.properties");

    private final Path file;

    public SessionStorage() {
        this(DEFAULT_FILE);
    }

    public SessionStorage(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    /** Writes the current state of the session to the file (replacing the old file). */
    public void save(UserSession session) throws IOException {
        SessionSnapshot snapshot = session.snapshot();

        Properties props = new Properties();
        props.setProperty("username", snapshot.username());
        props.setProperty("xp", String.valueOf(snapshot.xp()));
        props.setProperty("streak", String.valueOf(snapshot.streak()));
        props.setProperty("wordsLearned", String.valueOf(snapshot.wordsLearned()));
        int index = 0;
        for (Map.Entry<String, WordProgress> entry : snapshot.progress().entrySet()) {
            String prefix = "word." + index++ + ".";
            props.setProperty(prefix + "text", entry.getKey());
            props.setProperty(prefix + "box", String.valueOf(entry.getValue().getBox()));
            props.setProperty(prefix + "consecutiveCorrect",
                    String.valueOf(entry.getValue().getConsecutiveCorrect()));
        }

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            props.store(writer, "Language Learning App - user progress");
        }
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Loads the session from the file.
     *
     * @return {@code true} if the file existed and was loaded, {@code false} if there is no file yet
     *         (first launch) — the session is then left untouched
     * @throws IOException if the file cannot be read or its content is damaged;
     *                     the session is not changed in that case
     */
    public boolean load(UserSession session) throws IOException {
        if (!Files.exists(file)) {
            return false;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        session.restore(parse(props)); // restore only after everything was parsed successfully
        return true;
    }

    private SessionSnapshot parse(Properties props) throws IOException {
        try {
            String username = required(props, "username");
            if (username.isBlank()) {
                throw new IllegalArgumentException("пустое имя пользователя");
            }
            int xp = nonNegative(props, "xp");
            int streak = nonNegative(props, "streak");
            int wordsLearned = nonNegative(props, "wordsLearned");

            Map<String, WordProgress> progress = new LinkedHashMap<>();
            for (int i = 0; props.containsKey("word." + i + ".text"); i++) {
                String prefix = "word." + i + ".";
                String text = required(props, prefix + "text");
                if (text.isBlank()) {
                    throw new IllegalArgumentException("пустое слово в записи " + i);
                }
                progress.put(text, new WordProgress(
                        nonNegative(props, prefix + "box"),
                        nonNegative(props, prefix + "consecutiveCorrect")));
            }
            return new SessionSnapshot(username, xp, streak, wordsLearned, progress);
        } catch (IllegalArgumentException e) { // includes NumberFormatException
            throw new IOException("Файл прогресса повреждён: " + e.getMessage(), e);
        }
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("нет поля '" + key + "'");
        }
        return value;
    }

    private static int nonNegative(Properties props, String key) {
        int value = Integer.parseInt(required(props, key).trim());
        if (value < 0) {
            throw new IllegalArgumentException("отрицательное значение в поле '" + key + "'");
        }
        return value;
    }
}
