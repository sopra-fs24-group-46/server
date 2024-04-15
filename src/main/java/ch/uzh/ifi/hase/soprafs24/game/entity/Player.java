package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Immutable
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long persistent_id;

    private String id;
    private String displayName;

    private Player() {
    }

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
