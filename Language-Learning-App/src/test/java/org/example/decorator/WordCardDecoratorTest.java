package org.example.decorator;

import org.example.Word;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordCardDecoratorTest {
    @Test
    void severalDecoratorsCanBeWrappedInSequence() {
        WordCard card = new BasicWordCard(new Word("apple", "яблоко"));
        card = new ExampleDecorator(card, "I eat an apple.");
        card = new AudioDecorator(card, "audio/apple.mp3");
        card = new ImageHintDecorator(card, "images/apple.png");

        assertEquals("apple - яблоко | Example: I eat an apple. | Audio: audio/apple.mp3 | Image hint: images/apple.png", card.render());
    }

    @Test
    void differentOrderPreservesAllFeatures() {
        WordCard card = new ExampleDecorator(new ImageHintDecorator(
                new AudioDecorator(new BasicWordCard(new Word("apple", "яблоко")), "audio/apple.mp3"),
                "images/apple.png"), "I eat an apple.");
        String rendered = card.render();
        assertTrue(rendered.contains("apple - яблоко"));
        assertTrue(rendered.contains("Example: I eat an apple."));
        assertTrue(rendered.contains("Audio: audio/apple.mp3"));
        assertTrue(rendered.contains("Image hint: images/apple.png"));
    }
}
