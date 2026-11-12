package org.example.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @BeforeEach
    void cleanSession() {
        UserSession.getInstance().reset(); // tests stay independent of each other
    }

    @Test
    void alwaysReturnsTheSameInstance() {
        assertSame(UserSession.getInstance(), UserSession.getInstance());
    }

    @Test
    void defaultUsernameIsNotHardcodedToATeamMember() {
        assertEquals(UserSession.DEFAULT_USERNAME, UserSession.getInstance().getUsername());
    }

    @Test
    void stateIsSharedBetweenClientsOfTheSingleton() {
        UserProgress a = UserSession.getInstance();
        UserProgress b = UserSession.getInstance();

        a.addXp(10);
        a.incrementStreak();
        a.incrementWordsLearned();
        a.setUsername("Aigerim");

        assertEquals(10, b.getXp());
        assertEquals(1, b.getStreak());
        assertEquals(1, b.getWordsLearned());
        assertEquals("Aigerim", b.getUsername());
    }

    @Test
    void resetClearsEverythingButKeepsTheInstance() {
        UserSession session = UserSession.getInstance();
        session.addXp(50);
        session.setUsername("Aigerim");
        session.getProgress("apple").recordAnswer(true);

        session.reset();

        assertSame(session, UserSession.getInstance());
        assertEquals(0, session.getXp());
        assertEquals(UserSession.DEFAULT_USERNAME, session.getUsername());
        assertTrue(session.getAllProgress().isEmpty());
    }

    @Test
    void progressIsCaseInsensitiveAndCreatedOnDemand() {
        UserSession session = UserSession.getInstance();
        WordProgress first = session.getProgress("Apple");
        assertSame(first, session.getProgress("apple"));
    }

    @Test
    void invalidInputIsRejected() {
        UserSession session = UserSession.getInstance();
        assertThrows(IllegalArgumentException.class, () -> session.setUsername(" "));
        assertThrows(IllegalArgumentException.class, () -> session.addXp(-5));
        assertThrows(IllegalArgumentException.class, () -> session.getProgress(null));
    }

    @Test
    void getAllProgressIsAReadOnlySnapshot() {
        UserSession session = UserSession.getInstance();
        session.getProgress("apple");

        assertThrows(UnsupportedOperationException.class,
                () -> session.getAllProgress().put("x", new WordProgress()));
        assertEquals(1, session.getAllProgress().size());
    }
}
