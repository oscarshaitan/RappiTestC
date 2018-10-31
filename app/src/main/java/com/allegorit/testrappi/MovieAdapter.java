package com.allegorit.testrappi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import Retro.MovieList;

public class MovieAdapter extends RecyclerView.Adapter <MovieAdapter.SimpleViewHolder> {
    private Activity activity;
    private List<MovieList> dataSet;
    private int  height, width;

    MovieAdapter(int height, int width, Activity activity, List<MovieList> dataSet){
        this.height = height;
        this.width = width;
        this.activity = activity;
        this.dataSet = dataSet;
    }


    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_cell, viewGroup, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.SimpleViewHolder viewHolder, final int i) {

        viewHolder.superLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MovieDetail.class);
                intent.putExtra("MID",""+dataSet.get(i).getId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

        viewHolder.background.setMinimumHeight((int)(height*0.2343));
        viewHolder.background.setMinimumWidth((width-20)/3);

        String url = "https://image.tmdb.org/t/p/w300"+getItem(i).getPosterPath();


        Picasso.get() //
                .load(url) //
                .placeholder(R.drawable.ic_launcher_background) //
                .resize((width-20)/3,(int)(height*0.2343))
                .tag(activity) //
                .into(viewHolder.background);

        viewHolder.title.setText(getItem(i).getTitle());
        NumberFormat formatter = new DecimalFormat("#0.0");
        viewHolder.rate.setText(formatter.format(getItem(i).getVoteAverage()));
    }



    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public MovieList getItem(int i){return dataSet.get(i);}

    public void addItem(MovieList movieList){
        dataSet.add(movieList);
    }

    public void addItems(List<MovieList> movieList){
        dataSet.addAll(movieList);
    }

    public long getItemId(int i) {
        return dataSet.get(i).getId();
    }

    public List<MovieList> getDataset(){
        return dataSet;
    }

    public void replaceItems(List<MovieList> topMovie) {
        dataSet.removeAll(dataSet);
        dataSet.addAll(topMovie);
    }
    public void clear(){
        dataSet.clear();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        ImageView background;
        TextView title;
        TextView rate;
        LinearLayout superLay;
        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            background = (ImageView)itemView.findViewById(R.id.background);
            title =(TextView)itemView.findViewById(R.id.title);
            rate =(TextView)itemView.findViewById(R.id.rate);
            superLay = (LinearLayout)itemView.findViewById(R.id.superLay);
        }
    }
}
