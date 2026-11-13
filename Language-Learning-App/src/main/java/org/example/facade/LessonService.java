package org.example.facade;

import org.example.Word;
import org.example.adapter.DictionaryService;
import org.example.builder.Lesson;
import org.example.builder.LessonBuilder;
import org.example.decorator.AudioDecorator;
import org.example.decorator.BasicWordCard;
import org.example.decorator.ExampleDecorator;
import org.example.decorator.ImageHintDecorator;
import org.example.factory.Exercise;
import org.example.factory.ExerciseFactory;

import java.util.List;
import java.util.Objects;

public final class LessonService {
    private static final String LESSON_TITLE = "Урок 1: Базовая лексика и предметы";
    private static final String LESSON_LEVEL = "Beginner (A1)";
    private static final String APPLE_WORD_KEY = "apple";
    private static final String WINDOW_WORD_KEY = "window";
    private static final String BOOK_WORD_KEY = "book";
    private static final String FLASHCARD_TYPE = "flashcard";
    private static final String TRANSLATION_TYPE = "translation";
    private static final String QUIZ_TYPE = "quiz";
    private static final String APPLE_EXAMPLE = "An apple a day keeps the doctor away";
    private static final String WINDOW_AUDIO = "audio/window.mp3";
    private static final String BOOK_IMAGE = "img/library_book.png";
    private static final String WINDOW_SENTENCE = "Open the window, please";
    private static final String WINDOW_TRANSLATION = "Открой окно, пожалуйста";
    private static final String BOOK_QUIZ_PROMPT = "A) Книга  B) Тетрадь (book = ?)";
    private static final String BOOK_QUIZ_ANSWER = "A";

    private final DictionaryService dictionaryService;

    public LessonService(DictionaryService dictionaryService) {
        this.dictionaryService = Objects.requireNonNull(dictionaryService);
    }

    public LessonView buildLesson() {
        Word apple = dictionaryService.findWord(APPLE_WORD_KEY);
        Word window = dictionaryService.findWord(WINDOW_WORD_KEY);
        Word book = dictionaryService.findWord(BOOK_WORD_KEY);

        List<CardView> cards = List.of(
                new CardView(apple.getText(), "Example", APPLE_EXAMPLE,
                        new ExampleDecorator(new BasicWordCard(apple), APPLE_EXAMPLE).render()),
                new CardView(window.getText(), "Audio", WINDOW_AUDIO,
                        new AudioDecorator(new BasicWordCard(window), WINDOW_AUDIO).render()),
                new CardView(book.getText(), "ImageHint", BOOK_IMAGE,
                        new ImageHintDecorator(new BasicWordCard(book), BOOK_IMAGE).render()));

        List<Exercise> exercises = List.of(
                ExerciseFactory.create(FLASHCARD_TYPE, apple.getText(), apple.getTranslation()),
                ExerciseFactory.create(TRANSLATION_TYPE, WINDOW_SENTENCE, WINDOW_TRANSLATION),
                ExerciseFactory.create(QUIZ_TYPE, BOOK_QUIZ_PROMPT, BOOK_QUIZ_ANSWER));

        Lesson lesson = new LessonBuilder()
                .setTitle(LESSON_TITLE)
                .setLevel(LESSON_LEVEL)
                .addWord(apple)
                .addWord(window)
                .addWord(book)
                .addExercise(exercises.get(0))
                .addExercise(exercises.get(1))
                .addExercise(exercises.get(2))
                .build();
        List<WordView> words = lesson.getWords().stream()
                .map(word -> new WordView(word.getText(), word.getTranslation(), word.getLevel()))
                .toList();
        return new LessonView(lesson.getTitle(), lesson.getLevel(), words, cards, lesson.getExercises());
    }

    public WordView findWord(String key) {
        Word word = dictionaryService.findWord(key);
        return new WordView(word.getText(), word.getTranslation(), word.getLevel());
    }
}
