package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {
    private Integer score;
    private Double distance;

    public Score(int score, Double distance) {
        this.score = score;
        this.distance = distance;
    }

    @JsonProperty("score")
    public int getScore() {
        if (score == null) {
            return 0;
        }
        return score;
    }

    @JsonProperty("distance")
    public double getDistance() {
        if (score == null) {
            return 0;
        }
        return distance;
    }
}
