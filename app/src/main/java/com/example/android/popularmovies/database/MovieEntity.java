package com.example.android.popularmovies.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favMovies")
public class MovieEntity {
    @PrimaryKey(autoGenerate = true)
    int movieId;
    private String title;
    private String imgeName;
    private String rating;
    private String relaeseDate;
    private String overView;

    @Ignore
    public MovieEntity(String title, String imgeName, String rating, String relaeseDate, String overView) {
        this.title = title;
        this.imgeName = imgeName;
        this.rating = rating;
        this.relaeseDate = relaeseDate;
        this.overView = overView;
    }

    public MovieEntity(int movieId, String title, String imgeName, String rating, String relaeseDate, String overView) {
        this.movieId = movieId;
        this.title = title;
        this.imgeName = imgeName;
        this.rating = rating;
        this.relaeseDate = relaeseDate;
        this.overView = overView;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgeName() {
        return imgeName;
    }

    public void setImgeName(String imgeName) {
        this.imgeName = imgeName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRelaeseDate() {
        return relaeseDate;
    }

    public void setRelaeseDate(String relaeseDate) {
        this.relaeseDate = relaeseDate;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }
}
