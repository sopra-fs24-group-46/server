package ch.uzh.ifi.hase.soprafs24.game.entity;

//Immutable
public class Question {
    private String location_name;
    private GeoLocation location;

    public Question(String location_name, GeoLocation location) {
        this.location_name = location_name;
        this.location = location;
    }

    public String getLocation_name() {
        return location_name;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
