package org.example.singleton;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDate;
import java.util.LinkedHashMap;

/**
 * Singleton Pattern — maintains global state for the active user session.
 */
public class UserSession implements UserProgress {
    private static volatile UserSession instance;

    private String username;
    private int xp;
    private int streak;
    private int wordsLearned;
    private final Map<String, WordProgress> progressMap;

    private UserSession() {
        this.username = "Dauren";
        this.xp = 0;
        this.streak = 0;
        this.wordsLearned = 0;
        this.progressMap = new LinkedHashMap<>();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int amount) {
        this.xp += amount;
    }

    public int getStreak() {
        return streak;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public void resetStreak() {
        this.streak = 0;
    }

    public int getWordsLearned() {
        return wordsLearned;
    }

    public void incrementWordsLearned() {
        this.wordsLearned++;
    }

    public synchronized WordProgress getProgress(String wordText) {
        return progressMap.computeIfAbsent(wordText.toLowerCase(Locale.ROOT), k -> new WordProgress());
    }

    @Override
    public Map<String, WordProgress> getAllProgress() {
        return Collections.unmodifiableMap(progressMap);
    }

    @Override
    public Map<String, WordProgress> getDueProgress(LocalDate date) {
        Map<String, WordProgress> dueProgress = new LinkedHashMap<>();
        progressMap.forEach((word, progress) -> {
            if (!progress.getNextReviewDate().isAfter(date)) {
                dueProgress.put(word, progress);
            }
        });
        return Collections.unmodifiableMap(dueProgress);
    }
}
