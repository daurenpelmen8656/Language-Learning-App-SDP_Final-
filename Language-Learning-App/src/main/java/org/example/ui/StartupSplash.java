package org.example.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Анимированный splash-экран при запуске приложения.
 */
public final class StartupSplash {

    private StartupSplash() {
    }

    public static void showAndWait() {
        CountDownLatch done = new CountDownLatch(1);
        AtomicBoolean finished = new AtomicBoolean(false);
        AtomicReference<Stage> stageRef = new AtomicReference<>();

        try {
            JavaFxSupport.ensureStarted();
            Platform.runLater(() -> {
                try {
                    showSplash(done, finished, stageRef);
                } catch (Exception ex) {
                    markDone(done, finished);
                }
            });

            if (!done.await(6, TimeUnit.SECONDS)) {
                System.out.println("[UI] Splash пропущен по таймауту — открываем меню.");
                Stage stage = stageRef.get();
                if (stage != null) {
                    Platform.runLater(() -> {
                        try {
                            stage.close();
                        } catch (Exception ignored) {
                            // ignore
                        }
                    });
                }
                markDone(done, finished);
            }
        } catch (Exception e) {
            System.out.println("[UI] Splash недоступен (" + e.getMessage() + ") — открываем меню.");
            markDone(done, finished);
        }
    }

    private static void markDone(CountDownLatch done, AtomicBoolean finished) {
        if (finished.compareAndSet(false, true)) {
            done.countDown();
        }
    }

    private static void showSplash(
            CountDownLatch done,
            AtomicBoolean finished,
            AtomicReference<Stage> stageRef
    ) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stageRef.set(stage);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle("Language Learning App");

        Label brand = new Label("Language Learning");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        brand.setTextFill(Color.WHITE);
        brand.setOpacity(0);
        brand.setScaleX(0.7);
        brand.setScaleY(0.7);

        Label subtitle = new Label("SDP Final Project  ·  SE-2535");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#DCEBFF"));
        subtitle.setOpacity(0);

        Label team = new Label("Dauren  ·  Yelzhan  ·  Turar");
        team.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        team.setTextFill(Color.web("#A8C8FF"));
        team.setOpacity(0);

        Circle pulse = new Circle(70, Color.web("#4F8CFF", 0.25));
        pulse.setStroke(Color.web("#9EC1FF", 0.55));
        pulse.setStrokeWidth(2);
        pulse.setScaleX(0.4);
        pulse.setScaleY(0.4);
        pulse.setOpacity(0);

        VBox textBox = new VBox(8, brand, subtitle, team);
        textBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(pulse, textBox);
        root.setPadding(new Insets(40));
        root.setPrefSize(460, 280);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1F3A, #1A3A6B, #2457A6);");

        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();

        FadeTransition brandFade = new FadeTransition(Duration.millis(600), brand);
        brandFade.setFromValue(0);
        brandFade.setToValue(1);

        ScaleTransition brandScale = new ScaleTransition(Duration.millis(600), brand);
        brandScale.setFromX(0.7);
        brandScale.setFromY(0.7);
        brandScale.setToX(1.0);
        brandScale.setToY(1.0);

        FadeTransition subFade = new FadeTransition(Duration.millis(450), subtitle);
        subFade.setFromValue(0);
        subFade.setToValue(1);

        FadeTransition teamFade = new FadeTransition(Duration.millis(450), team);
        teamFade.setFromValue(0);
        teamFade.setToValue(1);

        FadeTransition pulseFade = new FadeTransition(Duration.millis(500), pulse);
        pulseFade.setFromValue(0);
        pulseFade.setToValue(1);

        ScaleTransition pulseScale = new ScaleTransition(Duration.millis(900), pulse);
        pulseScale.setFromX(0.4);
        pulseScale.setFromY(0.4);
        pulseScale.setToX(1.1);
        pulseScale.setToY(1.1);
        pulseScale.setAutoReverse(true);
        pulseScale.setCycleCount(2);

        FadeTransition out = new FadeTransition(Duration.millis(350), root);
        out.setFromValue(1);
        out.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(
                new ParallelTransition(brandFade, brandScale, pulseFade),
                new ParallelTransition(subFade, teamFade),
                pulseScale,
                new PauseTransition(Duration.millis(250)),
                out
        );

        Runnable complete = () -> {
            try {
                stage.close();
            } catch (Exception ignored) {
                // ignore
            }
            markDone(done, finished);
        };

        sequence.setOnFinished(e -> complete.run());
        stage.setOnCloseRequest(e -> {
            sequence.stop();
            complete.run();
        });
        sequence.play();
    }
}
