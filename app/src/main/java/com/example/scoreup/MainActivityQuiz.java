package com.example.scoreup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivityQuiz extends AppCompatActivity {
    private ArrayList<String> selectedParts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnBack = findViewById(R.id.btn_back);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityQuiz.this, MainActivity.class);
                startActivity(intent);

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedParts.isEmpty()) {
                    // Thông báo cho người dùng chọn ít nhất một phần
                    return;
                }
                Collections.sort(selectedParts);
                // Tạo Intent để chuyển đến layout mới
                Intent intent = new Intent(MainActivityQuiz.this, Quiz.class);
                intent.putStringArrayListExtra("selectedParts", selectedParts);
                startActivity(intent);

            }
        });


        setupCheckBoxes();
    }

    private void setupCheckBoxes() {
        CheckBox checkboxPart1 = findViewById(R.id.checkbox_part1);
        CheckBox checkboxPart2 = findViewById(R.id.checkbox_part2);
        CheckBox checkboxPart3 = findViewById(R.id.checkbox_part3);
        CheckBox checkboxPart4 = findViewById(R.id.checkbox_part4);
        CheckBox checkboxPart5 = findViewById(R.id.checkbox_part5);
        CheckBox checkboxPart6 = findViewById(R.id.checkbox_part6);
        CheckBox checkboxPart7 = findViewById(R.id.checkbox_part7);

        checkboxPart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart1, "Part 1");
            }
        });
        checkboxPart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart2, "Part 2");
            }
        });
        checkboxPart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart3, "Part 3");
            }
        });
        checkboxPart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart4, "Part 4");
            }
        });
        checkboxPart5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart5, "Part 5");
            }
        });
        checkboxPart6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart6, "Part 6");
            }
        });
        checkboxPart7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckboxClick(checkboxPart7, "Part 7");
            }
        });
    }

    private void handleCheckboxClick(CheckBox checkBox, String part) {
        if (checkBox.isChecked()) {
            selectedParts.add(part);
        } else {
            selectedParts.remove(part);
        }
    }
}