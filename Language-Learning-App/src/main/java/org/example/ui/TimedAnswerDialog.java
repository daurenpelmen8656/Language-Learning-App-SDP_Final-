package org.example.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Окно ответа с анимированным круговым таймером.
 * Возвращает ответ пользователя или empty при истечении времени / отмене.
 */
public final class TimedAnswerDialog {

    private static final double RING_RADIUS = 48;
    private static final double RING_CENTER = 60;
    private static final double RING_SIZE = 120;

    private TimedAnswerDialog() {
    }

    public static Optional<String> ask(String title, String prompt, int seconds) {
        JavaFxSupport.ensureStarted();

        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Optional<String>> result = new AtomicReference<>(Optional.empty());

        Platform.runLater(() -> buildAndShow(title, prompt, seconds, result, done));

        try {
            done.await(seconds + 30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result.get() == null ? Optional.empty() : result.get();
    }

    private static void buildAndShow(
            String title,
            String prompt,
            int seconds,
            AtomicReference<Optional<String>> result,
            CountDownLatch done
    ) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle(title);

        Label header = new Label(title);
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        header.setTextFill(Color.web("#EAF2FF"));

        Label promptLabel = new Label(prompt);
        promptLabel.setWrapText(true);
        promptLabel.setMaxWidth(360);
        promptLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        promptLabel.setTextFill(Color.WHITE);

        Label secondsLabel = new Label(String.valueOf(seconds));
        secondsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        secondsLabel.setTextFill(Color.WHITE);

        // Фиксированные координаты — кольцо не «уезжает» при анимации
        Pane ringPane = new Pane();
        ringPane.setPrefSize(RING_SIZE, RING_SIZE);
        ringPane.setMinSize(RING_SIZE, RING_SIZE);
        ringPane.setMaxSize(RING_SIZE, RING_SIZE);

        Circle track = new Circle(RING_CENTER, RING_CENTER, RING_RADIUS);
        track.setFill(Color.TRANSPARENT);
        track.setStroke(Color.web("#2A4A7A"));
        track.setStrokeWidth(8);

        double circumference = 2 * Math.PI * RING_RADIUS;
        Circle progress = new Circle(RING_CENTER, RING_CENTER, RING_RADIUS);
        progress.setFill(Color.TRANSPARENT);
        progress.setStroke(Color.web("#4FD1C5"));
        progress.setStrokeWidth(8);
        progress.setStrokeLineCap(StrokeLineCap.ROUND);
        progress.getStrokeDashArray().setAll(circumference);
        progress.setStrokeDashOffset(0);
        progress.setRotate(-90);

        ringPane.getChildren().addAll(track, progress);

        StackPane timerPane = new StackPane(ringPane, secondsLabel);
        timerPane.setPrefSize(RING_SIZE, RING_SIZE);
        timerPane.setMinSize(RING_SIZE, RING_SIZE);
        timerPane.setMaxSize(RING_SIZE, RING_SIZE);

        TextField answerField = new TextField();
        answerField.setPromptText("Введите ответ...");
        answerField.setPrefWidth(320);
        answerField.setStyle(
                "-fx-background-color: #12284A;"
                        + "-fx-text-fill: white;"
                        + "-fx-prompt-text-fill: #8AA4C8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 10;"
                        + "-fx-font-size: 14px;"
        );

        Button submit = new Button("Ответить");
        submit.setDefaultButton(true);
        submit.setStyle(
                "-fx-background-color: #3D7EFF;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 8 18;"
        );

        Button skip = new Button("Пропустить");
        skip.setCancelButton(true);
        skip.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: #A8C0E0;"
                        + "-fx-border-color: #3A567A;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 8 14;"
        );

        HBox actions = new HBox(10, submit, skip);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, header, timerPane, promptLabel, answerField, actions);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #0E223F, #173862);");

        stage.setScene(new Scene(content));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.strokeDashOffsetProperty(), 0)),
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(progress.strokeDashOffsetProperty(), circumference))
        );

        Timeline tick = new Timeline();
        for (int i = seconds; i >= 0; i--) {
            final int left = i;
            tick.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(seconds - i),
                    e -> {
                        secondsLabel.setText(String.valueOf(left));
                        if (left <= 5) {
                            progress.setStroke(Color.web("#FF6B6B"));
                            secondsLabel.setTextFill(Color.web("#FFB4B4"));
                        } else if (left <= seconds / 2.0) {
                            progress.setStroke(Color.web("#FFC857"));
                        }
                    }
            ));
        }

        AtomicBoolean finished = new AtomicBoolean(false);
        Consumer<String> finishWith = answer -> {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            timeline.stop();
            tick.stop();
            result.set(Optional.ofNullable(answer).filter(s -> !s.isBlank()));
            stage.close();
            done.countDown();
        };

        submit.setOnAction(e -> finishWith.accept(answerField.getText()));
        skip.setOnAction(e -> finishWith.accept(null));
        answerField.setOnAction(e -> finishWith.accept(answerField.getText()));
        timeline.setOnFinished(e -> finishWith.accept(null));
        stage.setOnCloseRequest(e -> {
            e.consume();
            finishWith.accept(null);
        });

        stage.centerOnScreen();
        stage.show();
        stage.toFront();
        answerField.requestFocus();

        timeline.playFromStart();
        tick.playFromStart();
    }
}
