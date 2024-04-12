package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

//Immutable
public class Question {
    private String location_name;
    private GeoLocation location;

    public Question(String location_name,
            GeoLocation location) {
        this.location_name = location_name;
        this.location = location;
    }

    @JsonProperty("location_name")
    public String getLocation_name() {
        return location_name;
    }

    @JsonProperty("location")
    public GeoLocation getLocation() {
        return location;
    }
}
