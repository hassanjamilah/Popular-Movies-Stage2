package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("select * from favMovies order by movieId")
    LiveData<List<MovieEntity>> loadAllMovies() ;

    @Insert
     void insertMovie(MovieEntity movie) ;

    @Update(onConflict =  OnConflictStrategy.REPLACE)
     void updateMovie(MovieEntity movie) ;


    @Delete
     void deleteMovie(MovieEntity movie) ;


    @Query("select * from favMovies where movieId = :id order by movieId")
     LiveData<List<MovieEntity>> loadMoviesById(int id ) ;


}
