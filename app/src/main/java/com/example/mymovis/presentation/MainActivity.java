package com.example.mymovis.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwitchMaterial switchMaterial;
    private TextView textViewPopularity, textViewTopRated;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        switchMaterial = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        movieAdapter = new MovieAdapter();
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

    private void myOnClick(){
        switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
            setMethodOfSort(b);
        });
        switchMaterial.setChecked(false);
        
        textViewPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMethodOfSort(false);
                switchMaterial.setChecked(false);
            }
        });
        textViewTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMethodOfSort(true);
                switchMaterial.setChecked(true);
            }
        });
        
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void OnPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id",movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "Конец!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setMethodOfSort(boolean b){
        int methodOfSort;
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
        downloadData(methodOfSort, 1);
    }
    private void downloadData(int methodOfSort, int page) {
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            viewModel.deleteAllMovies();
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
        }
    }
}