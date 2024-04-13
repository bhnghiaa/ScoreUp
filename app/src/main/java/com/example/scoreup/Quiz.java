package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Quiz extends AppCompatActivity {
    private TextView questionTextView;
    private boolean isMenuVisible = true;
    private DrawerLayout drawerLayout;
    private TextView txtTimer;
    private CountDownTimer countDownTimer;
    private final long totalTime = 80 * 60 * 1000;
    private QuizDatabaseHelper dbHelper;
    private TextView nextButton;
    private TextView prevButton;
    private int currentQuestionIndex = 1;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private Cursor cursor;
    private TextView numQuestion;
    //    private TextView totalQuestion;
    private ArrayList<String> selectedParts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Button btn_Finish = findViewById(R.id.btn_Finish);
        btn_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Quiz.this,ResultActivity.class);
                startActivity(intent);
            }
        });

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
        makeRequestPart5();

//        Question[] questions = {
//                new Question("Part 1", "What is the capital of France?", "London", "Berlin", "Madrid", "Paris", "Paris"),
//                new Question("Part 1", "Which river runs through Egypt?", "Nile", "Amazon", "Mississippi", "Thames", "Nile"),
//                new Question("Part 1", "What is the currency of Japan?", "Yuan", "Yen", "Dollar", "Won", "Yen"),
//                new Question("Part 1", "Which planet is known as the Red Planet?", "Mars", "Venus", "Saturn", "Jupiter", "Mars"),
//                new Question("Part 1", "Who wrote 'Romeo and Juliet'?", "Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen", "William Shakespeare"),
//                new Question("Part 2", "When was the Eiffel Tower built?", "1885", "1887", "1890", "1889", "1889"),
//                new Question("Part 2", "Which ocean is the largest?", "Indian Ocean", "Pacific Ocean", "Atlantic Ocean", "Arctic Ocean", "Pacific Ocean"),
//                new Question("Part 2", "Who is the author of 'Pride and Prejudice'?", "Emily Bronte", "Jane Austen", "Charlotte Bronte", "Agatha Christie", "Jane Austen"),
//                new Question("Part 2", "Which country is known as the Land of the Rising Sun?", "China", "Vietnam", "Japan", "Korea", "Japan"),
//                new Question("Part 2", "Who discovered penicillin?", "Alexander Fleming", "Isaac Newton", "Albert Einstein", "Galileo Galilei", "Alexander Fleming"),
//                new Question("Part 3", "What is the capital of Australia?", "Melbourne", "Sydney", "Canberra", "Brisbane", "Canberra"),
//                new Question("Part 3", "Which gas do plants absorb from the atmosphere?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen", "Carbon Dioxide"),
//                new Question("Part 3", "Who is known as the Father of Modern Physics?", "Albert Einstein", "Isaac Newton", "Galileo Galilei", "Niels Bohr", "Albert Einstein"),
//                new Question("Part 3", "Which is the largest mammal?", "Blue Whale", "Elephant", "Giraffe", "Hippopotamus", "Blue Whale"),
//                new Question("Part 3", "What is the chemical symbol for gold?", "Ag", "Au", "Hg", "Pb", "Au"),
//                new Question("Part 4", "Which is the longest river in the world?", "Amazon", "Nile", "Mississippi", "Yangtze", "Nile"),
//                new Question("Part 4", "Who painted the Mona Lisa?", "Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo", "Leonardo da Vinci"),
//                new Question("Part 4", "What is the currency of China?", "Won", "Yuan", "Yen", "Pound", "Yuan"),
//                new Question("Part 4", "Which planet is known as the Blue Planet?", "Earth", "Mars", "Jupiter", "Venus", "Earth"),
//                new Question("Part 4", "Who is the author of 'The Great Gatsby'?", "F. Scott Fitzgerald", "Ernest Hemingway", "George Orwell", "Charles Dickens", "F. Scott Fitzgerald"),
//                new Question("Part 5", "What is the chemical symbol for oxygen?", "O", "Au", "Ag", "Hg", "O"),
//                new Question("Part 5", "Who was the first President of the United States?", "Abraham Lincoln", "John F. Kennedy", "Thomas Jefferson", "George Washington", "George Washington"),
//                new Question("Part 5", "What is the capital of Brazil?", "Rio de Janeiro", "Sao Paulo", "Brasilia", "Salvador", "Brasilia"),
//                new Question("Part 5", "Which country is known as the Land of the Midnight Sun?", "Sweden", "Norway", "Finland", "Denmark", "Norway"),
//                new Question("Part 5", "Who wrote 'The Odyssey'?", "Homer", "Virgil", "Dante", "Shakespeare", "Homer"),
//                new Question("Part 6", "What is the capital of Canada?", "Toronto", "Vancouver", "Ottawa", "Montreal", "Ottawa"),
//                new Question("Part 6", "Which gas do humans breathe out?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Helium", "Carbon Dioxide"),
//                new Question("Part 6", "Who discovered electricity?", "Thomas Edison", "Nikola Tesla", "Benjamin Franklin", "Michael Faraday", "Benjamin Franklin"),
//                new Question("Part 6", "Which is the largest planet in our solar system?", "Mars", "Venus", "Jupiter", "Saturn", "Jupiter"),
//                new Question("Part 6", "Who wrote 'To Kill a Mockingbird'?", "John Steinbeck", "Ernest Hemingway", "Mark Twain", "Harper Lee", "Harper Lee"),
//                new Question("Part 7", "What is the capital of South Africa?", "Cape Town", "Johannesburg", "Durban", "Pretoria", "Pretoria"),
//                new Question("Part 7", "Who discovered the theory of relativity?", "Isaac Newton", "Galileo Galilei", "Albert Einstein", "Stephen Hawking", "Albert Einstein"),
//                new Question("Part 7", "What is the currency of Russia?", "Ruble", "Yen", "Pound", "Dollar", "Ruble"),
//                new Question("Part 7", "Which is the smallest continent?", "Europe", "Asia", "Australia", "Antarctica", "Australia"),
//                new Question("Part 7", "Who wrote '1984'?", "George Orwell", "Aldous Huxley", "Ray Bradbury", "Ernest Hemingway", "George Orwell")
//        };
//
//        for (Question question : questions) {
//            dbHelper.addQuestion(question);
//        }


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
        numQuestion = findViewById(R.id.numQuestion);
