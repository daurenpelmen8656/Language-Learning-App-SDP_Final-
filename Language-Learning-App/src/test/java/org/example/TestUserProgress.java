package org.example;

import org.example.singleton.UserProgress;
import org.example.singleton.WordProgress;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;

public final class TestUserProgress implements UserProgress {
    private final Map<String, WordProgress> progressByWord = new LinkedHashMap<>();
    private int xp;
    private int streak;
    private int wordsLearned;

    @Override public String getUsername() { return "Test learner"; }
    @Override public void setUsername(String username) { throw new UnsupportedOperationException(); }
    @Override public int getXp() { return xp; }
    @Override public void addXp(int amount) { xp += amount; }
    @Override public int getStreak() { return streak; }
    @Override public void incrementStreak() { streak++; }
    @Override public void resetStreak() { streak = 0; }
    @Override public int getWordsLearned() { return wordsLearned; }
    @Override public void incrementWordsLearned() { wordsLearned++; }

    @Override
    public WordProgress getProgress(String wordText) {
        return progressByWord.computeIfAbsent(wordText.toLowerCase(Locale.ROOT), ignored -> new WordProgress());
    }

    @Override
    public Map<String, WordProgress> getAllProgress() {
        return Collections.unmodifiableMap(progressByWord);
    }

    @Override
    public Map<String, WordProgress> getDueProgress(LocalDate date) {
        Map<String, WordProgress> dueWords = new LinkedHashMap<>();
        progressByWord.forEach((word, progress) -> {
            if (!progress.getNextReviewDate().isAfter(date)) {
                dueWords.put(word, progress);
            }
        });
        return Collections.unmodifiableMap(dueWords);
    }
}
