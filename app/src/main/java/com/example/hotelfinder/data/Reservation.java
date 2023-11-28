package com.example.hotelfinder.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation {
    private Date end_date;
    private Date start_date;
    private String hotel;
    private String id;
    private int room;
    private String status;

    public Reservation(){}

    public Reservation(Reservation reservation)
    {
        this.end_date = reservation.getEnd_date();
        this.start_date = reservation.getStart_date();
        this.hotel = reservation.getHotel();
        this.id = reservation.getId();
        this.room = reservation.getRoom();
        this.status = reservation.status;
    }


    public Reservation(Date end_date, Date start_date, String hotel, String id, int room, String status) {
        this.end_date = end_date;
        this.start_date = start_date;
        this.hotel = hotel;
        this.id = id;
        this.room = room;
        this.status = status;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public String getHotel() {
        return hotel;
    }

    public String getId() {
        return id;
    }

    public int getRoom() {
        return room;
    }

    public String getStatus() {
        return status;
    }

    public String getPeriod()
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        return df.format(start_date) + "-" + df.format(end_date);
    }
}
