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
import Retro.RetroDataService;
import Retro.RetrofitClientInstance;
import Retro.TopTv;
import Retro.TvSeriesList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvActivity extends AppCompatActivity {
    private RecyclerView gridR;
    private RetroDataService service;
    private int  height, width;
    private String type;
    private boolean onSearch = false;
    private FloatingActionButton Fab;
    private List<TvSeriesList> allCachedTv = new ArrayList<>();
    private List<Genre> genres= new ArrayList<>();
    private BottomNavigationView navigation;
    private LinearLayout animLay;
    private int page = 1;
    private GridLayoutManager gridLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TvAdapter tvAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            scrollListener.resetState();
            allCachedTv.clear();
            tvAdapter.clear();
            page = 1;
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    populateGrid("Popular",1);
                    return true;
                case R.id.navigation_top_rated:
                    populateGrid("Top",1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
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


        animLay = (LinearLayout)findViewById(R.id.animLay);
        //gridL = (GridView) findViewById(R.id.gridL);
        gridR = (RecyclerView) findViewById(R.id.gridR);
        gridR.setLayoutManager(gridLayoutManager);
        tvAdapter = new TvAdapter(height,width,this,allCachedTv);
        gridR.setAdapter(tvAdapter);

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
        navigation.getMenu().findItem(R.id.navigation_upcomming).setVisible(false);
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
    }

    private void fillGrid(List<TvSeriesList> topSeries){
        tvAdapter.addItems(topSeries);
        tvAdapter.notifyDataSetChanged();
    }

    private void fillGridSearch(List<TvSeriesList> topSeries){
        tvAdapter.replaceItems(topSeries);
        tvAdapter.notifyDataSetChanged();
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void getCats(){
        Call<GenreList> getGenres = (Call<GenreList>)service.getGenresTv(getResources().getString(R.string.api_key_tmdb));
        getGenres.enqueue(new Callback<GenreList>() {
            @Override
            public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                genres.addAll(response.body().getGenres());
                findByCatTv();
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
                    Call<TopTv> searchList = (Call<TopTv>)service.searchTv(getResources().getString(R.string.api_key_tmdb),""+input);
                    searchList.enqueue(new Callback<TopTv>() {
                        @Override
                        public void onResponse(Call<TopTv> call, Response<TopTv> response) {
                            changeFab(true);
                            fillGridSearch(response.body().getResults()); }@Override
                        public void onFailure(Call<TopTv> call, Throwable t) {}});
                    }
                })
                .show();
    }

    public void changeFab(Boolean onSearch){
        this.onSearch = onSearch;
        if(onSearch)Fab.setImageDrawable(getResources().getDrawable(R.drawable.close));
        else Fab.setImageDrawable(getResources().getDrawable(R.drawable.search));
    }

    public void findByCatTv(){
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
                        List<TvSeriesList> myTvSerieList = new ArrayList<>();
                        for (TvSeriesList tvSeriesList : tvAdapter.getDataset()) {
                            if(tvSeriesList.isGenre(genres.get(dialog.getSelectedIndex()).getId())){
                                myTvSerieList.add(tvSeriesList);
                            }
                        }
                        changeFab(true);
                        fillGrid(myTvSerieList);
                        return false;
                    }
                })
                .show();
    }

    public void getPopular(final int newPage){
        Call<TopTv> popularList = (Call<TopTv>)service.getPopularTv(getResources().getString(R.string.api_key_tmdb), ""+newPage);
        popularList.enqueue(new Callback<TopTv>() {
            @Override
            public void onResponse(Call<TopTv> call, Response<TopTv> response) {
                fillGrid(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<TopTv> call, Throwable t) {
                Log.d("popularListR",t+"");}
        });
    }

    public void getRated(final int newPage) {
        Call<TopTv> popularList = (Call<TopTv>)service.getTopTv(getResources().getString(R.string.api_key_tmdb),""+newPage);
        popularList.enqueue(new Callback<TopTv>() {
            @Override
            public void onResponse(Call<TopTv> call, Response<TopTv> response) {
                fillGrid(response.body().getResults());
                animateGridFadeIn();
            }

            @Override
            public void onFailure(Call<TopTv> call, Throwable t) {}
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
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                return true;
            case R.id.tv:
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

}
