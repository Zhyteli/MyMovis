package com.example.mymovis.presentation;

import static com.example.mymovis.data.MainViewModel.SORT_BY_POPULARITY;
import static com.example.mymovis.data.MainViewModel.SORT_BY_TOP_RATED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mymovis.R;
import com.example.mymovis.adapters.MovieAdapter;
import com.example.mymovis.data.MainViewModel;
import com.example.mymovis.data.pojo.Movie;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SwitchMaterial switchMaterial;
    private TextView textViewPopularity, textViewTopRated;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;
    private ProgressBar progressBar;


    private static final int LOADER_ID = 122;
    private LoaderManager loaderManager;

    private int page;
    private static String methodOfSort;
    private static boolean isLoaning = true;

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
        switch (id) {
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

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 2 : 2;
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
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        recyclerViewPosters.setAdapter(movieAdapter);
        switchMaterial.setChecked(true);
        myOnClick();

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });

    }

    private void myOnClick() {
        switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
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
            intent.putExtra("id", movie.getId());
            Log.d("idIN", Integer.toString(movie.getId()));
            startActivity(intent);
        });
        recyclerViewPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                movieAdapter.setOnReachEndListener(()->{
                    viewModel.nextPage(lang, methodOfSort, ++page);
                });
            }
        });

    }

    private void setMethodOfSort(boolean b) {
        if (b) {
            methodOfSort = SORT_BY_TOP_RATED;
            Log.d("setMethodOfSort", methodOfSort);
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
        } else {
            methodOfSort = SORT_BY_POPULARITY;
            Log.d("setMethodOfSort", methodOfSort);
            textViewPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        if (hasConnection(viewModel.getApplication())){
            viewModel.deleteAllMovies();
        }
        page = 0;
        viewModel.nextPage(lang, methodOfSort, ++page);
    }
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}