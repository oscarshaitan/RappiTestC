package com.allegorit.testrappi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import Retro.MovieList;
import Retro.MyMovie;

class MovieAdapter extends BaseAdapter {

    private Context context;
    private List<MovieList> dataSet;
    private int  height, width;

    MovieAdapter(int height, int width,Context context, List<MovieList> dataSet){
        this.height = height;
        this.width = width;
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public MovieList getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dataSet.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View cellView = new View(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cellView = inflater.inflate(R.layout.cell,null);

        ImageView background = (ImageView)cellView.findViewById(R.id.background);
        background.setMinimumHeight(450);
        background.setMinimumWidth((width-20)/3);

        String url = "https://image.tmdb.org/t/p/w300"+getItem(i).getPosterPath();


        Picasso.get() //
                .load(url) //
                .placeholder(R.drawable.ic_launcher_background) //
                .resize((width-20)/3,450)
                .tag(context) //
                .into(background);

        TextView title =(TextView)cellView.findViewById(R.id.title);
        title.setText(getItem(i).getTitle());
        TextView rate =(TextView)cellView.findViewById(R.id.rate);
        NumberFormat formatter = new DecimalFormat("#0.00");
        rate.setText(formatter.format(getItem(i).getVoteAverage()));
        return cellView;
    }

    public List<MovieList> getDataset(){
        return dataSet;
    }

}
