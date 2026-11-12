package org.example.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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

    @Test
    void getInstanceIsThreadSafe() throws Exception {
        int threads = 16;
        Set<UserSession> instances = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                instances.add(UserSession.getInstance());
            });
            workers.add(t);
            t.start();
        }
        start.countDown();
        for (Thread t : workers) {
            t.join();
        }
        assertEquals(1, instances.size(), "Все потоки должны получить один и тот же экземпляр");
    }

    @Test
    void concurrentUpdatesAreNotLost() throws Exception {
        UserSession session = UserSession.getInstance();
        int threads = 8;
        int perThread = 1000;
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                for (int n = 0; n < perThread; n++) {
                    session.addXp(1);
                    session.incrementStreak();
                }
            });
            workers.add(t);
            t.start();
        }
        for (Thread t : workers) {
            t.join();
        }
        assertEquals(threads * perThread, session.getXp());
        assertEquals(threads * perThread, session.getStreak());
    }
}
