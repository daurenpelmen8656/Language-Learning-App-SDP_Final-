package org.example.factory;

/**
 * Creator of the Factory Method pattern.
 *
 * <p>{@link #create} is the template: it is the same for every exercise type.
 * Subclasses only implement the factory method {@link #createExercise},
 * which decides <em>which</em> concrete {@link Exercise} is instantiated.</p>
 */
public abstract class ExerciseCreator {

    public final Exercise create(String prompt, String expectedAnswer) {
        return createExercise(prompt, expectedAnswer);
    }

    /** The factory method. */
    protected abstract Exercise createExercise(String prompt, String expectedAnswer);
}
