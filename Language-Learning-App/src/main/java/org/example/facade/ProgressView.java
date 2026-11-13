package org.example.facade;

import java.util.Map;

public record ProgressView(String username, int xp, int streak, int wordsLearned,
                           String strategyName, Map<String, WordProgressView> words) {
    public ProgressView {
        words = Map.copyOf(words);
    }
}
