package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

//Immutable
public class GeoLocation {
    private final Double x; // latitude
    private final Double y; // longitude

    public GeoLocation(Double lat, Double lng) {
        this.x = lat;
        this.y = lng;
    }

    @JsonProperty("x")
    public Double getX() {
        return x;
    }

    @JsonProperty("y")
    public Double getY() {
        return y;
    }

    public Double getDistanceTo(GeoLocation other) {
        var lat_dif = x - other.x;
        var lng_dif = y - other.y;
        return Math.sqrt(Math.pow(lat_dif, 2) + Math.pow(lng_dif, 2));
    }
}
