package com.example.mymovis.data.pojo;

import static com.example.mymovis.presentation.DetailActivity.BASE_YOUTUBE_URL;
import static com.example.mymovis.presentation.DetailActivity.KEY_KEY_OF_VIDEO;
import static com.example.mymovis.presentation.DetailActivity.KEY_NAME;

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
        return KEY_NAME + name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
