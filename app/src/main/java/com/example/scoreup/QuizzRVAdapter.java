package com.example.scoreup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizzRVAdapter extends RecyclerView.Adapter<QuizzRVAdapter.QuizzViewHolder>{
    private ArrayList<String> title2;
    private ArrayList<String> numQuestion;
    private Context context2;
    private HomeFragment homeFragment2;

    public QuizzRVAdapter(ArrayList<String> title2, ArrayList<String> numQuestion, Context context2) {
        this.title2 = title2;
        this.numQuestion = numQuestion;
        this.context2 = context2;
    }

    @NonNull
    @Override
    public QuizzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizzcardview,parent,false);

        return new QuizzViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizzViewHolder holder, int position) {
        holder.txtTitle2.setText(title2.get(position));
        holder.numQuestion.setText(numQuestion.get(position));
        holder.constraintLayout.setOnClickListener(v -> {
            if (position == 0){
//                Intent intent = new Intent(context2,Quizz.class);
//                context2.startActivity(intent);

            } else if (position == 1){


            } else if (position == 2){


            }
        });
    }

    @Override
    public int getItemCount() {
        return title2.size();
    }

    public class QuizzViewHolder extends RecyclerView.ViewHolder{
        private TextView numQuestion, txtTitle2;
        private ConstraintLayout constraintLayout;


        public QuizzViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle2 = itemView.findViewById(R.id.txtTitle2);
            numQuestion = itemView.findViewById(R.id.numQuestion);
            constraintLayout = itemView.findViewById(R.id.course1);




//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (recyclerViewInterface2 != null){
//                        int pos = getAdapterPosition();
//
//                        if(pos != RecyclerView.NO_POSITION){
//                            recyclerViewInterface2.onItemClick(pos);
//                        }
//                    }
//                }
//            });
        }
    }
}

