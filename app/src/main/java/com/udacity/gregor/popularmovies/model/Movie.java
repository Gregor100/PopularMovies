package com.udacity.gregor.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.gregor.popularmovies.MainActivity;

/**
 * Created by Gregor on 25.02.2018.
 */



public class Movie implements Parcelable {
    private int voteCount;
    private String title;
    private String releaseDate;
    private String posterPath;
    private String synopsis;

    public Movie(String title, int voteCount, String releaseDate, String posterPath, String synopsis){
        this.voteCount = voteCount;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
    }

    public int getVoteCount(){
        return voteCount;
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

    private Movie(Parcel input){
        String[] details = new String[5];
        input.readStringArray(details);
        title = details[MainActivity.titlePosition];
        releaseDate = details[MainActivity.releaseDatePosition];
        synopsis = details[MainActivity.synopsisPosition];
        voteCount = Integer.parseInt(details[MainActivity.voteCountPosition]);
        posterPath = details[MainActivity.posterPathPosition];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{title,
        Integer.toString(voteCount),
        releaseDate,
        posterPath,
        synopsis});
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
