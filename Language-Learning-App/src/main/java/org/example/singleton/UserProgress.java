package org.example.singleton;

import java.util.Map;

/**
 * What the rest of the app needs from the user session.
 * Code can depend on this interface (Dependency Inversion) instead of the
 * concrete {@link UserSession} singleton, which makes it easy to mock in tests.
 */
public interface UserProgress {
    String getUsername();

    void setUsername(String username);

    int getXp();

    void addXp(int amount);

    int getStreak();

    void incrementStreak();

    void resetStreak();

    int getWordsLearned();

    void incrementWordsLearned();

    WordProgress getProgress(String wordText);

    Map<String, WordProgress> getAllProgress();
}
