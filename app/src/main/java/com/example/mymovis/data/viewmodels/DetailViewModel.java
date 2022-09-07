package com.example.mymovis.data.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mymovis.presentation.adapters.ReviewAdapter;
import com.example.mymovis.presentation.adapters.TrailerAdapter;
import com.example.mymovis.data.api.ApiFactoryVideo;
import com.example.mymovis.data.api.ApiService;
import com.example.mymovis.data.pojo.ReviewResponse;
import com.example.mymovis.data.pojo.TrailerResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends AndroidViewModel {

    private CompositeDisposable compositeDisposable;
    private final String key = "32f91e104228c0c9ff3630899838e82e";
    private MutableLiveData<Throwable> errors;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        errors = new MutableLiveData<>();
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.setValue(null);
    }

    public void loadTrailer(String id, String lang, TrailerAdapter trailerAdapter) {
        ApiFactoryVideo apiFactoryVideo = ApiFactoryVideo.getInstanceVideo();
        ApiService apiService = apiFactoryVideo.getApiServiceVideo();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getMovieTrailer(id, key, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trailer -> trailerAdapter.setTrailers(trailer.getTrailers()),
                        throwable -> errors.setValue(throwable)
                );
        compositeDisposable.add(disposable);
    }

    public void loadReview(String id, String lang, ReviewAdapter reviewAdapter) {
        ApiFactoryVideo apiFactoryVideo = ApiFactoryVideo.getInstanceVideo();
        ApiService apiService = apiFactoryVideo.getApiServiceVideo();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getMovieReview(id, key, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reviewResponse ->
                        reviewAdapter.setReviews(reviewResponse.getReviews()),
                        throwable -> errors.setValue(throwable));
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onCleared();
    }
}
