package org.example.decorator;

import org.example.Word;

public abstract class WordCardDecorator implements WordCard {
    protected final WordCard card;

    protected WordCardDecorator(WordCard card) {
        this.card = card;
    }

    @Override
    public Word getWord() {
        return card.getWord();
    }
}
