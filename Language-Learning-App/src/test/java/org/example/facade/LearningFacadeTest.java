package org.example.facade;

import org.example.singleton.UserSession;
import org.example.singleton.WordProgress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LearningFacadeTest {

    @Test
    void submitAnswerUpdatesUserSessionAndProgress() {
        LearningFacade facade = new LearningFacade();
        UserSession session = UserSession.getInstance();

        int xpBefore = session.getXp();
        int streakBefore = session.getStreak();

        // Отправляем верный ответ для слова "window"
        facade.submitAnswer("window", true);

        // Проверяем, что XP увеличился (+10 от XpTracker)
        assertEquals(xpBefore + 10, session.getXp());
        // Проверяем, что серия увеличилась (+1 от StreakTracker)
        assertEquals(streakBefore + 1, session.getStreak());

        // Проверяем состояние прогресса слова
        WordProgress progress = session.getProgress("window");
        assertEquals(1, progress.getBox());
        assertEquals(1, progress.getConsecutiveCorrect());

        // Теперь отправляем неверный ответ
        facade.submitAnswer("window", false);

        // Проверяем сброс серии
        assertEquals(0, session.getStreak());
        assertEquals(0, progress.getBox());
    }
}
