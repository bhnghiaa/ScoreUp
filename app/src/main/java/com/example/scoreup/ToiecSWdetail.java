package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToiecSWdetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiec_swdetail);
        TextView about = findViewById(R.id.textView9);
        TextView feedback = findViewById(R.id.textView8);
        FrameLayout line1 = findViewById(R.id.line1);
        FrameLayout line2 = findViewById(R.id.line2);
        LinearLayout aView = findViewById(R.id.aView);
        LinearLayout fView = findViewById(R.id.fView);
        Button btnFulltestToeicSW = findViewById(R.id.btnFullTestToeicSW);
        btnFulltestToeicSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToiecSWdetail.this,MainActivityQuiz.class);
                startActivity(intent);
            }
        });
        about.setOnClickListener(v -> {
            about.setTextColor(Color.parseColor("#1d267d"));
            about.setTypeface(null, Typeface.BOLD);
            line1.setBackgroundColor(Color.parseColor("#1d267d"));
            aView.setVisibility(View.VISIBLE);
            feedback.setTypeface(null, Typeface.NORMAL);
            feedback.setTextColor(Color.parseColor("#9ba4b5"));
            line2.setBackgroundColor(Color.TRANSPARENT);
            fView.setVisibility(View.GONE);
        });
        feedback.setOnClickListener(v -> {
            feedback.setTextColor(Color.parseColor("#1d267d"));
            feedback.setTypeface(null, Typeface.BOLD);
            line2.setBackgroundColor(Color.parseColor("#1d267d"));
            fView.setVisibility(View.VISIBLE);
            about.setTypeface(null, Typeface.NORMAL);
            about.setTextColor(Color.parseColor("#9ba4b5"));
            line1.setBackgroundColor(Color.TRANSPARENT);
            aView.setVisibility(View.GONE);
        });
    }
}