package com.example.android.popularmovies;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    //instance of the click handling interface
    private final trailerOnClickHandler selectedMoviClickHandler;
    private final String TRAILER_TITLE = "Trailer ";
    //used to store the movies data
    private ArrayList<Movie.Trailer> myData = new ArrayList<>();
    // the context of the application
    private Context myContext;
    // Constructor of the adapter
    TrailerAdapter(trailerOnClickHandler handler) {
        this.selectedMoviClickHandler = handler;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout of the recycler view
        Context context = parent.getContext();
        this.myContext = context;
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        //getting the movie of the current position
        holder.mTrailerTitleTextview.setText(new StringBuilder().append(TRAILER_TITLE).append(position + 1).toString());
    }

    //returning the number of the movies
    @Override
    public int getItemCount() {
        return myData.size();
    }

    // used to set the data to the adapter for example when changing the method of
    // obtaining the movies from popular to top rated
    public void setData(ArrayList<Movie.Trailer> data) {
        this.myData = data;
        notifyDataSetChanged();
    }

    /**
     * Creating the interface to interact with the user clicks
     */
    public interface trailerOnClickHandler {
        void onClick(Movie.Trailer r);
    }
    //The view holder class of the recycler view adapter
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //the image view of the item
        final TextView mTrailerTitleTextview;
        final ImageView mShareImageView;

        //The constructor of the view holder
        private TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerTitleTextview = itemView.findViewById(R.id.trailer_listitem_title);
            mShareImageView = itemView.findViewById(R.id.trailer_listitem_share_imageview);
            mShareImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Movie.Trailer trai = myData.get(pos);
                    String address = NetworkUtils.YOUTUBE_BASE_URL + trai.getKey();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, address);
                    PackageManager manager = myContext.getPackageManager();
                    if (intent.resolveActivity(manager) != null) {
                        myContext.startActivity(intent);
                    } else {
                        Log.e("hassan", "No Intent available to handle share action");
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        // handling the click on the recycler view
        @Override
        public void onClick(View v) {
            Log.i("hassan", "Trailer clicekd");
            int pos = getAdapterPosition();
            Movie.Trailer x = myData.get(pos);
            selectedMoviClickHandler.onClick(x);
        }
    }
}
