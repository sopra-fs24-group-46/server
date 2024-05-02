package ch.uzh.ifi.hase.soprafs24.game.entity;

public enum LocationTypes {
    ALPINE_MOUNTAIN("alpiner gipfel"), MOUNTAIN("gipfel"), HILL("huegel"), LAKE("see"), MAIN_HILL("haupthuegel");

    private final String value;

    LocationTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
