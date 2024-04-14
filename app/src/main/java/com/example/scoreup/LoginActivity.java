package com.example.scoreup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Perform login validation (not implemented in this example)
                Toast.makeText(LoginActivity.this, "Logging in with username: " + username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //yêu cầu HTTP POST đến một API endpoint để lấy thông tin người dùng từ cơ sở dữ liệu
    private void performLogin(String username, String password) {
        OkHttpClient client = new OkHttpClient();
        // Tạo request body từ dữ liệu người dùng
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        // Tạo request đến API endpoint
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/auth/login")
                .post(requestBody)
                .build();

        // Thực hiện request và xử lý kết quả
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // mỗi lần đăng nhập trả về token, lưu trữ token để sử dụng cho các yêu cầu sau
                     SharedPreferences sharedPreferences = getSharedPreferences("com.example.scoreup", Context.MODE_PRIVATE);
                     SharedPreferences.Editor editor = sharedPreferences.edit();
                     editor.putString("token", responseData);
                     editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
    public static void main(String[] args) {
        LoginActivity loginActivity = new LoginActivity();
        loginActivity.performLogin("username", "password");
    }

}
