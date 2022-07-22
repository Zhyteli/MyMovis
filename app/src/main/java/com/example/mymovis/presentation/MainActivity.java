package com.example.mymovis.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovis.R;
import com.example.mymovis.adapters.MovieAdapter;
import com.example.mymovis.data.MainViewModel;
import com.example.mymovis.data.Movie;
import com.example.mymovis.utils.JSONUtils;
import com.example.mymovis.utils.NetworkUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private SwitchMaterial switchMaterial;
    private TextView textViewPopularity, textViewTopRated;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;
    private ProgressBar progressBar;

    private static final int LOADER_ID = 122;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoaning = false;

    private static String lang;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent i = new Intent(this, FavouriteActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width/2 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        switchMaterial = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBar = findViewById(R.id.progressBarLoading);

        loaderManager = LoaderManager.getInstance(this);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,getColumnCount()));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);
        switchMaterial.setChecked(true);
        myOnClick();
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if(page == 1){
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    private void myOnClick(){
        switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
            page = 1;
            setMethodOfSort(b);
        });
        switchMaterial.setChecked(false);
        
        textViewPopularity.setOnClickListener(view -> {
            setMethodOfSort(false);
            switchMaterial.setChecked(false);
        });
        textViewTopRated.setOnClickListener(view -> {
            setMethodOfSort(true);
            switchMaterial.setChecked(true);
        });
        
        movieAdapter.setOnPosterClickListener(position -> {
            Movie movie = movieAdapter.getMovies().get(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id",movie.getId());
            startActivity(intent);
        });
        movieAdapter.setOnReachEndListener(() -> {
            if (!isLoaning){
                progressBar.setVisibility(View.VISIBLE);
                downloadData(methodOfSort,page);
            }
        });
    }
    private void setMethodOfSort(boolean b){
        if(b) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
        }
        else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        downloadData(methodOfSort, page);
    }
    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle,this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this,args);
        jsonLoader.setOnStartLoadingListener(() -> isLoaning = true);
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            if(page == 1){
                viewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);
            page++;
        }
        isLoaning = false;
        progressBar.setVisibility(View.GONE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}