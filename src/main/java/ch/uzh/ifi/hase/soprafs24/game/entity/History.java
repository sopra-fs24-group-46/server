package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Immutable;

@Entity
public class History implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    private List<Round> rounds;

    public History() {
        rounds = new ArrayList<>();
    }

    public void addRound(Round round) {
        rounds.add(round);
    }

    // returns shallow copy
    @JsonProperty("rounds")
    public List<Round> getRounds() {
        return new ArrayList<>(rounds);
    }

}
