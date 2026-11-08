package org.example.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject maintaining subscribers and broadcasting events.
 */
public class ProgressPublisher implements ProgressSubject {
    private final List<ProgressObserver> observers = new ArrayList<>();

    @Override
    public void attach(ProgressObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(ProgressObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ProgressEvent event) {
        for (ProgressObserver observer : observers) {
            observer.update(event);
        }
    }
}
