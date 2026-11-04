package org.example.decorator;

import org.example.Word;

public class BasicWordCard implements WordCard {
    private final Word word;

    public BasicWordCard(Word word) {
        this.word = word;
    }

    @Override
    public Word getWord() {
        return word;
    }

    @Override
    public String render() {
        return word.toString();
    }
}
