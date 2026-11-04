package org.example.decorator;

public class ExampleDecorator extends WordCardDecorator {
    private final String example;

    public ExampleDecorator(WordCard card, String example) {
        super(card);
        this.example = example;
    }

    @Override
    public String render() {
        return card.render() + " | Example: " + example;
    }
}
