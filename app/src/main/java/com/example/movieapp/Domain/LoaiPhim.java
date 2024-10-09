//4 cái loại phim á
package com.example.movieapp.Domain;

public class LoaiPhim {
    private String title;
    private int thumb;
    private String type;

    public LoaiPhim(String title, int thumb, String type) {
        this.title = title;
        this.thumb = thumb;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }
}
