package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {

    private String title ;
    private String imgeName ;
    private String rating ;
    private String relaeseDate ;
    private String overView ;
    private boolean isFavourite ;
    private int id ;
    private ArrayList<Review> reviews ;
    private ArrayList<Trailer> trailers ;

    public Movie(){}

    /*public Movie(String title, String imgeName, String rating, String relaeseDate, String overView, int id) {
        this.title = title;
        this.imgeName = imgeName;
        this.rating = rating;
        this.relaeseDate = relaeseDate;
        this.overView = overView;
        this.id = id;
    }*/

    private Movie(Parcel in) {
        title = in.readString();
        imgeName = in.readString();
        rating = in.readString();
        relaeseDate = in.readString();
        overView = in.readString();
        isFavourite = in.readByte()!= 0;
        id = in.readInt();
        reviews = in.readArrayList(Review.class.getClassLoader()) ;
        trailers = in.readArrayList(Trailer.class.getClassLoader()) ;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imgeName);
        dest.writeString(rating);
        dest.writeString(relaeseDate);
        dest.writeString(overView);
        dest.writeInt(id);
        dest.writeList(reviews);
        dest.writeList(trailers);
        dest.writeByte((byte)( isFavourite ? 1:0));
    }


   /* public String toString(){
        StringBuilder builder;
        builder = new StringBuilder();
        builder.append("Movie Title: ").append(title);
        builder.append("\nMovie Image Name: " + imgeName) ;
        builder.append("\nMovie Rating: " + rating) ;
        builder.append("\nMovie Release Date: " + relaeseDate) ;
        builder.append("\nMovie Overview: " + overView) ;
        builder.append("\nMovie id: " + String.valueOf(id)) ;
         return builder.toString() ;
    }*/

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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId (){return this.id ; }


    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    /**
     * This class is used to store a review
     * in the Movie class we'll have a list of Review s
     */

    public static class Review{

        private String author ;
        private String content ;
        private String id ;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }


    /**
     * This class is used to save trailer data
     *
     */
    public static class Trailer {
        private String key ;
        private String name ;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
