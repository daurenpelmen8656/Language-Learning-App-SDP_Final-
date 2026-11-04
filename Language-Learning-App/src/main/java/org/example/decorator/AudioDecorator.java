package org.example.decorator;

public class AudioDecorator extends WordCardDecorator {
    private final String audioUrl;

    public AudioDecorator(WordCard card, String audioUrl) {
        super(card);
        this.audioUrl = audioUrl;
    }

    @Override
    public String render() {
        return card.render() + " | Audio: " + audioUrl;
    }
}
