package com.example.mymovis.data.converters;

import androidx.room.TypeConverter;

import com.example.mymovis.data.pojo.Movie;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    @TypeConverter
    public String listMoviesToString(List<Movie> specialities) {
        return new Gson().toJson(specialities);
    }
    @TypeConverter
    public List<Movie> stringToListMovie(String movieAsString) {
        Gson gson = new Gson();
        ArrayList objects = gson.fromJson(movieAsString, ArrayList.class);
        ArrayList<Movie> specialities = new ArrayList<>();
        for (Object o: objects) {
            specialities.add(gson.fromJson(o.toString(), Movie.class));
        }
        return specialities;
    }
}
