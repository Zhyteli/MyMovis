package com.example.mymovis.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovis.R;
import com.example.mymovis.data.viewmodels.DetailViewModel;
import com.example.mymovis.data.viewmodels.MainViewModel;
import com.example.mymovis.domain.FavouriteMovie;
import com.example.mymovis.domain.Movie;
import com.example.mymovis.domain.Review;
import com.example.mymovis.domain.Trailer;
import com.example.mymovis.presentation.adapters.ReviewAdapter;
import com.example.mymovis.presentation.adapters.TrailerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster, imageViewAddToFavourite;
    private TextView textViewTitle, textViewOriginalTitle, textViewRating, textViewReleaseDate, textViewOverview;

    private MainViewModel viewModel;
    private DetailViewModel viewModelDet;

    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private RecyclerView recyclerViewTrailers, recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private static String lang;
    private static String langReview = "en-US";
    private int id;

    public static final String KEY_NAME = "name";
    public static String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String BIG_POSTER_SIZE = "w780";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        movie = viewModel.getMovieById(id);
        Picasso.get().load(BASE_POSTER_URL + BIG_POSTER_SIZE + movie.getPosterPath())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();

        imageViewAddToFavourite.setOnClickListener(view -> {
            if (favouriteMovie == null) {
                viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                Toast.makeText(DetailActivity.this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
            } else {
                viewModel.deleteFavouriteMovie(favouriteMovie);
                Toast.makeText(DetailActivity.this, R.string.remove_to_favourite, Toast.LENGTH_SHORT).show();
            }
            setFavourite();
        });
        
        trailerAdapter.setOnTrailerClickListener(url -> {
            Intent intentTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentTrailer);
        });

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);

        viewModelDet.loadTrailer(Integer.toString(movie.getId()), lang, trailerAdapter);
        viewModelDet.loadReview(Integer.toString(movie.getId()), langReview, reviewAdapter);

        ArrayList<Trailer> trailers = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);

    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }

    private void init() {
        viewModelDet = new ViewModelProvider(this).get(DetailViewModel.class);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
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