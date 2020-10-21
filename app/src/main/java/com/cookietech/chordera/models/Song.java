package com.cookietech.chordera.models;

/**
 * This is the song class
 *
 */

public class Song implements Comparable<Song>{

    private String tittle, artistName, bandName, genre, updateDate, totalView;

    public Song(String titlle, String artistName, String bandName, String genre, String updateDate, String totalView)
    {
        this.tittle = titlle;
        this.artistName = artistName;
        this.bandName = bandName;
        this.genre = genre;
        this.updateDate = updateDate;
        this.totalView = totalView;
    }
/*
    @Override
    public int compareTo(Song song) {
        if(song.getTittle().equals(this.tittle) && song.getArtistName().equals(this.artistName) && song.getBandName().equals(this.bandName) &&
                song.getGenre().equals(this.genre) && song.getUpdateDate().equals(this.updateDate) && song.getTotalView().equals(this.totalView))
        {
            return 0;
        }
        return 1;
    }

 */

    @Override
    public int compareTo(Song song) {
        if(song.getTittle().equals(this.tittle) && song.getBandName().equals(this.bandName))
        {
            return 0;
        }
        return 1;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getTotalView() {
        return totalView;
    }

    public void setTotalView(String totalView) {
        this.totalView = totalView;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }



}
