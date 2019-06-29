package com.example.flickster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flickster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    // wraps the list of movies

    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    Config config;
    // context for rendering
    Context context;

    // intialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // get content and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //create new view using the item-movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // finds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());
        String imageUrl = null;

        // determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // if in portrait mode, load the poster image
        if (isPortrait) {
            // set image using glide - build url for poster image
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            // load the backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // get the correct placeholder and image view dependent on orientation
        int placeholderID = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.tvPosterImage : holder.ivBackdropImage;

        // load image using glide (as well as placeholders while loading)
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(placeholderID)
                .error(R.drawable.flicks_movie_placeholder)
                .into(imageView);
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create the viewholder as a inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // track new objects
        ImageView tvPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by id
            tvPosterImage = (ImageView) itemView.findViewById(R.id.imageView);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // gets item position
            int position = getAdapterPosition();
            // ensure is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at position
                Movie movie = movies.get(position);

                // create activity's intent
                Intent intent = new Intent(context, MovieDetailsActivity.class);

                // serialize the movie using parceler
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // show activity
                context.startActivity(intent);
            }
        }
    }
}
