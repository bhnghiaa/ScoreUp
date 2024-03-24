package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Quiz extends AppCompatActivity {
    private TextView questionTextView;
    private boolean isMenuVisible = true;
    private DrawerLayout drawerLayout;
    private TextView txtTimer;
    private CountDownTimer countDownTimer;
    private final long totalTime = 80 * 60 * 1000;
    private QuizDatabaseHelper dbHelper;
    private Button nextButton;
    private Button prevButton;
    private int currentQuestionIndex = 0;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private Cursor cursor;
    private ArrayList<String> selectedParts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        selectedParts = getIntent().getStringArrayListExtra("selectedParts");
        if (selectedParts == null || selectedParts.isEmpty()) {
            // Thông báo cho người dùng không có phần nào được chọn
            return;
        }

        initializeViews();
        setupTimer();
        setupNavigationDrawer();
        loadQuestionsForPart(selectedParts.get(0));
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);

// Mảng các câu hỏi
        Question[] questions = {
                new Question("Part 1", "What is the capital of France?", "London", "Berlin", "Madrid", "Paris", "Paris"),
                new Question("Part 1", "Which river runs through Egypt?", "Nile", "Amazon", "Mississippi", "Thames", "Nile"),
                new Question("Part 1", "What is the currency of Japan?", "Yuan", "Yen", "Dollar", "Won", "Yen"),
                new Question("Part 1", "Which planet is known as the Red Planet?", "Mars", "Venus", "Saturn", "Jupiter", "Mars"),
                new Question("Part 1", "Who wrote 'Romeo and Juliet'?", "Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen", "William Shakespeare"),
                new Question("Part 2", "When was the Eiffel Tower built?", "1885", "1887", "1890", "1889", "1889"),
                new Question("Part 2", "Which ocean is the largest?", "Indian Ocean", "Pacific Ocean", "Atlantic Ocean", "Arctic Ocean", "Pacific Ocean"),
                new Question("Part 2", "Who is the author of 'Pride and Prejudice'?", "Emily Bronte", "Jane Austen", "Charlotte Bronte", "Agatha Christie", "Jane Austen"),
                new Question("Part 2", "Which country is known as the Land of the Rising Sun?", "China", "Vietnam", "Japan", "Korea", "Japan"),
                new Question("Part 2", "Who discovered penicillin?", "Alexander Fleming", "Isaac Newton", "Albert Einstein", "Galileo Galilei", "Alexander Fleming"),
                new Question("Part 3", "What is the capital of Australia?", "Melbourne", "Sydney", "Canberra", "Brisbane", "Canberra"),
                new Question("Part 3", "Which gas do plants absorb from the atmosphere?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen", "Carbon Dioxide"),
                new Question("Part 3", "Who is known as the Father of Modern Physics?", "Albert Einstein", "Isaac Newton", "Galileo Galilei", "Niels Bohr", "Albert Einstein"),
                new Question("Part 3", "Which is the largest mammal?", "Blue Whale", "Elephant", "Giraffe", "Hippopotamus", "Blue Whale"),
                new Question("Part 3", "What is the chemical symbol for gold?", "Ag", "Au", "Hg", "Pb", "Au"),
                new Question("Part 4", "Which is the longest river in the world?", "Amazon", "Nile", "Mississippi", "Yangtze", "Nile"),
                new Question("Part 4", "Who painted the Mona Lisa?", "Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo", "Leonardo da Vinci"),
                new Question("Part 4", "What is the currency of China?", "Won", "Yuan", "Yen", "Pound", "Yuan"),
                new Question("Part 4", "Which planet is known as the Blue Planet?", "Earth", "Mars", "Jupiter", "Venus", "Earth"),
                new Question("Part 4", "Who is the author of 'The Great Gatsby'?", "F. Scott Fitzgerald", "Ernest Hemingway", "George Orwell", "Charles Dickens", "F. Scott Fitzgerald"),
                new Question("Part 5", "What is the chemical symbol for oxygen?", "O", "Au", "Ag", "Hg", "O"),
                new Question("Part 5", "Who was the first President of the United States?", "Abraham Lincoln", "John F. Kennedy", "Thomas Jefferson", "George Washington", "George Washington"),
                new Question("Part 5", "What is the capital of Brazil?", "Rio de Janeiro", "Sao Paulo", "Brasilia", "Salvador", "Brasilia"),
                new Question("Part 5", "Which country is known as the Land of the Midnight Sun?", "Sweden", "Norway", "Finland", "Denmark", "Norway"),
                new Question("Part 5", "Who wrote 'The Odyssey'?", "Homer", "Virgil", "Dante", "Shakespeare", "Homer"),
                new Question("Part 6", "What is the capital of Canada?", "Toronto", "Vancouver", "Ottawa", "Montreal", "Ottawa"),
                new Question("Part 6", "Which gas do humans breathe out?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Helium", "Carbon Dioxide"),
                new Question("Part 6", "Who discovered electricity?", "Thomas Edison", "Nikola Tesla", "Benjamin Franklin", "Michael Faraday", "Benjamin Franklin"),
                new Question("Part 6", "Which is the largest planet in our solar system?", "Mars", "Venus", "Jupiter", "Saturn", "Jupiter"),
                new Question("Part 6", "Who wrote 'To Kill a Mockingbird'?", "John Steinbeck", "Ernest Hemingway", "Mark Twain", "Harper Lee", "Harper Lee"),
                new Question("Part 7", "What is the capital of South Africa?", "Cape Town", "Johannesburg", "Durban", "Pretoria", "Pretoria"),
                new Question("Part 7", "Who discovered the theory of relativity?", "Isaac Newton", "Galileo Galilei", "Albert Einstein", "Stephen Hawking", "Albert Einstein"),
                new Question("Part 7", "What is the currency of Russia?", "Ruble", "Yen", "Pound", "Dollar", "Ruble"),
                new Question("Part 7", "Which is the smallest continent?", "Europe", "Asia", "Australia", "Antarctica", "Australia"),
                new Question("Part 7", "Who wrote '1984'?", "George Orwell", "Aldous Huxley", "Ray Bradbury", "Ernest Hemingway", "George Orwell")
        };

        for (Question question : questions) {
            dbHelper.addQuestion(question);
        }
    }

    private void initializeViews() {
        txtTimer = findViewById(R.id.txtTimer);
        dbHelper = new QuizDatabaseHelper(this);
        questionTextView = findViewById(R.id.question_textview);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        option1Button = findViewById(R.id.button_option1);
        option2Button = findViewById(R.id.button_option2);
        option3Button = findViewById(R.id.button_option3);
        option4Button = findViewById(R.id.button_option4);

        option1Button.setTextColor(Color.BLACK);
        option2Button.setTextColor(Color.BLACK);
        option3Button.setTextColor(Color.BLACK);
        option4Button.setTextColor(Color.BLACK);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPrevQuestion();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextQuestion();
            }
        });
        option1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 1 selection
                onOptionSelected(option1Button);
            }
        });

        option2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 2 selection
                onOptionSelected(option2Button);
            }
        });

        option3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 3 selection
                onOptionSelected(option3Button);
            }
        });

        option4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle option 4 selection
                onOptionSelected(option4Button);
            }
        });
    }
    private void onOptionSelected(Button selectedButton) {
        // Reset elevation for all buttons
        Button option1Button = findViewById(R.id.button_option1);
        Button option2Button = findViewById(R.id.button_option2);
        Button option3Button = findViewById(R.id.button_option3);
        Button option4Button = findViewById(R.id.button_option4);


        // Reset background color for all buttons
        option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));

        // Change elevation and background color of selected button
        selectedButton.setBackgroundColor(Color.parseColor("#2195F3"));

        // Show toast with selected option
        showToast(selectedButton.getText().toString() + " selected");
    }
    private void setupTimer() {
        countDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeftFormatted;
                long hours = millisUntilFinished / 3600000;
                long minutes = (millisUntilFinished % 3600000) / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                if (hours >= 1) {
                    timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                }
                txtTimer.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                txtTimer.setText("00:00");
            }
        }.start();
    }

    private void setupNavigationDrawer() {
        ImageButton hamburgerButton = findViewById(R.id.hamburger_button);
        drawerLayout = findViewById(R.id.drawer_layout);

        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuVisible) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    isMenuVisible = false;
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    isMenuVisible = true;
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        SubMenu selectedPartsSubMenu = menu.addSubMenu("Selected Parts");
        for (String part : selectedParts) {
            selectedPartsSubMenu.add(part);
        }
    }

    private void loadQuestionsForPart(String part) {
        cursor = dbHelper.getQuestionsForPart(part);
        if (cursor != null && cursor.moveToFirst()) {
            currentQuestionIndex = 0;
            displayQuestion();
        } else {
            showToast("No questions found for " + part);
        }
    }

    private void displayQuestion() {
        if (cursor.moveToPosition(currentQuestionIndex)) {
            String questionText = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION));
            String option1 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION2));
            String option3 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION3));
            String option4 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION4));

            questionTextView.setText(questionText);
            option1Button.setText(option1);
            option2Button.setText(option2);
            option3Button.setText(option3);
            option4Button.setText(option4);
            option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        }
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void moveToPrevQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}