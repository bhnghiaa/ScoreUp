package com.example.scoreup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class AccountFragment extends Fragment {
    private String username,email;
    private TextView txtUsername, txtEmail;


    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        Button btnInstruction = view.findViewById(R.id.btnInstruction);
        btnInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Instruction.class);
                startActivity(intent);
            }
        });
        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail);
        Bundle data = getArguments();
        if (data != null){
            username = data.getString("username");
            email = data.getString("email");
        }
        txtUsername.setText("Username: "+ username);
        txtEmail.setText("Email: "+ email);
        return view;
    }
}