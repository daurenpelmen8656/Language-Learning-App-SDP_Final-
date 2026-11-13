package org.example.singleton;

import java.time.LocalDate;
import java.util.Map;

public interface UserProgress {
    String getUsername();
    int getXp();
    void addXp(int amount);
    int getStreak();
    void incrementStreak();
    void resetStreak();
    int getWordsLearned();
    void incrementWordsLearned();
    WordProgress getProgress(String wordText);
    Map<String, WordProgress> getAllProgress();
    Map<String, WordProgress> getDueProgress(LocalDate date);
}
