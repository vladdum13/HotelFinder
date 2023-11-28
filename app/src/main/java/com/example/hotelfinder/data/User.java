package com.example.hotelfinder.data;

import java.lang.reflect.Array;

public class User {
    private String email;
    private String name;
    private String password;
    private String phone;
    private String[] reservation_ids;

    public User(String email, String name, String password, String phone, String[] reservation_ids) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.reservation_ids = reservation_ids;
    }
}
