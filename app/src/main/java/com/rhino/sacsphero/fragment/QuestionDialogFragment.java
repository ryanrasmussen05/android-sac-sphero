package com.rhino.sacsphero.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.sacsphero.R;

public class QuestionDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface QuestionResultListener {
        void onQuestionResult(boolean correct, int pointValue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container);

        view.findViewById(R.id.answerA).setOnClickListener(this);
        view.findViewById(R.id.answerB).setOnClickListener(this);
        view.findViewById(R.id.answerC).setOnClickListener(this);
        view.findViewById(R.id.answerD).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        QuestionResultListener activity = (QuestionResultListener) getActivity();
        activity.onQuestionResult(true, 5);
        this.dismiss();
    }
}