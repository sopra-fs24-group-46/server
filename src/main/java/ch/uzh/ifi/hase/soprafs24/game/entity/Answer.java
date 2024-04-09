package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

//Immutable
public class Answer {

    private GeoLocation location;

    public Answer(GeoLocation location) {
        this.location = location;
    }

    @JsonProperty("location")
    public GeoLocation getLocation() {
        return location;
    }
}
