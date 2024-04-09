package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    private String id;
    private String displayName;

    public Player(String id, String displayName) {

    }

    @JsonProperty("playerId")
    public String getId() {
        return id;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

}
