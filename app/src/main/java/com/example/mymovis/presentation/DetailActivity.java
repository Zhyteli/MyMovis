package com.example.mymovis.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovis.R;
import com.example.mymovis.adapters.ReviewAdapter;
import com.example.mymovis.adapters.TrailerAdapter;
import com.example.mymovis.data.FavouriteMovie;
import com.example.mymovis.data.MainViewModel;
import com.example.mymovis.data.Movie;
import com.example.mymovis.data.Review;
import com.example.mymovis.data.Trailer;
import com.example.mymovis.utils.JSONUtils;
import com.example.mymovis.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster, imageViewAddToFavourite;
    private TextView textViewTitle,textViewOriginalTitle,textViewRating,textViewReleaseDate,textViewOverview;
    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private RecyclerView recyclerViewTrailers, recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("id")){
            id = intent.getIntExtra("id", -1);
            }else {
            finish();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).placeholder(android.R.drawable.progress_indeterminate_horizontal).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();

        imageViewAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favouriteMovie == null){
                    viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(DetailActivity.this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
                }else {
                    viewModel.deleteFavouriteMovie(favouriteMovie);
                    Toast.makeText(DetailActivity.this, R.string.remove_to_favourite, Toast.LENGTH_SHORT).show();
                }
                setFavourite();
            }
        });
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentTrailer);
            }
        });

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);

        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailerFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);

        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
    }
    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null){
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        }else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
    private void init(){
        lang = Locale.getDefault().getLanguage();
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);

        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
    }
}