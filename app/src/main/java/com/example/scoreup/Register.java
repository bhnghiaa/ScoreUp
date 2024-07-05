package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;

public class Register extends AppCompatActivity {
    Button returnToLogin;
    Button buttonRegister;
    EditText editTextUsername, editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get references to the EditText fields
        editTextUsername = findViewById(R.id.txtUsername);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        returnToLogin = findViewById(R.id.btnLoginReg);
        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performRegistration() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String bodyContent = "{\r\n    \"email\": \"" + editTextEmail.getText().toString() + "\",\r\n    \"username\": \"" + editTextUsername.getText().toString() + "\",\r\n    \"fullName\": \"" + editTextFullName.getText().toString() + "\",\r\n    \"password\": \"" + editTextPassword.getText().toString() + "\",\r\n    \"passwordConfirm\": \"" + editTextConfirmPassword.getText().toString() + "\"\r\n}";
        RequestBody body = RequestBody.create(mediaType, bodyContent);
        Request request = new Request.Builder()
                .url("https://foliastudy.com/exam/api/v1/api/auth/register")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Navigate to LoginActivity
                        Intent intent = new Intent(Register.this, LoginActivity.class);
                        startActivity(intent);
                    });
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String message = jsonObject.getString("message");
                        runOnUiThread(() -> Toast.makeText(Register.this, "Registration failed: " + message, Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}