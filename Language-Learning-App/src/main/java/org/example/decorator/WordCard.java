package org.example.decorator;

import org.example.Word;

public interface WordCard {
    Word getWord();
    String render();
}
