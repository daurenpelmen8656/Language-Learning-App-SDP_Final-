package org.example.singleton;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton Pattern — maintains global state for the active user session.
 */
public class UserSession implements UserProgress {
    public static final String DEFAULT_USERNAME = "Guest";
    private static volatile UserSession instance;

    private String username;
    private int xp;
    private int streak;
    private int wordsLearned;
    private final Map<String, WordProgress> progressMap;

    private UserSession() {
        progressMap = new LinkedHashMap<>();
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

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        this.username = username;
    }

    public synchronized int getXp() {
        return xp;
    }

    public synchronized void addXp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP cannot be negative");
        }
        this.xp += amount;
    }

    public synchronized int getStreak() {
        return streak;
    }

    public synchronized void incrementStreak() {
        this.streak++;
    }

    public synchronized void resetStreak() {
        this.streak = 0;
    }

    public synchronized int getWordsLearned() {
        return wordsLearned;
    }

    public synchronized void incrementWordsLearned() {
        this.wordsLearned++;
    }

    public synchronized WordProgress getProgress(String wordText) {
        if (wordText == null || wordText.isBlank()) {
            throw new IllegalArgumentException("Word must not be blank");
        }
        return progressMap.computeIfAbsent(wordText.toLowerCase(Locale.ROOT), k -> new WordProgress());
    }

    @Override
    public synchronized Map<String, WordProgress> getAllProgress() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(progressMap));
    }

    @Override
    public synchronized Map<String, WordProgress> getDueProgress(LocalDate date) {
        Objects.requireNonNull(date);
        Map<String, WordProgress> dueProgress = new LinkedHashMap<>();
        progressMap.forEach((word, progress) -> {
            if (!progress.getNextReviewDate().isAfter(date)) {
                dueProgress.put(word, progress);
            }
        });
        return Collections.unmodifiableMap(dueProgress);
    }

    public synchronized void reset() {
        resetState();
    }

    synchronized SessionSnapshot snapshot() {
        Map<String, WordProgress> copy = new LinkedHashMap<>();
        progressMap.forEach((word, progress) -> copy.put(word, copyProgress(progress)));
        return new SessionSnapshot(username, xp, streak, wordsLearned, copy);
    }

    synchronized void restore(SessionSnapshot snapshot) {
        Objects.requireNonNull(snapshot);
        username = snapshot.username();
        xp = snapshot.xp();
        streak = snapshot.streak();
        wordsLearned = snapshot.wordsLearned();
        progressMap.clear();
        snapshot.progress().forEach((word, progress) -> progressMap.put(word, copyProgress(progress)));
    }

    private void resetState() {
        username = DEFAULT_USERNAME;
        xp = 0;
        streak = 0;
        wordsLearned = 0;
        progressMap.clear();
    }

    private static WordProgress copyProgress(WordProgress progress) {
        return new WordProgress(progress.getBox(), progress.getConsecutiveCorrect(), progress.getNextReviewDate());
    }
}
