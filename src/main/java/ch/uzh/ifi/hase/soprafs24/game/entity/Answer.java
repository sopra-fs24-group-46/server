package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

//Immutable
@Entity
@Immutable
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private GeoLocation location;

    private Answer() {
    }

    public Answer(GeoLocation location) {
        this.location = location;
    }

    @JsonProperty("location")
    public GeoLocation getLocation() {
        return location;
    }
}
