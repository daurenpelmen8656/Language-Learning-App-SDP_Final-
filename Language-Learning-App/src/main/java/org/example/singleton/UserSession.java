package org.example.singleton;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton Pattern — maintains global state for the active user session.
 */
public class UserSession {
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
        return progressMap.computeIfAbsent(wordText.toLowerCase(), k -> new WordProgress());
    }

    public Map<String, WordProgress> getAllProgress() {
        return Collections.unmodifiableMap(progressMap);
    }
}
