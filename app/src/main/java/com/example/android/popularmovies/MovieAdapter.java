package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    //used to store the movies data
    private ArrayList<Movie> myData = new ArrayList<>();

    // the context of the application
    private Context myContext;

    //instance of the click handling interface
    private final movieOnClickHandler selectedMoviClickHandler;


    /**
     * Creating the interface to interact with the user clicks
     */
    public interface movieOnClickHandler {
        void onClick(Movie m);
    }


    // Constructor of the adapter
    MovieAdapter(movieOnClickHandler handler) {
        this.selectedMoviClickHandler = handler;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout of the recycler view
        Context context = parent.getContext();
        this.myContext = context;

        int layoutIdForListItem = R.layout.movie_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);



        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MovieViewHolder(view);
    }

    //the action when the view holder binding
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        //getting the movie of the current position
        Movie m = myData.get(position);

        //obtaining the image of the movie
        String imagePath = m.getImgeName();
        Picasso.with(myContext).load("http://image.tmdb.org/t/p/w185/" + imagePath).into(holder.mImageView);

    }


    //returning the number of the movies
    @Override
    public int getItemCount() {
        return myData.size();
    }

    //The view holder class of the recycler view adapter
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //the image view of the item
        final ImageView mImageView;

        //The constructor of the view holder
        private MovieViewHolder(View itemView) {
            super(itemView);
            mImageView =  itemView.findViewById(R.id.movie_item_imageview);
            itemView.setOnClickListener(this);

        }

        // handling the click on the recycler view
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Movie x = myData.get(pos);
            selectedMoviClickHandler.onClick(x);
        }
    }

    // used to set the data to the adapter for example when changing the method of
    // obtaining the movies from popular to top rated
    public  void setData(ArrayList<Movie> data) {
        this.myData = data;
        notifyDataSetChanged();
    }
}
