package com.example.mymovis.data.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactoryVideo {
    private static ApiFactoryVideo apiFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/";

    private ApiFactoryVideo() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL_VIDEOS)
                .build();

    }

    public static ApiFactoryVideo getInstanceVideo() {
        if (apiFactory == null) {
            apiFactory = new ApiFactoryVideo();
        }
        return apiFactory;
    }

    public ApiService getApiServiceVideo() {
        return retrofit.create(ApiService.class);
    }
}
