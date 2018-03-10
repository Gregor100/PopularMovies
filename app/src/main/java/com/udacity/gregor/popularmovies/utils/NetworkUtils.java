package com.udacity.gregor.popularmovies.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by Gregor on 25.02.2018.
 */



public class NetworkUtils {


    static URL buildUrl(String apiString) {
        Uri builtUri = Uri.parse(apiString)
                .buildUpon()
                .build();

        URL url = null;

        try {
            String uriString = builtUri.toString();
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return url;
    }



    @Nullable
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

}