//        totalQuestion = findViewById(R.id.numTotalQuestion);
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
        SubMenu selectedPartsSubMenu = menu.findItem(R.id.menu_selected_parts).getSubMenu();

        class ButtonWrapper {
            Button button;
        }
        ButtonWrapper selectedButton = new ButtonWrapper();
        for (String part : selectedParts) {
            // Lấy số lượng câu hỏi cho mỗi part
            int numberOfQuestions = getNumberOfQuestionsForPart(part);

            // Thêm MenuItem cho mỗi part
            MenuItem partItem = selectedPartsSubMenu.add(part).setOnMenuItemClickListener(item -> {
                // Hiển thị toast thông báo part đang được chọn
//                Toast.makeText(getApplicationContext(), item.getTitle() + " is selected", Toast.LENGTH_SHORT).show();
                loadQuestionsForPart(item.getTitle().toString());
                return true;
            });

            // Khởi tạo mainLayout
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setGravity(Gravity.START);
            mainLayout.setPadding(0, 0, 0, 16);  // Padding dưới cho LinearLayout

            // Tạo LinearLayout và thêm các Button vào đó dựa trên số lượng câu hỏi
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.START);
            linearLayout.setPadding(0, 0, 0, 16);  // Padding dưới cho LinearLayout

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            LinearLayout currentRow = null;
            int buttonCount = 0;

            for (int i = 1; i <= numberOfQuestions; i++) {
                if (buttonCount % 6 == 0) {
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(layoutParams);
                    linearLayout.addView(currentRow);
                }

                Button button = new Button(this);
                button.setText(String.valueOf(i));
                button.setTextColor(ContextCompat.getColor(this, R.color.white)); // Sử dụng màu từ resources


                // Kích thước hình vuông cho Button (30dp)
                int buttonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, getResources().getDisplayMetrics());

                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(buttonSize, buttonSize);
                buttonLayoutParams.setMargins(10, 10, 18, 10);  // Margin cho Button
                button.setLayoutParams(buttonLayoutParams);

                // Tạo GradientDrawable với borderRadius và màu nền cho Button
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setCornerRadius(20); // Độ cong cho góc: 20dp
                gd.setColor(Color.parseColor("#9ba4b5")); // Màu nền của nút

                // Thiết lập GradientDrawable làm nền cho Button
                button.setBackground(gd);

                // Thiết lập sự kiện click cho Button
                button.setOnClickListener(v -> {
                    GradientDrawable gd1 = new GradientDrawable();
                    gd1.setShape(GradientDrawable.RECTANGLE);
                    gd1.setCornerRadius(20); // Độ cong cho góc: 20dp
                    gd1.setColor(Color.parseColor("#6a5be2"));


                    if (selectedButton.button != null) {
                        // Reset màu cho Button đã được chọn trước đó
                        selectedButton.button.setBackground(gd);

                    }
                    String buttonText = ((Button) v).getText().toString();
                    int questionNumber = Integer.parseInt(buttonText);

                    currentQuestionIndex = questionNumber;
                    displayQuestion();

                    // Đặt màu xanh cho Button hiện tại được chọn

                    button.setBackground(gd1);
                    selectedButton.button = button;

                });


                if (currentRow != null) {
                    currentRow.addView(button);
                }

                buttonCount++;
            }

            // Thêm partItem và linearLayout vào mainLayout
            View actionView = partItem.getActionView();
            if (actionView != null) {
                mainLayout.addView(actionView);
            }
            mainLayout.addView(linearLayout);

            // Thêm mainLayout vào selectedPartsSubMenu
            selectedPartsSubMenu.add("").setActionView(mainLayout);
        }
    }

    private void loadQuestionsForPart(String part) {

        cursor = dbHelper.getQuestionsForPart(part, getNumberOfQuestionsForPart(part) + 1);
        if (cursor != null && cursor.moveToFirst()) {
            currentQuestionIndex = 1;
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
//            Integer totQuestion = cursor.getCount();

            questionTextView.setText(Html.fromHtml(questionText.toString()), TextView.BufferType.SPANNABLE);
            option1Button.setText(option1);
            option2Button.setText(option2);
            option3Button.setText(option3);
            option4Button.setText(option4);
            option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));

            numQuestion.setText("Question " + String.valueOf(currentQuestionIndex) + ":");
            numQuestion.setTextSize(18);
            numQuestion.setTypeface(null, Typeface.BOLD);
            numQuestion.setTextColor(Color.BLACK);
