package com.example.amit.newsapp;

import android.graphics.Bitmap;

import java.net.URL;

public class NewsItem {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private Bitmap bitmap;

    public NewsItem(String title,String description,String url,String urlToImage){
        this.title=title;
        if(description.equals("")||description.equals("null"))
            this.description="Please tap on news to visit site for description.";
        else
            this.description=description;
        this.url=url;
        this.urlToImage=urlToImage;
        bitmap=null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
