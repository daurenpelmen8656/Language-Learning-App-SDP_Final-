package org.example.singleton;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton Pattern — the single, global state of the active user session.
 * Implements {@link UserProgress}, so clients may depend on the interface.
 */
public class UserSession implements UserProgress {
    public static final String DEFAULT_USERNAME = "Guest";

    private static volatile UserSession instance;

    private String username;
    private int xp;
    private int streak;
    private int wordsLearned;
    private final Map<String, WordProgress> progressMap = new LinkedHashMap<>();

    private UserSession() {
        resetState();
    }

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the session to its initial state (same instance, empty data).
     * Intended for tests, so that they do not depend on each other, and for "log out".
     */
    public synchronized void reset() {
        resetState();
    }

    private void resetState() {
        username = DEFAULT_USERNAME;
        xp = 0;
        streak = 0;
        wordsLearned = 0;
        progressMap.clear();
    }

    @Override
    public synchronized String getUsername() {
        return username;
    }

    @Override
    public synchronized void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        this.username = username;
    }

    @Override
    public synchronized int getXp() {
        return xp;
    }

    @Override
    public synchronized void addXp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP нельзя уменьшить: " + amount);
        }
        this.xp += amount;
    }

    @Override
    public synchronized int getStreak() {
        return streak;
    }

    @Override
    public synchronized void incrementStreak() {
        this.streak++;
    }

    @Override
    public synchronized void resetStreak() {
        this.streak = 0;
    }

    @Override
    public synchronized int getWordsLearned() {
        return wordsLearned;
    }

    @Override
    public synchronized void incrementWordsLearned() {
        this.wordsLearned++;
    }

    @Override
    public synchronized WordProgress getProgress(String wordText) {
        if (wordText == null || wordText.isBlank()) {
            throw new IllegalArgumentException("Слово не может быть пустым");
        }
        return progressMap.computeIfAbsent(wordText.toLowerCase(), k -> new WordProgress());
    }

    /** Read-only snapshot of the progress of all words. */
    @Override
    public synchronized Map<String, WordProgress> getAllProgress() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(progressMap));
    }
}
