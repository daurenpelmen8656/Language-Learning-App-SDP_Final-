package org.example.singleton;

import java.util.Map;

/**
 * Immutable copy of everything stored in a {@link UserSession}.
 * Used to save the session to a file and to restore it later.
 * Package-private: only {@link UserSession} and {@link SessionStorage} use it.
 */
record SessionSnapshot(String username, int xp, int streak, int wordsLearned,
                       Map<String, WordProgress> progress) {
}
