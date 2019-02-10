package com.example.android.popularmovies;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.database.MovieDatabase;
import com.example.android.popularmovies.database.MovieEntity;

import java.util.List;
public class MoviesViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntity>> movies ;
    public MoviesViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getmInstance(this.getApplication());

       movies = database.movieDAO().loadAllMovies() ;
       Log.i("hassan" , "the movies size is :movies loaded " );
    }

    public LiveData<List<MovieEntity>> getMovies() {
        return movies;
    }
}
