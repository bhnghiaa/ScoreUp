package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
public class ResultActivity extends AppCompatActivity {
    Button btn_TryAgain, btn_HomePage;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        btn_TryAgain = findViewById(R.id.btn_TryAgain);
        btn_HomePage = findViewById(R.id.btn_HomePage);
        int score = getIntent().getIntExtra("SCORE", 0);

        // Get the total number of questions
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        // Calculate the percentage of correct answers
        int percentage = (int) ((double) score / totalQuestions * 100);

        // Find the ProgressBar and set its progress
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(percentage);

        TextView numScore = findViewById(R.id.numScore);
        numScore.setText(String.valueOf(score));
        btn_HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btn_TryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivityQuiz.class);
                startActivity(intent);
            }
        });
    }
}