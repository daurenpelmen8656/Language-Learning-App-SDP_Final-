package org.example.factory;

/** Product of the Factory Method pattern: a single task shown to the learner. */
public interface Exercise {
    boolean check(String answer);

    void displayTask();
}
