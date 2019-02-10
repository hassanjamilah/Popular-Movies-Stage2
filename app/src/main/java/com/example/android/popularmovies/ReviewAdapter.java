package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    //instance of the click handling interface
    private final movieOnClickHandler selectedMoviClickHandler;
    //used to store the movies data
    private ArrayList<Movie.Review> myData = new ArrayList<>();
    // the context of the application
    private Context myContext;
    // Constructor of the adapter
    ReviewAdapter(movieOnClickHandler handler) {
        this.selectedMoviClickHandler = handler;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout of the recycler view

        this.myContext = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        //getting the movie of the current position
        Movie.Review r = myData.get(position);
        holder.mTitleTextView.setText(r.getAuthor());
        holder.mContentTextView.setText(r.getContent());
    }

    //returning the number of the movies
    @Override
    public int getItemCount() {
        return myData.size();
    }

    // used to set the data to the adapter for example when changing the method of
    // obtaining the movies from popular to top rated
    public void setData(ArrayList<Movie.Review> data) {
        this.myData = data;
        notifyDataSetChanged();
    }

    /**
     * Creating the interface to interact with the user clicks
     */
    public interface movieOnClickHandler {
        void onClick(Movie.Review r);
    }
    //The view holder class of the recycler view adapter
    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //the image view of the item
        final TextView mTitleTextView;
        final TextView mContentTextView;

        //The constructor of the view holder
        private ReviewViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.reviewlistitem_title);
            mContentTextView = itemView.findViewById(R.id.reviewlistitem_content);
            itemView.setOnClickListener(this);
        }

        // handling the click on the recycler view
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Movie.Review x = myData.get(pos);
            selectedMoviClickHandler.onClick(x);
        }
    }
}
