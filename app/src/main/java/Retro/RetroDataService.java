package Retro;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetroDataService {

    //Movie

    @GET("movie/popular")
    Call<TopMovie> getPopular(@Query("api_key") String api_key, @Query("page") String page);

    @GET("movie/top_rated")
    Call<TopMovie> getTop(@Query("api_key") String api_key, @Query("page") String page);

    @GET("movie/upcoming")
    Call<TopMovie> getUpcoming(@Query("api_key") String api_key, @Query("page") String page);

    @GET("movie/{movie_id}")
    Call<MyMovie> getMovie(@Path("movie_id") String movie_id, @Query("api_key") String api_key);

    @GET("genre/movie/list")
    Call<GenreList> getGenres(@Query("api_key") String api_key);

    @GET("search/movie")
    Call<TopMovie> searchMovie(@Query("api_key") String api_key,@Query("query")String query);

    @GET("movie/{movie_id}/videos")
    Call<RetroYoutubeList> getMovieVideos(@Path("movie_id") String movie_id, @Query("api_key") String api_key);

    //TV

    @GET("tv/popular")
    Call<TopTv> getPopularTv(@Query("api_key") String api_key, @Query("page") String page);

    @GET("tv/top_rated")
    Call<TopTv> getTopTv(@Query("api_key") String api_key, @Query("page") String page);

    @GET("tv/{tv_id}")
    Call<MyTv> getTv(@Path("tv_id") String movie_id, @Query("api_key") String api_key);

    @GET("genre/tv/list")
    Call<GenreList> getGenresTv(@Query("api_key") String api_key);

    @GET("search/tv")
    Call<TopTv> searchTv(@Query("api_key") String api_key,@Query("query")String query);

    @GET("tv/{tv_id}/videos")
    Call<RetroYoutubeList> getTvVideos(@Path("tv_id") String movie_id, @Query("api_key") String api_key);

}
