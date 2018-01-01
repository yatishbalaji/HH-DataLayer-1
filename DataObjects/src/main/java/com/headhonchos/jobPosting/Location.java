package com.headhonchos.jobPosting;

/**
 * Created by ishu on 27/3/14.
 */
public class Location {

    String Id;
    String cityName;
    String countryId;
    double latitude;
    double longitude;

    public Location(String id) {
        this.Id=id;
    }

    public String getId() {
        return this.Id;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName=cityName;
    }

    public String getCountryId() {
        return this.countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId=countryId;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude =latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "Id='" + Id + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countryId='" + countryId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
