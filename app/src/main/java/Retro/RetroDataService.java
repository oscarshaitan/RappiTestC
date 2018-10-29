package Retro;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetroDataService {

    @GET("movie/popular")
    Call<TopMovie> getPopular(@Query("api_key") String api_key);

    @GET("movie/top_rated")
    Call<TopMovie> getTop(@Query("api_key") String api_key);

    @GET("movie/upcoming")
    Call<TopMovie> getUpcoming(@Query("api_key") String api_key);

    @GET("movie/{movie_id}")
    Call<MyMovie> getMovie(@Path("movie_id") String movie_id, @Query("api_key") String api_key);


    @GET("genre/movie/list")
    Call<GenreList> getGenres(@Query("api_key") String api_key);

    @GET("search/movie")
    Call<TopMovie> searchMovie(@Query("api_key") String api_key,@Query("query")String query);

    @GET("movie/{movie_id}/videos")
    Call<RetroYoutubeList> getMovieVideos(@Path("movie_id") String movie_id, @Query("api_key") String api_key);

}
