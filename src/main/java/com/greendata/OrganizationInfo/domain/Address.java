package com.greendata.OrganizationInfo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String index;
    private String regionType;
    private String region;
    private String districtType;
    private String district;
    private String settlementType;
    private String settlement;
    private String streetType;
    private String street;
    private String house;
    private String building;
    private String room;

    @Override
    public String toString() {
        String addressString = "";
        if (index != null) addressString += index;
        if (region != null) addressString += ", " + regionType + " " + region;
        if (district != null) addressString += ", " + districtType + " " + district;
        if (settlement != null) addressString += ", " + settlementType + " " + settlement;
        if (street != null) addressString += ", " + streetType + " " + street;
        if (house != null) addressString += ", " + house;
        if (building != null && building.length() > 0) addressString += ", " + building;
        if (room != null && room.length() > 0) addressString += ", " + room;
        
        return addressString;
    }
}
