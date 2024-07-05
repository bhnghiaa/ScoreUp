package com.example.scoreup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> title2 = new ArrayList<>();
    private ArrayList<String> info = new ArrayList<>();
    private ArrayList<String> numQuestion = new ArrayList<>();
    private ArrayList<Integer> img = new ArrayList<>();
    ArrayList<Button> fulltest = new ArrayList<>();
    ArrayList<Button> details = new ArrayList<>();
    private CourseRVAdapter courseRVAdapter;
    private QuizzRVAdapter quizzRVAdapter;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    TextView txtSeeAll;
    TextView txtName;
    private Context context;
    private String username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.courseRV);
        recyclerView2 = view.findViewById(R.id.quizzRV);
        txtSeeAll = view.findViewById(R.id.txtSeeAll);



        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));


        title.add("TOEIC (Listening & Reading)");
        title.add("SAT");
        info.add("The Test of English for International Communication (TOEIC) is an international standardized test of English language proficiency for non-native speakers. It...");
        info.add("The SAT is an entrance exam used by most colleges and universities to make admissions decisions. The SAT is a multiple-choice, pencil-and-paper test created and administered by the College Board....");
        img.add(R.drawable.toeic);
        img.add(R.drawable.sat);

        title2.add("Từ Vựng chủ đề Enviroment");
        title2.add("Từ Vựng chủ đề Music");
        numQuestion.add("20");
        numQuestion.add("100");

        courseRVAdapter= new CourseRVAdapter(title, info, img, fulltest, details, getContext());
        recyclerView.setAdapter(courseRVAdapter);

        quizzRVAdapter = new QuizzRVAdapter(title2, numQuestion, getContext());
        recyclerView2.setAdapter(quizzRVAdapter);




    }


//        recyclerView = findViewById(R.id.courseRV);
//        recyclerView2 = findViewById(R.id.quizzRV);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
//        recyclerView2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
//
//
//        title.add("TOEIC (Listening &amp; Reading)");
//        title.add("SAT");
//        info.add("The Test of English for International Communication (TOEIC) is an international standardized test of English language proficiency for non-native speakers. It...");
//        info.add("The SAT is an entrance exam used by most colleges and universities to make admissions decisions. The SAT is a multiple-choice, pencil-and-paper test created and administered by the College Board....");
//        img.add(R.drawable.toeic);
//        img.add(R.drawable.sat);
//
//        title2.add("Từ Vựng chủ đề Enviroment");
//        title2.add("Từ Vựng chủ đề Music");
//        numQuestion.add("20");
//        numQuestion.add("100");
//
//        courseRVAdapter= new CourseRVAdapter(title, info, img, HomeFragment.this);
//        recyclerView.setAdapter(courseRVAdapter);
//
//        quizzRVAdapter = new QuizzRVAdapter(title2, numQuestion, HomeFragment.this);
//        recyclerView2.setAdapter(quizzRVAdapter);

}