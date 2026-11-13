package org.example.ui;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Инициализация JavaFX Toolkit из консольного приложения.
 */
public final class JavaFxSupport {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    private JavaFxSupport() {
    }

    public static void ensureStarted() {
        if (STARTED.get()) {
            return;
        }
        synchronized (JavaFxSupport.class) {
            if (STARTED.get()) {
                return;
            }
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(() -> {
                    Platform.setImplicitExit(false);
                    latch.countDown();
                });
                latch.await();
                STARTED.set(true);
            } catch (IllegalStateException alreadyStarted) {
                // Toolkit уже запущен (например, из Splash)
                Platform.setImplicitExit(false);
                STARTED.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Не удалось запустить JavaFX", e);
            }
        }
    }

    public static void runAndWait(Runnable action) {
        ensureStarted();
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ожидание JavaFX-потока прервано", e);
        }
    }
}
