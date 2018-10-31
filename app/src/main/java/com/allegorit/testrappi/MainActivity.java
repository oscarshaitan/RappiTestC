package com.allegorit.testrappi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import Retro.Genre;
import Retro.GenreList;
import Retro.MovieList;
import Retro.RetroDataService;
import Retro.RetrofitClientInstance;
import Retro.TopMovie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView gridR;
    private RetroDataService service;
    private int  height, width;
    private String type;
    private boolean onSearch = false;
    private FloatingActionButton Fab;
    private List<MovieList> allCachedMovie = new ArrayList<>();
    private List<Genre> genres= new ArrayList<>();
    private BottomNavigationView navigation;
    private LinearLayout animLay;
    private int page = 1;
    private MovieAdapter movieAdapter;
    private GridLayoutManager gridLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            scrollListener.resetState();
            allCachedMovie.clear();
            movieAdapter.clear();
            page = 1;
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    populateGrid("Popular",1);
                    return true;
                case R.id.navigation_top_rated:
                    populateGrid("Top",1);
                    return true;
                case R.id.navigation_upcomming:
                    populateGrid("Upcoming",1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //retrofit services
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        service = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(RetroDataService.class);

        Fab = (FloatingActionButton)findViewById(R.id.Fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSearch){
                    changeFab(false);
                    populateGrid(type,1);
                }
                else searchMode();
            }
        });

        gridLayoutManager = new GridLayoutManager(this,3);
        onConfigurationChanged(getResources().getConfiguration());

        animLay = (LinearLayout)findViewById(R.id.animLay);
        gridR = (RecyclerView) findViewById(R.id.gridR);
        gridR.setLayoutManager(gridLayoutManager);
        movieAdapter = new MovieAdapter(height,width,this,allCachedMovie);
        gridR.setAdapter(movieAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d("count",page+"");
                loadNextDataFromApi(page);
            }
        };
        gridR.addOnScrollListener(scrollListener);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(!isConnected()) Toast.makeText(this, "Mode Offline activated", Toast.LENGTH_SHORT).show();
        populateGrid("Popular",1);
    }

    private void loadNextDataFromApi(int offset) {
        populateGrid(type,offset);
    }

    private void populateGrid(String type, int page){
        this.type = type;
        changeFab(false);
        if(type.equals("Popular")){
            getPopular(page);
        }
        if(type.equals("Top")){
            getRated(page);
        }
        if(type.equals("Upcoming")){
            getUpcomming(page);
        }
    }

    private void fillGrid(List<MovieList> topMovie){
        movieAdapter.addItems(topMovie);
        movieAdapter.notifyDataSetChanged();
    }

    private void fillGridSearch(List<MovieList> topMovie){
        movieAdapter.replaceItems(topMovie);
        movieAdapter.notifyDataSetChanged();
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void getCats(){
            Call<GenreList> getGenres = (Call<GenreList>)service.getGenres(getResources().getString(R.string.api_key_tmdb));
            getGenres.enqueue(new Callback<GenreList>() {
                @Override
                public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                    genres.addAll(response.body().getGenres());
                    findByCat();
                }

                @Override
                public void onFailure(Call<GenreList> call, Throwable t) {}
            });

    }

    public void searchMode(){
        final List<String> searchOpt = new ArrayList<>();
        searchOpt.add("Category");
        searchOpt.add("Keyword (Online only)");
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("Pick a Mode")
                .items(searchOpt)
                .itemsDisabledIndices(!isConnected()?1:-1)
                .positiveText("Ok")
                .choiceWidgetColor(getResources().getColorStateList(R.color.colorPrimary))
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                       if(dialog.getSelectedIndex()==0){
                           genres.clear();
                           getCats();
                       }
                       else if(dialog.getSelectedIndex()==1){
                           searchOnline();
                        }
                        return false;
                    }
                })
                .show();
    }

    public void searchOnline(){

        new MaterialDialog.Builder(this)
                .title("Search")
                .content("Enter a keyword")
                .positiveText("Search")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Action", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, final CharSequence input) {

                        Log.d("searchOnline",input+"");

                        Call<TopMovie> searchList = (Call<TopMovie>)service.searchMovie(getResources().getString(R.string.api_key_tmdb),""+input);
                        searchList.enqueue(new Callback<TopMovie>() {
                            @Override
                            public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                                Log.d("searchOnline",call.request().url().toString()+"");
                                Log.d("searchOnline",response.isSuccessful()+"");
                                Log.d("searchOnline",response.body().getTotalResults()+"");
                                changeFab(true);
                                fillGridSearch(response.body().getResults());
                            }

                            @Override
                            public void onFailure(Call<TopMovie> call, Throwable t) {Log.d("searchOnline",t+"");}
                        });

                    }
                })
                .show();
    }

    public void changeFab(Boolean onSearch){
        this.onSearch = onSearch;
        if(onSearch)Fab.setImageDrawable(getResources().getDrawable(R.drawable.close));
        else Fab.setImageDrawable(getResources().getDrawable(R.drawable.search));
    }

    public void findByCat(){
        final List<String> genreStringList = new ArrayList<>();
        for (Genre genre : genres) {
            genreStringList.add(genre.getName());
        }
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("Pick a Genr")
                .items(genreStringList)
                .positiveText("Search")
                .choiceWidgetColor(getResources().getColorStateList(R.color.colorPrimary))
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        List<MovieList>myMoviesResult = new ArrayList<>();

                        for (MovieList movieList : movieAdapter.getDataset()) {
                            if(movieList.isGenre(genres.get(dialog.getSelectedIndex()).getId())){
                                myMoviesResult.add(movieList);
                            }
                        }
                        changeFab(true);
                        fillGridSearch(myMoviesResult);
                        return false;
                    }
                })
                .show();
    }

    public void getPopular(final int newPage){
        Call<TopMovie> popularList = (Call<TopMovie>)service.getPopular(getResources().getString(R.string.api_key_tmdb),""+newPage);
        popularList.enqueue(new Callback<TopMovie>() {
            @Override
            public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                fillGrid(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<TopMovie> call, Throwable t) {}
        });
    }

    public void getRated(final int newPage) {

        Call<TopMovie> popularList = (Call<TopMovie>) service.getTop(getResources().getString(R.string.api_key_tmdb),""+newPage);
        popularList.enqueue(new Callback<TopMovie>() {
            @Override
            public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                fillGrid(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<TopMovie> call, Throwable t) {
                }
            });
    }

    public void getUpcomming(final int newPage){

        Call<TopMovie> popularList = (Call<TopMovie>)service.getUpcoming(getResources().getString(R.string.api_key_tmdb),""+newPage);
        popularList.enqueue(new Callback<TopMovie>() {
            @Override
            public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                fillGrid(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<TopMovie> call, Throwable t) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.movie:
                return true;
            case R.id.tv:
                Intent intent = new Intent(this,TvActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void animateGridFadeIn(){
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        Animation aniFadeo = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        animLay.startAnimation(aniFadeo);
        animLay.startAnimation(aniFade);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            gridLayoutManager.setSpanCount(5);
        }
        else {
            gridLayoutManager.setSpanCount(3);
        }
        /*
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;*/
    }

}

