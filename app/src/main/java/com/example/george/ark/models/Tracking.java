package com.example.george.ark.models;

/**
 * Created by George on 12.02.2018.
 */

public class Tracking {
    private String email;
    private String UUID;
    private String lat;
    private String lng;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Tracking() {

    }

    public Tracking(String email, String UUID, String lat, String lng) {

        this.email = email;
        this.UUID = UUID;
        this.lat = lat;
        this.lng = lng;
    }
}
