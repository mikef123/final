package com.valquiria.myapplication;

import java.util.Date;

/**
 * Created by murquijo on 2017-10-24.
 */

public class    Localizacion {
    String name;
    Double latitude;
    Double longitude;




    public Localizacion() {
       }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


}
