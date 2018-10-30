package com.allegorit.testrappi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Retro.Genre;
import Retro.MyTv;
import Retro.RetroDataService;
import Retro.RetroYoutube;
import Retro.RetroYoutubeList;
import Retro.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvDetail extends AppCompatActivity {

    private int height;
    private int width;
    private String MID;
    private RetroDataService service;
    private boolean onVideoFS = false;
    private YoutubeVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        Bundle bundle = getIntent().getExtras();
        MID = bundle.getString("MID");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        height = height/3;

        service = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(RetroDataService.class);

        Call<MyTv> getDetail = (Call<MyTv>)service.getTv(MID,getResources().getString(R.string.api_key_tmdb));
        getDetail.enqueue(new Callback<MyTv>() {
            @Override
            public void onResponse(Call<MyTv> call, Response<MyTv> response) {
                fillInfo(response.body());
            }

            @Override
            public void onFailure(Call<MyTv> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "NO CACHE FOUND", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void fillInfo(MyTv tv){

        ImageView banner = (ImageView)findViewById(R.id.banner);
        Picasso.get() //
                .load("https://image.tmdb.org/t/p/w500"+tv.getBackdropPath())
                //.error()//
                .placeholder(R.drawable.ic_launcher_background) //
                .resize(width,height)
                .tag(getApplicationContext()) //
                .into(banner);

        TextView title = (TextView)findViewById(R.id.title);
        title.setText(tv.getName());

        String release = tv.getFirstAirDate();
        try {
            Date datedb=new SimpleDateFormat("yyyy-MM-dd").parse(release);

            TextView year = (TextView)findViewById(R.id.year);
            year.setText(""+(datedb.getYear()+1900));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView duration = (TextView)findViewById(R.id.duration);
        duration.setText(tv.getNumberOfSeasons().toString()+" Seasons");

        String genre= "";
        //movie.resetGenreList();
        List<Genre> genreList = tv.getGenres();
        for (int i = 0; i<genreList.size(); i++){
            genre+=genreList.get(i).getName();
            if((i+1)==genreList.size())genre+= "";
            else genre+="/";
        }

        TextView genreT = (TextView)findViewById(R.id.gener);
        genreT.setText(genre);

        TextView rate = (TextView)findViewById(R.id.rate);
        NumberFormat formatter = new DecimalFormat("#0.0");
        rate.setText(formatter.format(tv.getVoteAverage())+"/10.0");

        TextView descrip = (TextView)findViewById(R.id.description);
        descrip.setText(tv.getOverview());

        Call<RetroYoutubeList> popularList = (Call<RetroYoutubeList>)service.getTvVideos(MID,getResources().getString(R.string.api_key_tmdb));

        popularList.enqueue(new Callback<RetroYoutubeList>() {
            @Override
            public void onResponse(Call<RetroYoutubeList> call, Response<RetroYoutubeList> response) {
                populateVideos(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<RetroYoutubeList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "NO CACHE FOUND", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void populateVideos(final List<RetroYoutube> youtubeList){

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.videos);
        recyclerView.setHasFixedSize(true);
        //to use RecycleView, you need a layout manager. default is LinearLayoutManager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<String> videoKeys = new ArrayList<>();

        for (RetroYoutube retroYoutube : youtubeList) {
            videoKeys.add(retroYoutube.getKey());
        }

        adapter=new YoutubeVideoAdapter(this,videoKeys);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        if(!onVideoFS){
            super.onBackPressed();
            overridePendingTransition(R.anim.goup, R.anim.godown);
        }
    }

    public void animateGridFadeIn(){
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        LinearLayout superLay = (LinearLayout)findViewById(R.id.superLay);
        superLay.startAnimation(aniFade);
        superLay.setAlpha(1.0F);
    }
}
