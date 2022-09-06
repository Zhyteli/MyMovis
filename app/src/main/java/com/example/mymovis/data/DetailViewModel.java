package com.example.mymovis.data;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mymovis.adapters.ReviewAdapter;
import com.example.mymovis.adapters.TrailerAdapter;
import com.example.mymovis.data.api.ApiFactory;
import com.example.mymovis.data.api.ApiFactoryVideo;
import com.example.mymovis.data.api.ApiService;
import com.example.mymovis.data.pojo.Movie;
import com.example.mymovis.data.pojo.MovieResponse;
import com.example.mymovis.data.pojo.ReviewResponse;
import com.example.mymovis.data.pojo.Trailer;
import com.example.mymovis.data.pojo.TrailerResponse;
import com.example.mymovis.presentation.DetailActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel {
    private CompositeDisposable compositeDisposable;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTrailer(String id, String lang, TrailerAdapter trailerAdapter) {
        ApiFactoryVideo apiFactoryVideo = ApiFactoryVideo.getInstanceVideo();
        ApiService apiService = apiFactoryVideo.getApiServiceVideo();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getMovieTrailer(
                        id,
                        "32f91e104228c0c9ff3630899838e82e",
                        lang
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrailerResponse>() {
                    @Override
                    public void accept(TrailerResponse trailer) throws Exception {
                        trailerAdapter.setTrailers(trailer.getTrailers());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("trailer", throwable.toString());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void loadReview(String id, String lang, ReviewAdapter reviewAdapter) {
        ApiFactoryVideo apiFactoryVideo = ApiFactoryVideo.getInstanceVideo();
        ApiService apiService = apiFactoryVideo.getApiServiceVideo();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getMovieReview(
                        id,
                        "32f91e104228c0c9ff3630899838e82e",
                        lang
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReviewResponse>() {
                    @Override
                    public void accept(ReviewResponse reviewResponse) throws Exception {
                        reviewAdapter.setReviews(reviewResponse.getReviews());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("trailer", throwable.toString());
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.onCleared();
    }
}
