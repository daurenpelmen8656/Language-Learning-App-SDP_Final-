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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LessonService {
    public static final int EXERCISES_PER_WORD = 3;
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
    private static final String QUIZ_OPTION_A = "A) ";
    private static final String QUIZ_OPTION_B = "  B) ";
    private static final String QUIZ_CORRECT_ANSWER = "A";

    private final DictionaryService dictionaryService;

    public LessonService(DictionaryService dictionaryService) {
        this.dictionaryService = Objects.requireNonNull(dictionaryService);
    }

    public LessonView buildLesson() {
        List<Word> dictionaryWords = dictionaryService.findAllWords();
        List<CardView> cards = buildDecoratorCards(dictionaryWords);
        List<Exercise> exercises = buildExercises(dictionaryWords);

        Lesson lesson = new LessonBuilder()
                .setTitle(LESSON_TITLE)
                .setLevel(LESSON_LEVEL)
                .addWords(dictionaryWords)
                .addExercises(exercises)
                .build();
        List<WordView> wordViews = lesson.getWords().stream()
                .map(word -> new WordView(word.getText(), word.getTranslation(),
                        word.getKazakhTranslation(), word.getLevel()))
                .toList();
        return new LessonView(lesson.getTitle(), lesson.getLevel(), wordViews, cards, lesson.getExercises());
    }

    public WordView findWord(String key) {
        Word word = dictionaryService.findWord(key);
        return new WordView(word.getText(), word.getTranslation(), word.getKazakhTranslation(), word.getLevel());
    }

    private List<CardView> buildDecoratorCards(List<Word> words) {
        Word apple = findWord(words, APPLE_WORD_KEY);
        Word window = findWord(words, WINDOW_WORD_KEY);
        Word book = findWord(words, BOOK_WORD_KEY);
        return List.of(
                new CardView(apple.getText(), "Example", APPLE_EXAMPLE,
                        new ExampleDecorator(new BasicWordCard(apple), APPLE_EXAMPLE).render()),
                new CardView(window.getText(), "Audio", WINDOW_AUDIO,
                        new AudioDecorator(new BasicWordCard(window), WINDOW_AUDIO).render()),
                new CardView(book.getText(), "ImageHint", BOOK_IMAGE,
                        new ImageHintDecorator(new BasicWordCard(book), BOOK_IMAGE).render()));
    }

    private List<Exercise> buildExercises(List<Word> words) {
        List<Exercise> exercises = new ArrayList<>(words.size() * EXERCISES_PER_WORD);
        for (int index = 0; index < words.size(); index++) {
            Word word = words.get(index);
            Word distractor = words.get((index + 1) % words.size());
            exercises.add(ExerciseFactory.create(FLASHCARD_TYPE, word.getText(), word.getTranslation()));
            exercises.add(ExerciseFactory.create(TRANSLATION_TYPE, word.getText(), word.getTranslation()));
            String quizPrompt = QUIZ_OPTION_A + word.getTranslation()
                    + QUIZ_OPTION_B + distractor.getTranslation();
            exercises.add(ExerciseFactory.create(QUIZ_TYPE, quizPrompt, QUIZ_CORRECT_ANSWER));
        }
        return List.copyOf(exercises);
    }

    private Word findWord(List<Word> words, String key) {
        return words.stream()
                .filter(word -> word.getText().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Required lesson word is missing: " + key));
    }
}
