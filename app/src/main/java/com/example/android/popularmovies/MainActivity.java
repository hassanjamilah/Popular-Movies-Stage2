package com.example.android.popularmovies;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.database.MovieDatabase;
import com.example.android.popularmovies.database.MovieEntity;
import com.example.android.popularmovies.utilities.MovieJsonParser;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        MovieAdapter.movieOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {
    //dfining the keys that will be used to transfer data between the activities
    public static final String BUNDLE_TITLE_KEY = "bundleTitle";
    public static final String BUNDLE_DATE_KEY = "bundleDate";
    public static final String BUNDLE_VOTE_KEY = "bundleVote";
    public static final String BUNDLE_IMAGE_KEY = "bundleImage";
    public static final String BUNDLE_OVERVIEW_KEY = "bundleOverview";
    public static final String BUNDLE_MOVIE_ID = "bundleId";
    //defining the key that will be used to store data in the bundle in the state of rotation .....
    private static final String BUNDLE_KEY = "movies";
    // the key of the loader and its bundle key
    private final static int LOAER_KEY = 20;
    private final static String LOADER_BUNDLE_KEY = "myurl";
    public static boolean favChanged = false;
    private static MovieDatabase mdb;
    // the list of the movies
    private static ArrayList<Movie> movies;
    //preparing the tools of the recycler view the adapter and the manager
    private MovieAdapter adapter;
    // store the type of getting the mvoies popular of top rated
    private String sortMovies;
    // preparing the views variables
    private TextView error_TextView;
    private ProgressBar indicator_ProgressBar;
    private RecyclerView movie_RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movie_RecyclerView = findViewById(R.id.movie_recyclerview);
        error_TextView = findViewById(R.id.error_msg_tv);
        indicator_ProgressBar = findViewById(R.id.loading_indicator);
        //initializing the adapter
        adapter = new MovieAdapter(this);
        mdb = MovieDatabase.getmInstance(this);
        //checking the screen orientation if landscape
        //make the grid has three cols
        //else has two cols


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        readPrefs();
        GridLayoutManager manager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            manager = new GridLayoutManager(this, 3);
        } else {
            manager = new GridLayoutManager(this, 2);
        }
        //preparing the recycler view
        movie_RecyclerView.setLayoutManager(manager);
        movie_RecyclerView.setAdapter(adapter);
        movie_RecyclerView.setHasFixedSize(true);
        //reading the preferences and register the preference listener


        //reading the data after the instance changed
        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_KEY)) {
            movies = new ArrayList<>();
            //reading the movies data
            try {
                if (sortMovies.equals(getString(R.string.popular_value))) {
                    doWork(NetworkUtils.ORDER_BY_POPULAR_URL);
                } else if (sortMovies.equals(getString(R.string.fav_value))) {
                    doWork(NetworkUtils.ORDER_BY_FAVOURITES);
                } else {
                    doWork(NetworkUtils.ORDER_BY_RATING_URL);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("hassan" , e.getMessage());
                showErrorMsg(getString(R.string.error_msg));
            } catch (IOException e) {
                e.printStackTrace();

                showErrorMsg(getString(R.string.error_msg));
            }
        } else {
            movies = savedInstanceState.getParcelableArrayList(BUNDLE_KEY);
           adapter.setData(movies);
        }
    }

    //reading preferences and save the get movie state popular or top rated
    private void readPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortMovies = prefs.getString(getString(R.string.sort_prefs_key), getString(R.string.popular_value));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_KEY, movies);
        super.onSaveInstanceState(outState);
    }
    //Creating the settings menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPrefs();

    }
    // Responding to the settings menu action

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings_action) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    /**
     * this method do the most important work
     *
     * @param order the way to obtain movies popular or most rated
     * @throws IOException if an error occured throws an exception
     */
    private void doWork(String order) throws IOException {
        //prepareing the url
        Bundle bundle = new Bundle();
        if (order.contains(NetworkUtils.ORDER_BY_FAVOURITES)) {
           // String s = order;
            //bundle.putString(LOADER_BUNDLE_KEY, s.toString());
            loadFavouates();
        } else {
            final URL s = NetworkUtils.getUrl(order);
            bundle.putString(LOADER_BUNDLE_KEY, s.toString());
            Loader<Movie> loader = getSupportLoaderManager().getLoader(LOAER_KEY);
            if (loader == null) {
                getSupportLoaderManager().initLoader(LOAER_KEY, bundle, this);
            } else {
                getSupportLoaderManager().restartLoader(LOAER_KEY, bundle, this);
            }
        }
        //preparing the bundle to send it to the loader
        //preparing the loader and inintilize it

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        indicator_ProgressBar.setVisibility(View.VISIBLE);
        return new MyTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        indicator_ProgressBar.setVisibility(View.INVISIBLE);
        //indicate to show the error msg or if everything is well display data
        if (data == null) {
            Log.i("hassan" , "the data is null") ;
            showErrorMsg(getString(R.string.error_msg));
        } else {
            adapter.setData(movies);
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    /**
     * Handling the click on our recyclerview
     *
     * @param m the movie has been selected
     */
    @Override
    public void onClick(Movie m) {
        Intent intent = new Intent(this, DetailsActivity.class);
        //putting the data to send it to the details activity
        intent.putExtra(BUNDLE_TITLE_KEY, m.getTitle());
        intent.putExtra(BUNDLE_DATE_KEY, m.getRelaeseDate());
        intent.putExtra(BUNDLE_VOTE_KEY, m.getRating());
        intent.putExtra(BUNDLE_IMAGE_KEY, m.getImgeName());
        intent.putExtra(BUNDLE_OVERVIEW_KEY, m.getOverView());
        intent.putExtra(BUNDLE_MOVIE_ID, m.getId());
        Log.i("hassan", m.getId() + "");
        startActivity(intent);
    }

    /**
     * responding to the user changing of the way we get movies
     *
     * @param sharedPreferences the preferences
     * @param key               the key of preference
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sort_prefs_key))) {
            String order = sharedPreferences.getString(key, "");
            try {
                if (order.equals(getString(R.string.popular_value))) {
                    doWork(NetworkUtils.ORDER_BY_POPULAR_URL);
                } else if (order.equals(getString(R.string.fav_value))) {
                    loadFavouates();
                } else {
                    doWork(NetworkUtils.ORDER_BY_RATING_URL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFavouates() {
        MoviesViewModel model = ViewModelProviders.of(this).get(MoviesViewModel.class);

        model.getMovies().observe(this, new Observer<List<MovieEntity>>() {

            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                Log.i("hassan" , movieEntities.size() + "is the size of movies") ;
                movies = new ArrayList<>();


                if (movieEntities != null){
                    for (MovieEntity ent : movieEntities) {
                        Movie m = new Movie();
                        m.setImgeName(ent.getImgeName());
                        m.setId(ent.getMovieId());
                        m.setOverView(ent.getOverView());
                        m.setRating(ent.getRating());
                        m.setRelaeseDate(ent.getRelaeseDate());
                        m.setTitle(ent.getTitle());
                        movies.add(m);
                    }
                    adapter.setData(movies);
                    showDataView();
                }



            }
        });
    }

    /**
     * used to show the error message
     */
    private void showErrorMsg(String msg) {
        error_TextView.setText(msg);
        error_TextView.setVisibility(View.VISIBLE);
        movie_RecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * if no errors hide the error message and display data
     */
    private void showDataView() {
        error_TextView.setVisibility(View.INVISIBLE);
        movie_RecyclerView.setVisibility(View.VISIBLE);
    }

    public static class MyTaskLoader extends AsyncTaskLoader<String> {
        final Bundle args;
        String mData;

        private MyTaskLoader(@NonNull Context context, Bundle bundleArgs) {
            super(context);
            this.args = bundleArgs;
        }

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                deliverResult(mData);
            } else {
                forceLoad();
            }
        }

        @Nullable
        @Override
        public String loadInBackground() {
            NetworkUtils util = new NetworkUtils();
            MovieJsonParser parser = new MovieJsonParser();
            String x1 = args.getString(LOADER_BUNDLE_KEY, "");
            if (x1.contains(NetworkUtils.ORDER_BY_FAVOURITES)) {
                return "favs";
            } else {
                String x;
                String s;

                s = args.getString(LOADER_BUNDLE_KEY);
                try {
                    x = util.getHttpResponse(new URL(s));
                    movies = parser.parseMove(x);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                return x;
            }
        }

        @Override
        public void deliverResult(@Nullable String data) {
            mData = data;
            super.deliverResult(data);
        }
    }
}
