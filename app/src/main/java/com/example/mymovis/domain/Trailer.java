package com.example.mymovis.domain;

import static com.example.mymovis.presentation.activity.DetailActivity.BASE_YOUTUBE_URL;
import static com.example.mymovis.presentation.activity.DetailActivity.KEY_NAME;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;


    public String getKey() {
        return BASE_YOUTUBE_URL + key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
