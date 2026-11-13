package org.example.observer;

import java.util.Optional;

/**
 * Observer that schedules notifications/reminders for reviewing words.
 */
public class ReminderService implements ProgressObserver {
    private ProgressEvent latestReminder;

    @Override
    public void update(ProgressEvent event) {
        latestReminder = event;
    }

    public Optional<ProgressEvent> getLatestReminder() {
        return Optional.ofNullable(latestReminder);
    }
}
