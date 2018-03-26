package com.udacity.gregor.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.gregor.popularmovies.MainActivity;

/**
 * Created by Gregor on 25.02.2018.
 */



public class Movie implements Parcelable {
    private double voteAverage;
    private String title;
    private String releaseDate;
    private String posterPath;
    private String synopsis;
    private String id;



    public Movie(String title, double voteAverage, String releaseDate, String posterPath, String synopsis, String id){
        this.voteAverage = voteAverage;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.id = id;
    }


    public double getVoteAverage(){
        return voteAverage;
    }

    public String getMovieTitle(){
        return title;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public String getPosterPath(){
        return posterPath;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public String getId(){
        return id;
    }


    private Movie(Parcel input){
        String[] details = new String[MainActivity.NUMBER_OF_INTENT_DETAILS];
        input.readStringArray(details);
        title = details[MainActivity.titlePosition];
        releaseDate = details[MainActivity.releaseDatePosition];
        synopsis = details[MainActivity.synopsisPosition];
        voteAverage = Double.parseDouble(details[MainActivity.voteAveragePosition]);
        posterPath = details[MainActivity.posterPathPosition];
        id = details[MainActivity.idPosition];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{title,
        Double.toString(voteAverage),
        releaseDate,
        posterPath,
        synopsis,
        id});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Movie createFromParcel(Parcel input){
            return new Movie(input);
        }
        public Movie[] newArray(int size){
            return new Movie[size];
        }
    };
}