//            totalQuestion.setText(String.valueOf(totQuestion));
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
    private void makeRequestPart5() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"9099f9c67497482782b998bc0f98a394\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");

        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer 6fjIfPTNUbJx89Prv7guohA262VS3k34")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> questionTextView.setText("Request failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        StringBuilder questionsInfo = new StringBuilder();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            JSONArray questionsArray = item.getJSONArray("questions");

                            for (int j = 0; j < questionsArray.length(); j++) {
                                JSONObject question = questionsArray.getJSONObject(j);
                                String questionText = question.getJSONObject("properties").getJSONObject("question").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                String answerA = question.getJSONObject("properties").getJSONObject("A").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                String answerB = question.getJSONObject("properties").getJSONObject("B").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                String answerC = question.getJSONObject("properties").getJSONObject("C").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                String answerD = question.getJSONObject("properties").getJSONObject("D").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                Question questionss = new Question( "Part 5"  ,questionText.toString(), answerA.toString(), answerB.toString(), answerC.toString(), answerD.toString(), answerA.toString());
                                dbHelper.addQuestion(questionss);
                            }
                        }
                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString()), TextView.BufferType.SPANNABLE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> questionTextView.setText("JSON parsing error: " + e.getMessage()));
                    }
                } else {
                    runOnUiThread(() -> questionTextView.setText("Error: " + response.code() + " " + response.message()));
                }
            }
        });
    }
    private int getNumberOfQuestionsForPart(String part) {
        // Implement logic to get number of questions for each part
        // For example:
        if ("Part 1".equals(part)) {
            return 6;
        } else if ("Part 2".equals(part)) {
            return 25;
        } else if ("Part 3".equals(part)) {
            return 39;
        } else if ("Part 4".equals(part)) {
            return 30;
        } else if ("Part 5".equals(part)) {
            return 30;
        } else if ("Part 6".equals(part)) {
            return 16;
        } else if ("Part 7".equals(part)) {
            return 54;
        }
        return 0;
    }
}