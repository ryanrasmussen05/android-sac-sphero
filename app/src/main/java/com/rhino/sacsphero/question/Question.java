package com.rhino.sacsphero.question;

import java.util.ArrayList;

public class Question {
    private String text;
    private String correctAnswer;
    private ArrayList<String> answers;
    private int pointValue;

    public Question() {
        this.text = "";
        this.correctAnswer = "";
        this.answers = new ArrayList<>();
        this.pointValue = 0;
    }

    public void addAnswer(String answer) {
        this.answers.add(answer);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public int getPointValue() {
        return pointValue;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }
}
