package com.example.android.popularmovies;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.ReviewAdapter.movieOnClickHandler;
import com.example.android.popularmovies.database.MovieDatabase;
import com.example.android.popularmovies.database.MovieEntity;
import com.example.android.popularmovies.utilities.MovieJsonParser;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, movieOnClickHandler, TrailerAdapter.trailerOnClickHandler {
    // the key of the loader and its bundle key
    private final static int LOAER_KEY = 25;
    private final static int DATABASE_LOAER_KEY = 30;
    private final static String LOADER_MOVIE_BUNDLE_KEY = "movieid";
    private final static String LOADER_MOVIE_BUNDLE_TITLE = "movietitle";
    public static String movieReviews;
    public static String movieTrailers;
    //used to store the data in the bundle in the case of rotation or ....
    private final String BUNDLE_KEY = "saveinstance";
    private static AsyncTask<String, String, String>  task ;
    MovieDatabase mdb;
    private TextView date_TextView;
    private TextView votes_TextView;
    private TextView overview_TextView;
    private RecyclerView reviews_RecyclerView;
    private RecyclerView trailers_RecyclerView;
    private ImageButton favButton;
    private ImageView poster_ImageView;
    // used to store the data of the movie
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        date_TextView = findViewById(R.id.movie_details_date);
        votes_TextView = findViewById(R.id.movie_details_vote);
        overview_TextView = findViewById(R.id.movie_details_overview);
        reviews_RecyclerView = findViewById(R.id.reviews_recyclerview);
        trailers_RecyclerView = findViewById(R.id.trailers_recyclerview);
        favButton = findViewById(R.id.fav_button);
        favButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    addToFavourite();

            }
        });
        mdb = MovieDatabase.getmInstance(this);
        poster_ImageView = findViewById(R.id.movie_details_imageview);
        //Getting the data from the intent if exist
        Intent startedActivity = getIntent();
        String temp;
        movie = new Movie();
        //getting the date from the intent
        if (startedActivity.hasExtra(MainActivity.BUNDLE_DATE_KEY)) {
            temp = startedActivity.getStringExtra(MainActivity.BUNDLE_DATE_KEY);
            movie.setRelaeseDate(temp);
        }
        //getting the title from the intent
        if (startedActivity.hasExtra(MainActivity.BUNDLE_TITLE_KEY)) {
            temp = startedActivity.getStringExtra(MainActivity.BUNDLE_TITLE_KEY);
            movie.setTitle(temp);
        }
        //getting the rating from the intnet
        if (startedActivity.hasExtra(MainActivity.BUNDLE_VOTE_KEY)) {
            temp = startedActivity.getStringExtra(MainActivity.BUNDLE_VOTE_KEY);
            movie.setRating(temp);
        }

        /*
        getting the overview from the intent
         */
        if (startedActivity.hasExtra(MainActivity.BUNDLE_OVERVIEW_KEY)) {
            temp = startedActivity.getStringExtra(MainActivity.BUNDLE_OVERVIEW_KEY);
            movie.setOverView(temp);
        }

        /*
        getting the image name from the intent
         */
        if (startedActivity.hasExtra(MainActivity.BUNDLE_IMAGE_KEY)) {
            temp = startedActivity.getStringExtra(MainActivity.BUNDLE_IMAGE_KEY);
            movie.setImgeName(temp);
        }


        /*
        getting the id from the intent
         */
        if (startedActivity.hasExtra(MainActivity.BUNDLE_MOVIE_ID)) {
            int tem = startedActivity.getIntExtra(MainActivity.BUNDLE_MOVIE_ID, 0);
            movie.setId(tem);
            Log.i("hassan", movie.getId() + " details id");
        }
        //used to diplay the data stored in the movie variable
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY)) {
            movie = savedInstanceState.getParcelable(BUNDLE_KEY);
        }
        displayMovieData();

            doWork(movie.getId());

        LiveData<List<MovieEntity>> ents = mdb.movieDAO().loadMoviesById(movie.getId());
        ents.observe(this, new Observer<List<MovieEntity>>() {

            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {

               if (movieEntities !=null){

                    if (movieEntities.size() > 0) {
                        favButton.setImageResource(R.drawable.fav_filed);
                        movie.setFavourite(true);
                    } else {
                        favButton.setImageResource(R.drawable.fav_null);
                        movie.setFavourite(false);
                    }
               }
            }
        });
        hideAll(overview_TextView);
    }

    private void hideAll(View v) {
        overview_TextView.setVisibility(View.VISIBLE);
        trailers_RecyclerView.setVisibility(View.VISIBLE);
        reviews_RecyclerView.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
    }

    private void addToFavourite() throws SQLiteConstraintException {
        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_MOVIE_BUNDLE_KEY, movie.getId());
        bundle.putString(LOADER_MOVIE_BUNDLE_TITLE, movie.getTitle());
        Loader<Movie> loader = getSupportLoaderManager().getLoader(DATABASE_LOAER_KEY);
        if (loader == null) {
            getSupportLoaderManager().initLoader(DATABASE_LOAER_KEY, bundle, this);
        } else {
            getSupportLoaderManager().restartLoader(DATABASE_LOAER_KEY, bundle, this);
        }
        int id = movie.getId();
        String title = movie.getTitle();
        String imageName = movie.getImgeName();
        String rating = movie.getRating();
        String releaseDate = movie.getRelaeseDate();
        String overView = movie.getOverView();
        final MovieEntity mEnt = new MovieEntity(id, title, imageName, rating, releaseDate, overView);
         task= new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                if (movie.isFavourite()) {
                    mdb.movieDAO().deleteMovie(mEnt);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            favButton.setImageResource(R.drawable.fav_null);
                        }
                    });
                } else {
                    mdb.movieDAO().insertMovie(mEnt);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            favButton.setImageResource(R.drawable.fav_filed);
                        }
                    });
                }
                MainActivity.favChanged = true;
                Log.i("hassan", "The movie has been added to favourite");
                return null;
            }
        };
        task.execute("", "", "");
    }

    private void displayMovieData() {
        date_TextView.setText(movie.getRelaeseDate());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(movie.getTitle());
        }
        votes_TextView.setText(new StringBuilder().append(movie.getRating()).append(getString(R.string.percent)).toString());
        overview_TextView.setText(movie.getOverView());

        Picasso.with(this).load(NetworkUtils.BASE_IMAGE_URL + movie.getImgeName()).into(poster_ImageView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_KEY, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void doWork(int movieId)  {
        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_MOVIE_BUNDLE_KEY, movieId);
        Loader<Movie> loader = getSupportLoaderManager().getLoader(LOAER_KEY);
        if (loader == null) {
            getSupportLoaderManager().initLoader(LOAER_KEY, bundle, this);
        } else {
            getSupportLoaderManager().restartLoader(LOAER_KEY, bundle, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == LOAER_KEY) {
            Log.i("hassan", "On create loader details loader");
            return new MyDetailsLoader(this, args);
        } else {
            Log.i("hassan", "On create loader database loader");
            return new myDatabaseOperations(this, args);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (loader.getId() == DATABASE_LOAER_KEY) {
            Log.i("hassan", "database loaiding finished");
            return;
        }
        Log.i("hassan", "text View reviews are -------------:  " + movieReviews);
        MovieJsonParser parser = new MovieJsonParser();

        try {
            ArrayList<Movie.Review> revs = parser.parseMoveReview(movieReviews);
            Log.i("hassan", "The revs size is : " + revs.size());
            /*for (int i = 0 ; i<revs.size() ; i++){
                x = x + "\n" +  revs.get(i).getAuthor() ;
            }*/
            ReviewAdapter adapter = new ReviewAdapter(this);
            adapter.setData(revs);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            reviews_RecyclerView.setAdapter(adapter);
            reviews_RecyclerView.setLayoutManager(manager);
            reviews_RecyclerView.setHasFixedSize(true);
            ArrayList<Movie.Trailer> trails = parser.parseMoveTrailer(movieTrailers);
            TrailerAdapter adapter1 = new TrailerAdapter(this);


                    /*new TrailerAdapter.trailerOnClickHandler() {

                @Override
                public void onClick(Movie.Trailer r) {


                }
            });*/
            adapter1.setData(trails);
            LinearLayoutManager manager1 = new LinearLayoutManager(this);
            trailers_RecyclerView.setAdapter(adapter1);
            trailers_RecyclerView.setLayoutManager(manager1);
            trailers_RecyclerView.setHasFixedSize(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // reviews_RecyclerView.setText(x);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onClick(Movie.Review r) {
        Log.i("hassan", r.getContent());
    }

    @Override
    public void onClick(Movie.Trailer r) {
        Log.i("hassan", "Movie Trailer : " + r.getKey());
        String address = NetworkUtils.YOUTUBE_BASE_URL + r.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + r.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(intent);
        }
    }

    public static class MyDetailsLoader extends AsyncTaskLoader<String> {
        final Bundle args;
        String mData;

        private MyDetailsLoader(Context context, Bundle bundleArgs) {
            super(context);
            this.args = bundleArgs;
        }

        @Override
        public String loadInBackground() {
            int movieId = args.getInt(LOADER_MOVIE_BUNDLE_KEY);
            try {
                URL reviewUrl = NetworkUtils.getMovieReviewUrl(movieId);
                URL trailerUrl = NetworkUtils.getMovieVideosUrl(movieId);
                NetworkUtils utils = new NetworkUtils();
                String reviews = utils.getHttpResponse(reviewUrl);
                Log.i("hassan", "The  movie reviews is : " + reviews);
                String trailers = utils.getHttpResponse(trailerUrl);



                movieReviews = reviews;
                movieTrailers = trailers;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                deliverResult(mData);
            } else {
                forceLoad();
            }
        }

        @Override
        public void deliverResult(String data) {
            Log.i("hassan", "deliver result data : " + data);
            mData = data;
            super.deliverResult(data);
        }
    }
    public static class myDatabaseOperations extends AsyncTaskLoader<String> {
        private myDatabaseOperations(@NonNull Context context, Bundle args) {
            super(context);
        }

        @Nullable
        @Override
        public String loadInBackground() {
            return null;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
        }

        @Override
        public void deliverResult(@Nullable String data) {
            super.deliverResult(data);
        }
    }
}
