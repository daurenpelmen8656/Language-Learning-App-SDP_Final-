package org.example.singleton;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SessionStorageTest {

    private Path dir;
    private Path file;
    private SessionStorage storage;
    private final UserSession session = UserSession.getInstance();

    @BeforeEach
    void setUp() throws IOException {
        session.reset();
        dir = Files.createTempDirectory("session-storage-test");
        file = dir.resolve("progress.properties");
        storage = new SessionStorage(file);
    }

    @AfterEach
    void tearDown() throws IOException {
        session.reset();
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    private void fillSession() {
        session.setUsername("Айгерим");
        session.addXp(30);
        session.incrementStreak();
        session.incrementStreak();
        session.incrementWordsLearned();
        session.getProgress("apple").recordAnswer(true);
        session.getProgress("apple").recordAnswer(true);
        session.getProgress("window").recordAnswer(true);
        session.getProgress("book"); // created, but never answered
    }

    @Test
    void savedProgressSurvivesRestart() throws IOException {
        fillSession();
        storage.save(session);

        session.reset(); // simulates restarting the application
        assertEquals(0, session.getXp());

        assertTrue(storage.load(session));

        assertEquals("Айгерим", session.getUsername(), "Кириллица должна сохраняться");
        assertEquals(30, session.getXp());
        assertEquals(2, session.getStreak());
        assertEquals(1, session.getWordsLearned());
        assertEquals(3, session.getAllProgress().size());
        assertEquals(2, session.getProgress("apple").getBox());
        assertEquals(2, session.getProgress("apple").getConsecutiveCorrect());
        assertEquals(1, session.getProgress("window").getBox());
        assertEquals(0, session.getProgress("book").getBox());
    }

    @Test
    void loadWithoutFileReturnsFalseAndKeepsState() throws IOException {
        session.addXp(5);

        assertFalse(storage.load(session));

        assertEquals(5, session.getXp());
    }

    @Test
    void loadReplacesCurrentState() throws IOException {
        fillSession();
        storage.save(session);

        session.reset();
        session.addXp(999);
        session.getProgress("other");
        storage.load(session);

        assertEquals(30, session.getXp());
        assertEquals(3, session.getAllProgress().size());
    }

    @Test
    void secondSaveOverwritesFirstAndLeavesNoTempFile() throws IOException {
        fillSession();
        storage.save(session);
        session.addXp(10);
        storage.save(session);

        session.reset();
        storage.load(session);

        assertEquals(40, session.getXp());
        try (Stream<Path> files = Files.list(dir)) {
            assertEquals(1L, files.count(), "В папке должен остаться только один файл");
        }
    }

    @Test
    void saveCreatesMissingFolders() throws IOException {
        Path nested = dir.resolve("a").resolve("b").resolve("progress.properties");
        new SessionStorage(nested).save(session);

        assertTrue(Files.exists(nested));
    }

    @Test
    void fileIsReadableUtf8Text() throws IOException {
        fillSession();
        storage.save(session);

        String text = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(text.contains("username=Айгерим"));
        assertTrue(text.contains("xp=30"));
        assertTrue(text.contains("apple"));
    }

    @Test
    void damagedNumberThrowsAndLeavesSessionUntouched() throws IOException {
        Files.writeString(file, "username=Test\nxp=abc\nstreak=0\nwordsLearned=0\n", StandardCharsets.UTF_8);
        session.addXp(7);

        IOException e = assertThrows(IOException.class, () -> storage.load(session));

        assertTrue(e.getMessage().contains("повреждён"));
        assertEquals(7, session.getXp(), "Состояние не должно измениться");
        assertEquals(UserSession.DEFAULT_USERNAME, session.getUsername());
    }

    @Test
    void missingFieldThrows() throws IOException {
        Files.writeString(file, "username=Test\nxp=1\n", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> storage.load(session));
    }

    @Test
    void negativeValueThrows() throws IOException {
        Files.writeString(file, "username=Test\nxp=-5\nstreak=0\nwordsLearned=0\n", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> storage.load(session));
    }

    @Test
    void damagedWordRecordDoesNotLoadHalfOfTheFile() throws IOException {
        Files.writeString(file, "username=Test\nxp=50\nstreak=1\nwordsLearned=1\n"
                + "word.0.text=apple\nword.0.box=1\nword.0.consecutiveCorrect=1\n"
                + "word.1.text=book\nword.1.box=oops\nword.1.consecutiveCorrect=0\n", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> storage.load(session));

        assertEquals(0, session.getXp(), "Ничего из повреждённого файла не должно загрузиться");
        assertTrue(session.getAllProgress().isEmpty());
    }

    @Test
    void nullPathIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SessionStorage(null));
    }
}
