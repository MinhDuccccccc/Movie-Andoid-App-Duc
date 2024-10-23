package com.example.movieapp.dto;

public class FilmDTO {
    private String name, slug, origin_name, poster_url, thumb_url;

    public FilmDTO(String name, String slug, String origin_name, String poster_url, String thumb_url) {
        this.name = name;
        this.slug = slug;
        this.origin_name = origin_name;
        this.poster_url = poster_url;
        this.thumb_url = thumb_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
}
