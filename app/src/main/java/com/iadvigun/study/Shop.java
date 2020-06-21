package com.iadvigun.study;

public class Shop {
    private Long id;
    private String name;
    private float latitude;
    private float longitude;

    public Shop(String name, float latitude, float longitude, int areaSize) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaSize = areaSize;
    }

    private int areaSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
    }


}
