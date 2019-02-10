package com.example.android.popularmovies.utilities;
import android.util.Log;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class MovieJsonParser {
    /**
     * defining the keys that will be used to parse the JSON File
     * Main key is results
     */
    private static final String results_key = "results";
    private static final String TITLE_KEY = "title";
    private static final String IMAGE_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String DATE_KEY = "release_date";
    private static final String VOTES_KEY = "vote_average";
    private static final String ID_KEY = "id";
    private static final String REVIEW_ID_KEY = "id";
    private static final String REVIEW_AUTHOR_KEY = "author";
    private static final String REVIEW_CONTENT_KEY = "content";
    private static final String TRAILER_NAME_KEY = "name";
    private static final String TRAILER_KEY_KEY = "key";

    /**
     * used to parse the movie data from the json result
     *
     * @param jsonString the string that were obtained from the query
     * @return returning the list of movies
     * @throws JSONException if an error occured throwing the exception
     */
    public ArrayList<Movie> parseMove(String jsonString) throws JSONException {
        ArrayList<Movie> resultMovies = new ArrayList<>();
        // getting the main object
        JSONObject results = new JSONObject(jsonString);
        // reading the list of movies from the result object
        JSONArray movies1 = results.getJSONArray(results_key);
        Movie mov;
        // reading and storing the data of the movies in an arraylist
        for (int i = 0; i < movies1.length(); i++) {
            mov = new Movie();
            JSONObject obj = movies1.getJSONObject(i);
            mov.setId(obj.getInt(ID_KEY));
            mov.setImgeName(obj.getString(IMAGE_KEY));
            mov.setOverView(obj.getString(OVERVIEW_KEY));
            mov.setRating(obj.getString(VOTES_KEY));
            mov.setRelaeseDate(obj.getString(DATE_KEY));
            mov.setTitle(obj.getString(TITLE_KEY));
            resultMovies.add(mov);
        }
        // returning the result of the movies
        return resultMovies;
    }



    /**
     * used to parse the movie data from the json result
     *
     * @param jsonString the string that were obtained from the query
     * @return returning the list of reviews
     * @throws JSONException if an error occured throwing the exception
     */
    public ArrayList<Movie.Review> parseMoveReview(String jsonString) throws JSONException {
        ArrayList<Movie.Review> resultReviews = new ArrayList<>();
        // getting the main object
        JSONObject results = new JSONObject(jsonString);
        // reading the list of movies from the result object
        JSONArray reviews1 = results.getJSONArray(results_key);
        Log.i("hassan", "The reviewss array length is : " + reviews1.length());
        // reading and storing the data of the movies in an arraylist
        for (int i = 0; i < reviews1.length(); i++) {
            Movie.Review r = new Movie.Review();
            JSONObject obj = reviews1.getJSONObject(i);
            r.setAuthor(obj.getString(REVIEW_AUTHOR_KEY));
            r.setContent(obj.getString(REVIEW_CONTENT_KEY));
            r.setId(obj.getString(REVIEW_ID_KEY));
            resultReviews.add(r);
        }
        // returning the result of the movies
        return resultReviews;
    }

    /**
     * used to parse the trailers data from the json result
     *
     * @param jsonString the string that were obtained from the query
     * @return returning the list of reviews
     * @throws JSONException if an error occured throwing the exception
     */
    public ArrayList<Movie.Trailer> parseMoveTrailer(String jsonString) throws JSONException {
        ArrayList<Movie.Trailer> resultTrailers = new ArrayList<>();
        // getting the main object
        JSONObject results = new JSONObject(jsonString);
        // reading the list of movies from the result object
        JSONArray trailers1 = results.getJSONArray(results_key);
        // reading and storing the data of the movies in an arraylist
        for (int i = 0; i < trailers1.length(); i++) {
            Movie.Trailer r = new Movie.Trailer();
            JSONObject obj = trailers1.getJSONObject(i);
            r.setKey(obj.getString(TRAILER_KEY_KEY));
            r.setName(obj.getString(TRAILER_NAME_KEY));
            resultTrailers.add(r);
        }
        return resultTrailers;
    }
}
