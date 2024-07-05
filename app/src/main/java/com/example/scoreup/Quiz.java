package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    private final long totalTime = 80 * 60 * 1000; // total time for the quiz
    private QuizDatabaseHelper dbHelper;
    private int c = 0;
    private int d = 0;
    private TextView nextButton;
    private TextView prevButton;
    private int currentQuestionIndex = 1;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private Cursor cursor;
    private TextView numQuestion;
    private Button btn_Finish;

    private ListView answersListView;
    private int totalQues = 1;
    private ArrayList<String> selectedParts;
    ArrayList<HashMap<Integer, String>> userResponses = new ArrayList<>();
    private ArrayList<String> userAnswers;
    private ArrayAdapter<String> adapter;
    private List<String> correctAnswers;


    public static List<String> removeEmptyStringsAndSort(ArrayList<String> list) {
        return list.stream()
                .filter(s -> !s.equals(""))
                .sorted()
                .collect(Collectors.toList());
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        GlobalData globalData = (GlobalData) getApplicationContext();
        String token = globalData.getToken();
        correctAnswers = new ArrayList<>();
        btn_Finish = findViewById(R.id.btn_Finish);
        btn_Finish.setOnClickListener(v -> {
            List<String> userAnswers = removeEmptyStringsAndSort(this.userAnswers);
            for (int i = 0; i < userAnswers.size(); i++) {
                String answer = userAnswers.get(i);
                if (answer.length() >= 4) {
                    userAnswers.set(i, String.valueOf(answer.charAt(3)));
                } else {
                    userAnswers.set(i, "");
                }
            }
            int score = 0;
            for (int i = 0; i < userAnswers.size(); i++) {
                if (userAnswers.get(i).equals(correctAnswers.get(i))) {
                    score++;
                }
            }

            // Start ResultActivity with score as an extra
            Intent intent = new Intent(Quiz.this, ResultActivity.class);

            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL_QUESTIONS", totalQues); // totalQues is the total number of questions
            startActivity(intent);
        });
        selectedParts = getIntent().getStringArrayListExtra("selectedParts");
        if (selectedParts == null || selectedParts.isEmpty()) {
            Toast.makeText(this, "No parts selected.", Toast.LENGTH_LONG).show();
            return;
        }

        initializeViews();
        setupTimer();
        setupNavigationDrawer();
        loadQuestionsForPart(selectedParts.get(0));
        dbHelper = new QuizDatabaseHelper(this);
        List<Question> questions = dbHelper.getAllQuestions();
        Collections.shuffle(questions);
        makeRequestPart5(token);
        makeRequestPart1(token);
        makeRequestPart2(token);
        makeRequestPart3(token);
        makeRequestPart4(token);
        makeRequestPart6(token);
        makeRequestPart7(token);

        initializeResponseStorage();
    }
    private void initializeResponseStorage() {
        userResponses = new ArrayList<>();
        for (int i = 1; i <= totalQues; i++) {
            userResponses.add(new HashMap<>());
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
        numQuestion = findViewById(R.id.numQuestion);
        userAnswers = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            userAnswers.add("");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userAnswers);


        prevButton.setOnClickListener(v -> moveToPrevQuestion());
        nextButton.setOnClickListener(v -> {
            moveToNextQuestion();
        });

        View.OnClickListener optionClickListener = v -> onOptionSelected(v, currentQuestionIndex);

        option1Button.setOnClickListener(optionClickListener);
        option2Button.setOnClickListener(optionClickListener);
        option3Button.setOnClickListener(optionClickListener);
        option4Button.setOnClickListener(optionClickListener);

        option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
        option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));

        loadQuestion(currentQuestionIndex);
    }
    @SuppressLint("SetTextI18n")
    private void loadQuestion(int questionIndex) {
        if (cursor != null && cursor.moveToPosition(currentQuestionIndex - 1)) {
            String questionText = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION));
            String option1 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION2));
            String option3 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION3));
            String option4 = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTION4));

            questionTextView.setText(Html.fromHtml(questionText, Html.FROM_HTML_MODE_LEGACY));
            option1Button.setText(option1);
            option2Button.setText(option2);
            option3Button.setText(option3);
            option4Button.setText(option4);
            option1Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option2Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option3Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            option4Button.setBackgroundColor(Color.parseColor("#D6EBFD"));
            numQuestion.setText("Question " + currentQuestionIndex + ":");
            numQuestion.setTextSize(18);
            numQuestion.setTypeface(null, Typeface.BOLD);
            numQuestion.setTextColor(Color.BLACK);
            SharedPreferences sharedPreferences = getSharedPreferences("OptionPrefs", Context.MODE_PRIVATE);
            String lastSelectedOptionState = sharedPreferences.getString("lastSelectedOptionState" + questionIndex, null);
            if (lastSelectedOptionState != null) {
                if (option1Button.getText().toString().equals(lastSelectedOptionState)) {
                    option1Button.setBackgroundColor(Color.parseColor("#6a5be2"));
                } else if (option2Button.getText().toString().equals(lastSelectedOptionState)) {
                    option2Button.setBackgroundColor(Color.parseColor("#6a5be2"));
                } else if (option3Button.getText().toString().equals(lastSelectedOptionState)) {
                    option3Button.setBackgroundColor(Color.parseColor("#6a5be2"));
                } else if (option4Button.getText().toString().equals(lastSelectedOptionState)) {
                    option4Button.setBackgroundColor(Color.parseColor("#6a5be2"));
                }
            }

        }
    }
    private void onOptionSelected(View view, int questionNumber) {
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
        Button selectedOption = (Button) view;

        String optionText = selectedOption.getText().toString();
        String responseText = questionNumber + " " + optionText;
        HashMap<Integer, String> response = new HashMap<>();
        response.put(questionNumber, optionText);
        userResponses.set(questionNumber, response);
        userAnswers.set(c, responseText);
        if(userAnswers.get(c).contains(String.valueOf(questionNumber)) ) {
            d++;
            if(d > 1) {
                for(int i = c - 1; i >= 0; i--) {
                    if(userAnswers.get(i).contains(String.valueOf(questionNumber))) {
                        userAnswers.set(i, "");
                        break;
                    }
                }
            }
        }

        c++;
        selectedOption.setBackgroundColor(Color.parseColor("#6a5be2"));
        adapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = getSharedPreferences("OptionPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastSelectedOptionState" + questionNumber, selectedOption.getText().toString());
        editor.apply();
    }


    private void setupTimer() {
        countDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeftFormatted;
                long hours = millisUntilFinished / 3600000;
                long minutes = (millisUntilFinished % 3600000) / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                if (hours > 0) {
                    timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                }
                txtTimer.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                txtTimer.setText("00:00");
                showToast("Time's up!");
            }
        };
        countDownTimer.start();
    }

    private void setupNavigationDrawer() {
        ImageButton hamburgerButton = findViewById(R.id.hamburger_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        hamburgerButton.setOnClickListener(v -> {
            if (isMenuVisible) {
                drawerLayout.closeDrawer(GravityCompat.START);
                isMenuVisible = false;
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                isMenuVisible = true;
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        SubMenu selectedPartsSubMenu = menu.findItem(R.id.menu_selected_parts).getSubMenu();

        int buttonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, getResources().getDisplayMetrics());

        HashMap<String, HashMap<Integer, Button>> partQuestionButtonMap = new HashMap<>();

        for (String part : selectedParts) {
            int numberOfQuestions = getNumberOfQuestionsForPart(part);

            totalQues += numberOfQuestions;
            MenuItem partItem = selectedPartsSubMenu.add(part).setOnMenuItemClickListener(item -> {
                loadQuestionsForPart(part);
                currentQuestionIndex = 1;
                displayQuestion();
                return true;
            });

            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setGravity(Gravity.START);
            mainLayout.setPadding(0, 0, 0, 16);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.START);
            linearLayout.setPadding(0, 0, 0, 16);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            LinearLayout currentRow = null;
            int buttonCount = 0;

            // Step 2: Create a new HashMap to store buttons by question
            HashMap<Integer, Button> questionButtonMap = new HashMap<>();

            for (int i = 0; i < numberOfQuestions; i++) {
                final int index = i + 1;
                if (buttonCount % 6 == 0) {
                    currentRow = new LinearLayout(this);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(layoutParams);
                    linearLayout.addView(currentRow);
                }

                Button button = new Button(this);
                button.setText(String.valueOf(index));
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(buttonSize, buttonSize);
                buttonLayoutParams.setMargins(10, 10, 18, 10);
                button.setLayoutParams(buttonLayoutParams);

                GradientDrawable defaultBackground = createDrawable("#9ba4b5");
                GradientDrawable selectedBackground = createDrawable("#6a5be2");
                button.setBackground(defaultBackground);
                button.setOnClickListener(v -> {
                    currentQuestionIndex = index;
                    displayQuestion();
                    if(button.getBackground() == defaultBackground) {
                        button.setBackground(selectedBackground);
                    } else {
                        button.setBackground(defaultBackground);
                    }
                });
                // Step 3: Add the button to the questionButtonMap
                questionButtonMap.put(index, button);

                currentRow.addView(button);
                buttonCount++;
            }

            // Step 4: Add the questionButtonMap to the partQuestionButtonMap
            partQuestionButtonMap.put(part, questionButtonMap);

            View actionView = partItem.getActionView();
            if (actionView != null) {
                mainLayout.addView(actionView);
            }
            mainLayout.addView(linearLayout);

            selectedPartsSubMenu.add("").setActionView(mainLayout);
        }
    }

    private GradientDrawable createDrawable(String color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(20);
        gd.setColor(Color.parseColor(color));
        return gd;

    }


    private void loadQuestionsForPart(String part) {
        cursor = dbHelper.getQuestionsForPart(part, getNumberOfQuestionsForPart(part) );
        if (cursor != null && cursor.moveToFirst()) {
            displayQuestion();
        } else {
            showToast("No questions found for " + part);
        }
    }

    private void displayQuestion() {
        loadQuestion(currentQuestionIndex);
    }

    private void moveToNextQuestion() {
        if (currentQuestionIndex < cursor.getCount()) {
            currentQuestionIndex++;
            d = 0;
            displayQuestion();
        }
    }

    private void moveToPrevQuestion() {
        if (currentQuestionIndex > 1) {
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

    private void makeRequestPart5(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");
                                correctAnswers.add(ansCorrect);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
    private void makeRequestPart1(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 1", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
    private void makeRequestPart2(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 2", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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

    private void makeRequestPart3(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 3", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
    private void makeRequestPart4(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 4", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
    private void makeRequestPart6(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 6", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
    private void makeRequestPart7(String token) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"notionDatabaseId\": \"ba1ea74a570842ab9d46c6fd62772b83\",\n    \"tag\": \"part_5\",\n    \"limit\": 200,\n    \"multiQuestions\": true\n}");
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/questions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token )
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
                                String ansCorrect = question.getJSONObject("properties").getJSONObject("correct").getJSONArray("rich_text").getJSONObject(0).getJSONObject("text").getString("content");

                                Question questionsP5 = new Question("Part 7", questionText, answerA, answerB, answerC, answerD, ansCorrect);
                                correctAnswers.add(ansCorrect);
                                dbHelper.addQuestion(questionsP5);
                            }
                        }

                        runOnUiThread(() -> questionTextView.setText(Html.fromHtml(questionsInfo.toString(), Html.FROM_HTML_MODE_LEGACY)));
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
        return 0; // default case
    }
}
