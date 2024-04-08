package ch.uzh.ifi.hase.soprafs24.game.entity;

//Immutable
public class GeoLocation {
    private Double lat;
    private Double lng;

    public GeoLocation(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Double getDistanceTo(GeoLocation other) {
        var lat_dif = lat - other.lat;
        var lng_dif = lng - other.lng;
        return Math.sqrt(Math.pow(lat_dif, 2) + Math.pow(lng_dif, 2));
    }
}
