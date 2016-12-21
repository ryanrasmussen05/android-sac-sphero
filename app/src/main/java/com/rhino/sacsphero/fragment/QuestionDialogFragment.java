package com.rhino.sacsphero.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rhino.sacsphero.MainActivity;
import com.rhino.sacsphero.R;
import com.rhino.sacsphero.question.Question;

public class QuestionDialogFragment extends DialogFragment implements View.OnClickListener {

    private Question question;

    public interface QuestionResultListener {
        void onQuestionResult(boolean correct, int pointValue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        question = getArguments().getParcelable(MainActivity.QUESTION_EXTRA);

        View view = inflater.inflate(R.layout.fragment_question, container);

        //TODO add randomization
        Button answerA = (Button) view.findViewById(R.id.answerA);
        Button answerB = (Button) view.findViewById(R.id.answerB);
        Button answerC = (Button) view.findViewById(R.id.answerC);
        Button answerD = (Button) view.findViewById(R.id.answerD);
        TextView questionText = (TextView) view.findViewById(R.id.question);
        TextView pointValueText = (TextView) view.findViewById(R.id.pointValue);

        questionText.setText(question.getText());
        pointValueText.setText(String.valueOf(question.getPointValue()));

        answerA.setOnClickListener(this);
        answerA.setText(question.getAnswers().get(0));
        answerB.setOnClickListener(this);
        answerB.setText(question.getAnswers().get(1));
        answerC.setOnClickListener(this);
        answerC.setText(question.getAnswers().get(2));
        answerD.setOnClickListener(this);
        answerD.setText(question.getAnswers().get(3));

        return view;
    }

    @Override
    public void onClick(View v) {
        QuestionResultListener activity = (QuestionResultListener) getActivity();

        String answer = ((Button) v).getText().toString();

        activity.onQuestionResult(answer.equals(question.getCorrectAnswer()), question.getPointValue());
        this.dismiss();
    }
}