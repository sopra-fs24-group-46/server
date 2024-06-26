package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    private String id;
    private String displayName;

    public Player(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @JsonProperty("playerId")
    public String getId() {
        return id;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

}
