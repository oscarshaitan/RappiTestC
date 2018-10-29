package com.allegorit.testrappi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import Retro.Genre;
import Retro.GenreList;
import Retro.MovieList;
import Retro.TvSeriesList;
import Retro.RetroDataService;
import Retro.RetrofitClientInstance;
import Retro.TopMovie;
import Retro.TopTv;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private GridView gridL;
    private RetroDataService service;
    private int  height, width, page;
    private String type;
    private boolean onSearch = true;
    private FloatingActionButton Fab;
    private List<MovieList> allCachedMovie;
    private List<TvSeriesList> allCachedTv;
    private boolean isMovie = true;
    private BottomNavigationView navigation;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    populateGrid("Popular");
                    return true;
                case R.id.navigation_dashboard:
                    populateGrid("Top");
                    return true;
                case R.id.navigation_notifications:
                    populateGrid("Upcoming");
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
        service = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(RetroDataService.class);

        Fab = (FloatingActionButton)findViewById(R.id.Fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onSearch){
                    populateGrid(type);
                    changeFab(!onSearch);
                }
                else searchMode();
            }
        });

        gridL = (GridView) findViewById(R.id.gridL);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        if(!isConnected()) Toast.makeText(this, "Mode Offline activated", Toast.LENGTH_SHORT).show();
        populateGrid("Popular");
    }

    private void populateGrid(String type){
        this.type = type;
        changeFab(false);
        Log.d("popuG",type);
        Log.d("popuG",isMovie+"");
        if(type.equals("Popular")){
            getPopular(false);
        }
        if(type.equals("Top")){
            getRated(false);
        }
        if(type.equals("Upcoming")){
            getUpcomming(false);
        }
    }


    private void fillGrid(List<MovieList> topMovie){
        //page = topMovie.getPage();
        gridL.setAdapter(new  MovieAdapter(height, width,getApplicationContext(),topMovie));
        gridL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("MID",""+gridL.getAdapter().getItemId(i));
                intent.putExtra("isMovie",isMovie);
                startActivity(intent);
            }
        });
    }
    private void fillGrid(List<TvSeriesList> topSeries, Boolean isMovie){
        //page = topMovie.getPage();
        gridL.setAdapter(new  TvAdapter(height, width,getApplicationContext(),topSeries));
        gridL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("MID",""+gridL.getAdapter().getItemId(i));
                startActivity(intent);
            }
        });
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void getCats(){
        if(isMovie){
            Call<GenreList> getGenres = (Call<GenreList>)service.getGenres(getResources().getString(R.string.api_key_tmdb));
            getGenres.enqueue(new Callback<GenreList>() {
                @Override
                public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                    findByCat(response.body().getGenres());
                }

                @Override
                public void onFailure(Call<GenreList> call, Throwable t) {}
            });
        }
        else {
            Call<GenreList> getGenres = (Call<GenreList>)service.getGenresTv(getResources().getString(R.string.api_key_tmdb));
            getGenres.enqueue(new Callback<GenreList>() {
                @Override
                public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                    findByCat(response.body().getGenres());
                }

                @Override
                public void onFailure(Call<GenreList> call, Throwable t) {}
            });
        }


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
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if(isMovie){
                            Call<TopMovie> searchList = (Call<TopMovie>)service.searchMovie(getResources().getString(R.string.api_key_tmdb),""+input);
                            searchList.enqueue(new Callback<TopMovie>() {
                                @Override
                                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                                    changeFab(!onSearch);
                                    fillGrid(response.body().getResults());
                                }

                                @Override
                                public void onFailure(Call<TopMovie> call, Throwable t) {}
                            });
                        }
                        else {
                            Call<TopTv> searchList = (Call<TopTv>)service.searchTv(getResources().getString(R.string.api_key_tmdb),""+input);
                            searchList.enqueue(new Callback<TopTv>() {
                                @Override
                                public void onResponse(Call<TopTv> call, Response<TopTv> response) {
                                    changeFab(!onSearch);
                                    fillGrid(response.body().getResults(),false);
                                }

                                @Override
                                public void onFailure(Call<TopTv> call, Throwable t) {}
                            });
                        }


                    }
                })
                .show();
    }

    public void changeFab(Boolean onSearch){
        this.onSearch = onSearch;
        if(onSearch)Fab.setImageDrawable(getResources().getDrawable(R.drawable.close));
        else Fab.setImageDrawable(getResources().getDrawable(R.drawable.search));
    }

    public void findByCat(final List<Genre> genreList){
        final List<String> genreStringList = new ArrayList<>();
        for (Genre genre : genreList) {
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
                        Toast.makeText(MainActivity.this, genreStringList.get(dialog.getSelectedIndex()), Toast.LENGTH_SHORT).show();
                        List<MovieList>myMoviesResult = new ArrayList<>();

                        List<MovieList>dataSet =getAllCacheMovieLiest();
                        for (MovieList movieList : dataSet) {
                            if(movieList.isGenre(genreList.get(dialog.getSelectedIndex()).getId())){
                                myMoviesResult.add(movieList);
                            }
                        }
                        changeFab(!onSearch);
                        fillGrid(myMoviesResult);
                        return false;
                    }
                })
                .show();
    }

    public List<MovieList> getAllCacheMovieLiest(){
        getPopular(true);
        return allCachedMovie;
    }

    public void getPopular(final boolean addToAll){
        Log.d("getPop",isMovie+"");
        if(isMovie){
            Call<TopMovie> popularList = (Call<TopMovie>)service.getPopular(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                    if(!addToAll)fillGrid(response.body().getResults());
                    else{
                        allCachedMovie.addAll(response.body().getResults());
                        getRated(true);
                    }
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {}
            });
        }
        else {
            Call<TopTv> popularList = (Call<TopTv>)service.getPopularTv(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopTv>() {
                @Override
                public void onResponse(Call<TopTv> call, Response<TopTv> response) {
                    Log.d("popularListR",response.isSuccessful()+"");
                    if(!addToAll)fillGrid(response.body().getResults(),false);
                    else{
                        allCachedTv.addAll(response.body().getResults());
                        getRated(true);
                    }
                }

                @Override
                public void onFailure(Call<TopTv> call, Throwable t) {
                    Log.d("popularListR",t+"");}
            });
        }

    }

    public void getRated(final boolean addToAll) {
        if (isMovie) {
            Call<TopMovie> popularList = (Call<TopMovie>) service.getTop(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {

                    if (!addToAll) fillGrid(response.body().getResults());
                    else {
                        allCachedMovie.addAll(response.body().getResults());
                        getUpcomming(true);
                    }
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {
                }
            });
        }
        else {
            Call<TopTv> popularList = (Call<TopTv>)service.getTopTv(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopTv>() {
                @Override
                public void onResponse(Call<TopTv> call, Response<TopTv> response) {

                    if(!addToAll)fillGrid(response.body().getResults(),false);
                    else {
                        allCachedTv.addAll(response.body().getResults());
                        getUpcomming(true);
                    }
                }

                @Override
                public void onFailure(Call<TopTv> call, Throwable t) {}
            });
        }

    }

    public void getUpcomming(final boolean addToAll){
        if(isMovie){
            Call<TopMovie> popularList = (Call<TopMovie>)service.getUpcoming(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {

                    if(!addToAll)fillGrid(response.body().getResults());
                    else allCachedMovie.addAll(response.body().getResults());
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {}
            });
        }
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
                isMovie = true;
                navigation.getMenu().findItem(R.id.navigation_notifications).setVisible(true);
                populateGrid(type);
                navigation.setSelectedItemId(0);
                return true;
            case R.id.tv:
                isMovie = false;
                navigation.getMenu().findItem(R.id.navigation_notifications).setVisible(false);
                populateGrid(type);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

