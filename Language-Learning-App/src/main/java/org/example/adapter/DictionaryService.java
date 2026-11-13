package org.example.adapter;

import org.example.Word;

import java.util.List;

public interface DictionaryService {
    Word findWord(String key);
    List<Word> findAllWords();
}
