package ch.uzh.ifi.hase.soprafs24.game.entity;

//Immutable
public class Answer {
    private GeoLocation location;

    public Answer(GeoLocation location) {
        this.location = location;
    }

    public GeoLocation getLocation() {
        return location;
    }
}
