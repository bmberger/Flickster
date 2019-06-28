package com.example.flickster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "Flickster";


    // instance fields (only have values for specific activties/clients)
    AsyncHttpClient client;
    // the base url for loading images
    String imageBaseUrl;
    // the poster size when fetching images
    String posterSize;
    // list of currently playing movies
    ArrayList<Movie> movies;

    RecyclerView rvMovies; // recycler view of movies
    MovieAdapter adapter; // adapter wired to the recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the client
        client = new AsyncHttpClient();

        // intialize the list of movies
        movies = new ArrayList<>();

        // intialize the adapter - movies array cannot be reintialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapters
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        getConfig();
    }

    // get the list of currently playing movies from the API
    private void getNowPlaying() {
        // create the url
        String url = API_BASE_URL + "/movie/now_playing";

        //set the request parameters (appended to url) aka shows that you are a verified user
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into the movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through result set and create Movie objects

                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);

                        // notify adapter
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed parsing now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting data from new_playing endpoint", throwable, true);
            }
        });
    }

    // get the config from the API (gets data on posters)
    private void getConfig() {
        // create the url
        String url = API_BASE_URL + "/configuration";

        //set the request parameters (appended to url) aka shows that you are a verified user
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // execute a GET request that gives us a JSON object response with data if a success
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // go to images so then you can get secure_base_url
                    JSONObject images = response.getJSONObject("images");

                    // get the image base url
                    imageBaseUrl = images.getString("secure_base_url");

                    // get the poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");

                    // use the options at index 3 of poster_sizes (fallback is w342)
                    posterSize = posterSizeOptions.optString(3, "w342");
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", imageBaseUrl, posterSize));

                    // ensures correct order
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    // handle errors, logs and alert user for customized cases
    private void logError(String message, Throwable error, boolean alertUser) {
        Log.e(TAG, message, error);

        // alerts the user to avoid silent errors
        if (alertUser) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }
    }
}
