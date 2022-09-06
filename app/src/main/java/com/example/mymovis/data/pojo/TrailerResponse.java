package com.example.mymovis.data.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TrailerResponse {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("results")
    @Expose
    private ArrayList<Trailer> trailers = null;

    public int getIdTrailer() {
        return id;
    }

    public void setIdTrailer(int id) {
        this.id = id;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }
}
