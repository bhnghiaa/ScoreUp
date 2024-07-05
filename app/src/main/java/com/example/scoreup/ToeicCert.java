package com.example.scoreup;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ToeicCert extends AppCompatActivity {
    Button button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toeic_cert);
        Button nextbtn = findViewById(R.id.button3);
        Button prevbtn = findViewById(R.id.button5);

        Button detailbtn = findViewById(R.id.button);
        detailbtn.setOnClickListener(v -> {
            Intent intent = new Intent(ToeicCert.this, ToeicLRdetail.class);

            startActivity(intent);
        });
        Button detailbtn2 = findViewById(R.id.button0);
        detailbtn2.setOnClickListener(v -> {
            Intent intent = new Intent(ToeicCert.this, ToiecSWdetail.class);

            startActivity(intent);
        });


        final int[] crs = {0};
        crs[0] = 0;

        nextbtn.setOnClickListener(v -> {
            if (crs[0] < 1) {
                crs[0] = crs[0] + 1;

            }
            setPage(crs[0]);
//            if (crs[0] == 0){
//                card1.setVisibility(View.VISIBLE);
//                card2.setVisibility(View.GONE);
//                page.setText("1/2");
//            }
//            if (crs[0] == 1){
//                card2.setVisibility(View.VISIBLE);
//                card1.setVisibility(View.GONE);
//                page.setText("2/2");
//            }

        });
        prevbtn.setOnClickListener(v -> {
            if (crs[0] > 0) {
                crs[0] = crs[0] - 1;

            }
            setPage(crs[0]);
//            if (crs[0] == 0){
//                card1.setVisibility(View.VISIBLE);
//                card2.setVisibility(View.GONE);
//                page.setText("1/2");
//            }
//            if (crs[0] == 1){
//                card2.setVisibility(View.VISIBLE);
//                card1.setVisibility(View.GONE);
//                page.setText("2/2");
//            }

        });


    }

    private void setPage(int i) {
        FrameLayout card1 = findViewById(R.id.card1);
        FrameLayout card2 = findViewById(R.id.card2);
        TextView page = (TextView)findViewById(R.id.textView6);
        if (i == 0){
            card1.setVisibility(View.VISIBLE);
            card2.setVisibility(View.GONE);
            page.setText("1/2");
        }
        if (i == 1){
            card2.setVisibility(View.VISIBLE);
            card1.setVisibility(View.GONE);
            page.setText("2/2");
        }
    }
}