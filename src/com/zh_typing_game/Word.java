package com.zh_typing_game;

public class Word
{
    String word;
    String[] pinyin;
    String[] answer;
    String meaning;

    Word(String word, String[] pinyin, String[] answer, String meaning){
        this.word = word;
        this.pinyin = pinyin;
        this.answer = answer;
        this.meaning = meaning;
    }
}
