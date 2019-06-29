package com.example.flickster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    // the base url for loading images
    String imageBaseUrl;
    // the poster size when fetching images
    String posterSize;
    // backdrop size to use when fetching images
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        // go to images so then you can get secure_base_url
        JSONObject images = object.getJSONObject("images");

        // get the image base url
        imageBaseUrl = images.getString("secure_base_url");

        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");

        // use the options at index 3 of poster_sizes (fallback is w342)
        posterSize = posterSizeOptions.optString(3, "w342");

        // parse the backdrop sizes and use the pption at index 1 (fallback is w780)
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");
    }

    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
