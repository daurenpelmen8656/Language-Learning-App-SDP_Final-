package org.example.decorator;

import org.example.Word;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCardDecoratorTest {
    @Test
    void severalDecoratorsCanBeWrappedInSequence() {
        WordCard card = new BasicWordCard(new Word("apple", "яблоко"));
        card = new ExampleDecorator(card, "I eat an apple.");
        card = new AudioDecorator(card, "audio/apple.mp3");
        card = new ImageHintDecorator(card, "images/apple.png");

        assertEquals("apple - яблоко | Example: I eat an apple. | Audio: audio/apple.mp3 | Image hint: images/apple.png", card.render());
    }
}
