package com.example.hotelfinder.data;

import java.io.Serializable;

public class Room implements Serializable {
    private String id_hotel;
    private String image;
    private String name;
    private String id;
    private Integer price;
    private String capacity;
    private Integer bed_count;
    private String bed_type;

    public Room() {}

    public Room(String id_hotel, String image, String name, String id, Integer price, String capacity, Integer bed_count, String bed_type) {
        this.id_hotel = id_hotel;
        this.image = image;
        this.name = name;
        this.id = id;
        this.price = price;
        this.capacity = capacity;
        this.bed_count = bed_count;
        this.bed_type = bed_type;
    }

    public Room(Room room) {
        this.id_hotel = room.id_hotel;
        this.image = room.image;
        this.name = room.name;
        this.id = room.id;
        this.price = room.price;
        this.capacity = room.capacity;
        this.bed_count = room.bed_count;
        this.bed_type = room.bed_type;
    }

    public String getId_hotel() {
        return id_hotel;
    }

    public void setId_hotel(String id_hotel) {
        this.id_hotel = id_hotel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public Integer getBed_count() {
        return bed_count;
    }

    public void setBed_count(Integer bed_count) {
        this.bed_count = bed_count;
    }

    public String getBed_type() {
        return bed_type;
    }

    public void setBed_type(String bed_type) {
        this.bed_type = bed_type;
    }
}
