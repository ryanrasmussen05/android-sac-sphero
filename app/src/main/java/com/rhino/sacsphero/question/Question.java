package com.rhino.sacsphero.question;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable {
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

    protected Question(Parcel in) {
        text = in.readString();
        correctAnswer = in.readString();
        if (in.readByte() == 0x01) {
            answers = new ArrayList<String>();
            in.readList(answers, String.class.getClassLoader());
        } else {
            answers = null;
        }
        pointValue = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(correctAnswer);
        if (answers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(answers);
        }
        dest.writeInt(pointValue);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}