package com.example.scoreup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SatCert extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sat_cert);
        Button nextbtn = findViewById(R.id.button3);
        Button prevbtn = findViewById(R.id.button5);


        Button detailbtn = findViewById(R.id.button2);
        detailbtn.setOnClickListener(v -> {
            Intent intent = new Intent(SatCert.this, SatDetail.class);

            startActivity(intent);
        });

        final int[] crs = {0};
        crs[0] = 0;

//        nextbtn.setOnClickListener(v -> {
//            if (crs[0] < 1) {
//                crs[0] = crs[0] + 1;
//
//            }
//            setPage(crs[0]);
//
//
//        });
//        prevbtn.setOnClickListener(v -> {
//            if (crs[0] > 0) {
//                crs[0] = crs[0] - 1;
//
//            }
//            setPage(crs[0]);
//
//        });


    }

    private void setPage(int i) {
        FrameLayout card1 = findViewById(R.id.card1);
        FrameLayout card2 = findViewById(R.id.card2);
        TextView page = (TextView)findViewById(R.id.textView6);
        if (i == 0){
            card1.setVisibility(View.VISIBLE);
            card2.setVisibility(View.GONE);
            page.setText("1/1");
        }
//        if (i == 1){
//            card2.setVisibility(View.VISIBLE);
//            card1.setVisibility(View.GONE);
//            page.setText("2/2");
//        }
    }
}