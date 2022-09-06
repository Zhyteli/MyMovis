package com.example.mymovis.data.api;

import com.example.mymovis.data.pojo.Trailer;
import com.example.mymovis.data.pojo.MovieResponse;
import com.example.mymovis.data.pojo.TrailerResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("movie")
    Observable<MovieResponse> getMovieResponse(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("sort_by") String sort,
            @Query("vote_count.gte") String minVoteCountValue,
            @Query("page") String page);

    @GET("movie/{id}/videos")
    Observable<TrailerResponse> getMovieTrailer(
            @Path("id") String id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

}
