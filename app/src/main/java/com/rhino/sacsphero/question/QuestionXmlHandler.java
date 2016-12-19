package com.rhino.sacsphero.question;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class QuestionXmlHandler extends DefaultHandler {
    private ArrayList<Question> questions;
    private Question question;

    private boolean currentElement = false;
    private boolean correctAnswer = false;
    private String currentValue = "";

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = true;

        switch (qName) {
            case "questions":
                questions = new ArrayList<>();
                break;
            case "question":
                question = new Question();
                break;
            case "answer":
                correctAnswer = attributes.getValue("correct") != null;
                break;
        }
    }

    public void endElement(String uri, String localName, String qName) {
        currentElement = false;

        switch (qName) {
            case "text":
                question.setText(currentValue.trim());
                break;
            case "points":
                question.setPointValue(Integer.parseInt(currentValue.trim()));
                break;
            case "answer":
                question.addAnswer(currentValue.trim());
                if (correctAnswer) {
                    question.setCorrectAnswer(currentValue.trim());
                }
                break;
            case "question":
                questions.add(question);
                break;
        }

        currentValue = "";
    }

    public void characters(char[] ch, int start, int length) {
        if(currentElement) {
            currentValue += new String(ch, start, length);
        }
    }
}
