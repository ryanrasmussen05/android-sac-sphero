package com.rhino.sacsphero;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.rhino.sacsphero.question.Question;
import com.rhino.sacsphero.question.QuestionManager;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                ArrayList<Question> questions = QuestionManager.GetAllQuestions(SplashActivity.this);

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.QUESTIONS_EXTRA, questions);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
