package org.example.decorator;

public class ImageHintDecorator extends WordCardDecorator {
    private final String imageUrl;

    public ImageHintDecorator(WordCard card, String imageUrl) {
        super(card);
        this.imageUrl = imageUrl;
    }

    @Override
    public String render() {
        return card.render() + " | Image hint: " + imageUrl;
    }
}
