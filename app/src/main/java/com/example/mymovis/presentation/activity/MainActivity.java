package com.example.mymovis.presentation.activity;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovis.R;
import com.example.mymovis.data.viewmodels.MainViewModel;
import com.example.mymovis.databinding.ActivityMainBinding;
import com.example.mymovis.domain.Movie;
import com.example.mymovis.presentation.adapters.MovieAdapter;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;

    private ActivityMainBinding binding;

    private int page;
    private static String methodOfSort;

    private static String lang;
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lang = Locale.getDefault().getLanguage();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        movieAdapter = new MovieAdapter();
        binding.recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        binding.recyclerViewPosters.setAdapter(movieAdapter);
        binding.switchSort.setChecked(true);
        myOnClick();

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this,
                movies -> movieAdapter.setMovies(movies)
        );
        viewModel.getErrors().observe(this,
                throwable -> {
            Toast.makeText(
                    MainActivity.this,
                    "Error" + throwable,
                    Toast.LENGTH_SHORT).show();
            viewModel.clearErrors();
        });

    }

    private void myOnClick() {
        binding.switchSort.setOnCheckedChangeListener((compoundButton, b) -> {
            setMethodOfSort(b);
        });
        binding.switchSort.setChecked(false);

        binding.textViewPopularity.setOnClickListener(view -> {
            setMethodOfSort(false);
            binding.switchSort.setChecked(false);
        });
        binding.textViewTopRated.setOnClickListener(view -> {
            setMethodOfSort(true);
            binding.switchSort.setChecked(true);
        });

        movieAdapter.setOnPosterClickListener(position -> {
            Movie movie = movieAdapter.getMovies().get(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id", movie.getId());
            Log.d("idIN", Integer.toString(movie.getId()));
            startActivity(intent);
        });
        binding.recyclerViewPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            binding.textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            binding.textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
        } else {
            methodOfSort = SORT_BY_POPULARITY;
            binding.textViewPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            binding.textViewTopRated.setTextColor(getResources().getColor(R.color.white));
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