package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

//Immutable
@Entity(name = "question")
@Immutable
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;
    @Column
    private String location_name;
    @Column
    private GeoLocation location;

    // Hibernate needs default Constructor
    private Question() {
    }

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
