package com.udacity.gregor.popularmovies.model;

/**
 * Created by Gregor on 25.02.2018.
 */



public class Movie {
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


}
