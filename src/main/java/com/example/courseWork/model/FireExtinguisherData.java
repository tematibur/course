package com.example.courseWork.model;

import java.time.LocalDate;

public class FireExtinguisherData {

    private Long id;
    private String location;
    private LocalDate expirationDate;

    public FireExtinguisherData() {}

    public FireExtinguisherData(String location, LocalDate expirationDate) {
        this.location = location;
        this.expirationDate = expirationDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}