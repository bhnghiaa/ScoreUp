package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private Button nextButton;
    private Button prevButton;
    private int currentQuestionIndex = 1;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private Cursor cursor;
    private ArrayList<String> selectedParts;
    private String currentPartIndex;
    private TextView question_textview;
    private TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        question_textview = findViewById(R.id.question_textview);
        question = findViewById(R.id.question);
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
    private boolean isOptionSelected = false;
    private void onOptionSelected(Button selectedButton) {
        // Reset elevation for all buttons
        Button option1Button = findViewById(R.id.button_option1);
        Button option2Button = findViewById(R.id.button_option2);
        Button option3Button = findViewById(R.id.button_option3);
        Button option4Button = findViewById(R.id.button_option4);
        isOptionSelected = true;
        // Reset background color for all buttons
        option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));

        // Change elevation and background color of selected button
        selectedButton.setBackgroundColor(Color.parseColor("#2195F3"));
        showToast(String.valueOf(currentQuestionIndex));
        GradientDrawable gd2 = new GradientDrawable();
        gd2.setShape(GradientDrawable.RECTANGLE);
        gd2.setCornerRadius(20); // Độ cong cho góc: 20dp
        gd2.setColor(Color.parseColor("#FF4081"));
        // Show toast with selected option
//        showToast(selectedButton.getText().toString() + " selected");


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
    ArrayList<Map<String, Object>> partButtonList = new ArrayList<>();
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

    private void moveToQuestionPosition(int position) {
        if (cursor != null && dbHelper.moveToPosition(cursor, position)) {
            displayQuestion();
        } else {
            showToast("Invalid question position");
        }
    }


    private void loadQuestionsForPart(String part) {
        cursor = dbHelper.getQuestionsForPart(part);
        if (cursor != null && cursor.moveToFirst()) {
            currentQuestionIndex = 1;
            displayQuestion();
        } else {
            showToast("No questions found for " + part);
        }
        currentPartIndex = String.valueOf(selectedParts.indexOf(part) + 1);
    }

    private void displayQuestion() {
        if (cursor.moveToPosition(currentQuestionIndex)) {
            String questionText = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION));
            String option1 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION2));
            String option3 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION3));
            String option4 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION4));
                question.setText("Question " + String.valueOf(currentQuestionIndex) + ": ");

            question_textview.setText(Html.fromHtml(questionText.toString()), TextView.BufferType.SPANNABLE);
            option1Button.setText(option1);
            option2Button.setText(option2);
            option3Button.setText(option3);
            option4Button.setText(option4);
            option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));


//            GradientDrawable gd2 = new GradientDrawable();
//            gd2.setShape(GradientDrawable.RECTANGLE);
//            gd2.setCornerRadius(20); // Độ cong cho góc: 20dp
//            gd2.setColor(Color.parseColor("#FF4081"));

        }
//
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

    private int getNumberOfQuestionsForPart(String part) {
        // Implement logic to get number of questions for each part
        // For example:
        if ("Part 1".equals(part)) {
            return 15;
        } else if ("Part 2".equals(part)) {
            return 17;
        } else if ("Part 3".equals(part)) {
            return 16;
        } else if ("Part 4".equals(part)) {
            return 18;
        } else if ("Part 5".equals(part)) {
            return 30;
        } else if ("Part 6".equals(part)) {
            return 19;
        } else if ("Part 7".equals(part)) {
            return 15;
        }
        return 0;
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
                runOnUiThread(() -> question_textview.setText("Request failed: " + e.getMessage()));
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
                                Question questionss = new Question( "Part 5" ,null ,questionText.toString(), answerA.toString(), answerB.toString(), answerC.toString(), answerD.toString(), answerA.toString());
                                dbHelper.addQuestion(questionss);
                            }
                        }
                        runOnUiThread(() -> question_textview.setText(Html.fromHtml(questionsInfo.toString()), TextView.BufferType.SPANNABLE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> question_textview.setText("JSON parsing error: " + e.getMessage()));
                    }
                } else {
                    runOnUiThread(() -> question_textview.setText("Error: " + response.code() + " " + response.message()));
                }
            }
        });
    }
}