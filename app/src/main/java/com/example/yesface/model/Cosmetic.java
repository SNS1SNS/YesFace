package com.example.yesface.model;

import java.io.Serializable;

public class Cosmetic implements Serializable {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String rating;  // Change rating to double for easier manipulation

    // Empty constructor for Firebase
    public Cosmetic() {}

    public Cosmetic(String name, String description, double price, String imageUrl, String rating) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
