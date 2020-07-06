package com.tomato.market.location;

import lombok.Data;

@Data
public class LocationForm {

    private String locationName;

    public String getCityName() {
        return locationName.substring(0, locationName.indexOf(" "));
    }

    public String getProvinceName() {
        return locationName.substring(locationName.indexOf(" ")+1, locationName.lastIndexOf(" "));
    }

    public String getUnitName() {
        return locationName.substring(locationName.lastIndexOf(" ")+1);
    }

    public Location getLocation() {
        return Location.builder()
                .city(this.getCityName())
                .province(this.getProvinceName())
                .unit(this.getUnitName())
                .build();
    }
}

