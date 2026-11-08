package org.example.observer;

/**
 * Subject interface in the Observer Pattern.
 */
public interface ProgressSubject {
    void attach(ProgressObserver observer);
    void detach(ProgressObserver observer);
    void notifyObservers(ProgressEvent event);
}
