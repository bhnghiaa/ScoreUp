package com.example.scoreup;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseRVAdapter extends RecyclerView.Adapter<CourseRVAdapter.CourseViewHolder>{
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> info = new ArrayList<>();
    ArrayList<Integer> img = new ArrayList<>();
    ArrayList<Button> fulltest = new ArrayList<>();
    ArrayList<Button> details = new ArrayList<>();
    private HomeFragment homeFragment;
    private Context context;
    //    private final RecyclerViewInterface recyclerViewInterface;
    public CourseRVAdapter(ArrayList<String> title, ArrayList<String> info, ArrayList<Integer> img,ArrayList<Button> fulltest,ArrayList<Button> details, Context context) {
        this.title = title;
        this.info = info;
        this.img = img;
        this.fulltest = fulltest;
        this.details = details;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursecardview,parent,false);

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.txtTitle.setText(title.get(position));
        holder.txtInfo.setText(info.get(position));
        holder.imgCourse.setImageResource(img.get(position));
        holder.fulltest.setOnClickListener(v -> {
            if (position == 0){
                Intent intent = new Intent(context,MainActivityQuiz.class);
                context.startActivity(intent);

            } else if (position == 1){

            }
        });
        holder.details.setOnClickListener(v -> {
            if (position == 0){
                Intent intent = new Intent(context, ToeicCert.class);
                context.startActivity(intent);

            } else if (position == 1){
                Intent intent = new Intent(context, SatCert.class);
                context.startActivity(intent);

            }

        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        private TextView txtInfo, txtTitle;
        private ImageView imgCourse;
        private Button fulltest,details;
        private ConstraintLayout constraintLayout;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            fulltest = itemView.findViewById(R.id.btnFullTest);
            details = itemView.findViewById(R.id.btnDetail);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtInfo = itemView.findViewById(R.id.txtInfo);
            imgCourse = itemView.findViewById(R.id.imgCourse);
            constraintLayout = itemView.findViewById(R.id.course1);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (recyclerViewInterface != null){
//                        int pos = getAdapterPosition();
//
//                        if(pos != RecyclerView.NO_POSITION){
//                            recyclerViewInterface.onItemClick(pos);
//                        }
//                    }
//                }
//            });
        }
    }
}

