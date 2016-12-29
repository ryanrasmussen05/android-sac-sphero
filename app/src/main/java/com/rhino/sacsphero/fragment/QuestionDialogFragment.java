package com.rhino.sacsphero.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rhino.sacsphero.MainActivity;
import com.rhino.sacsphero.R;
import com.rhino.sacsphero.question.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuestionDialogFragment extends DialogFragment implements View.OnClickListener {

    private Question question;

    private Button answerA;
    private Button answerB;
    private Button answerC;
    private Button answerD;
    private Button continueLabyrinthButton;
    private TextView correctNotification;
    private TextView incorrectNotification;

    public interface QuestionResultListener {
        void onQuestionResult(boolean correct, int pointValue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setDimAmount(0.8f);
        question = getArguments().getParcelable(MainActivity.QUESTION_EXTRA);

        //randomize order of answers
        long seed = System.nanoTime();
        Collections.shuffle(question.getAnswers(), new Random(seed));

        View view = inflater.inflate(R.layout.fragment_question, container);

        answerA = (Button) view.findViewById(R.id.answerA);
        answerB = (Button) view.findViewById(R.id.answerB);
        answerC = (Button) view.findViewById(R.id.answerC);
        answerD = (Button) view.findViewById(R.id.answerD);
        continueLabyrinthButton = (Button) view.findViewById(R.id.continueLabyrinthButton);
        correctNotification = (TextView) view.findViewById(R.id.correctDisplay);
        incorrectNotification = (TextView) view.findViewById(R.id.incorrectDisplay);
        TextView questionText = (TextView) view.findViewById(R.id.question);
        TextView pointValueText = (TextView) view.findViewById(R.id.pointValue);

        questionText.setText(question.getText());
        String pointsLabel = question.getPointValue() > 1 ? "Points" : "Point";
        String pointsText = String.valueOf(question.getPointValue()) + " " + pointsLabel;
        pointValueText.setText(pointsText);

        correctNotification.setVisibility(View.GONE);
        incorrectNotification.setVisibility(View.GONE);
        continueLabyrinthButton.setVisibility(View.GONE);

        answerA.setOnClickListener(this);
        answerA.setText(question.getAnswers().get(0));
        answerB.setOnClickListener(this);
        answerB.setText(question.getAnswers().get(1));
        answerC.setOnClickListener(this);
        answerC.setText(question.getAnswers().get(2));
        answerD.setOnClickListener(this);
        answerD.setText(question.getAnswers().get(3));

        continueLabyrinthButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.continueLabyrinthButton) {
            continueLabyrinth();
        } else {
            handleAnswer(view);
        }
    }

    public void handleAnswer(View view) {
        QuestionResultListener activity = (QuestionResultListener) getActivity();

        String answer = ((Button) view).getText().toString();
        boolean isCorrect = answer.equals(question.getCorrectAnswer());

        if(isCorrect) {
            correctNotification.setVisibility(View.VISIBLE);
            view.setBackgroundResource(R.drawable.button_answer_correct_custom);
            ((Button) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        } else {
            incorrectNotification.setVisibility(View.VISIBLE);
            view.setBackgroundResource(R.drawable.button_answer_incorrect_custom);
            ((Button) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            highlightCorrectAnswer();
        }

        answerA.setEnabled(false);
        answerB.setEnabled(false);
        answerC.setEnabled(false);
        answerD.setEnabled(false);
        continueLabyrinthButton.setVisibility(View.VISIBLE);

        activity.onQuestionResult(isCorrect, question.getPointValue());
    }

    public void continueLabyrinth() {
        this.dismiss();
    }

    public void highlightCorrectAnswer() {
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(answerA);
        buttons.add(answerB);
        buttons.add(answerC);
        buttons.add(answerD);

        for(Button button : buttons) {
            String answer = button.getText().toString();
            boolean isCorrect = answer.equals(question.getCorrectAnswer());

            if(isCorrect) {
                button.setBackgroundResource(R.drawable.button_answer_correct_custom);
                button.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            }
        }
    }
}