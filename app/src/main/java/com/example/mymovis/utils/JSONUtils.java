package com.example.mymovis.utils;

import com.example.mymovis.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATA = "release_date";

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject){
        ArrayList<Movie> results = new ArrayList<>();
        if (jsonObject == null){
            return results;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCourt = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseData = objectMovie.getString(KEY_RELEASE_DATA);
                Movie movie = new Movie(id,voteCourt,title,originalTitle,overview,
                        posterPath,bigPosterPath,backdropPath,voteAverage,releaseData);
                results.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

}
