package com.example.hotelfinder.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String name;
    private String id_hotel;
    private String address;
    private GeoPoint location;
    private Integer rating;
    private String image;

    public Hotel() {}

    public Hotel(String name, String id_hotel, String address, GeoPoint location, Integer rating, String image) {
        this.name = name;
        this.id_hotel = id_hotel;
        this.address = address;
        this.location = location;
        this.rating = rating;
        this.image = image;
    }

    public Hotel(Hotel hotel) {
        this.name = hotel.getName();
        this.id_hotel = hotel.getId_hotel();
        this.address = hotel.getAddress();
        this.location = hotel.getLocation();
        this.rating = hotel.getRating();
        this.image = hotel.getImage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_hotel() {
        return id_hotel;
    }

    public void setId_hotel(String id_hotel) {
        this.id_hotel = id_hotel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + id_hotel + " " + location + " " + String.valueOf(rating);
    }
}
