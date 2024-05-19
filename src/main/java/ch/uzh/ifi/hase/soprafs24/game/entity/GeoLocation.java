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
        if (this.isNull() || other.isNull()) {
            throw new IllegalArgumentException("Both GeoLocations must not be null. to calculate distance");
        }
        // caluclating distance World Geodetic System 1984
        double earthRadius = 6371008.8; // in meters
        double latDistance = Math.toRadians(this.y - other.y);
        double lonDistance = Math.toRadians(this.x - other.x);
        return Math.sqrt(Math.pow(latDistance, 2)
                + Math.pow(lonDistance, 2) * Math.cos(Math.toRadians(other.y)) * Math.cos(Math.toRadians(this.y)))
                * earthRadius;
    }
    
    public boolean isNull() {
        return (y == null || x == null);
    }
}
