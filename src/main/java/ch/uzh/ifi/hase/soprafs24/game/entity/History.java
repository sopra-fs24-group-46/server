package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class History {

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
