package com.example.movieapp.Domain;
//chua dung den

import java.util.List;

public class Genres {
    private List<GenresItem> genres;

    public List<GenresItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresItem> genres) {
        this.genres = genres;
    }
}