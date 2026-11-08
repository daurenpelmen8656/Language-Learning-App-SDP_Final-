package org.example.observer;

/**
 * Observer that schedules notifications/reminders for reviewing words.
 */
public class ReminderService implements ProgressObserver {

    @Override
    public void update(ProgressEvent event) {
        System.out.println("[Reminder Service] ⏰ Слово \"" + event.getWordText()
                + "\" запланировано на повторение через "
                + event.getNextReviewDays() + " дн.");
    }
}
