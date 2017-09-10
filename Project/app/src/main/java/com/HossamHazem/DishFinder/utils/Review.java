package com.HossamHazem.DishFinder.utils;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review implements Serializable {
    String authorName;
    double rating;
    String text;
    String time;

    public Review(String authorName, double rating, String text, String time) {
        this.authorName = authorName;
        this.rating = rating;
        this.text = text;
        this.time = time;
    }

    public String getAuthorName() {
        return authorName;
    }

    public double getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        long timestamp = Long.parseLong(time) * 1000;
        try {
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
