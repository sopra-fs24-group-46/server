package ch.uzh.ifi.hase.soprafs24.game.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {
    private Integer score;
    private Double distance;

    public Score(Integer score, Double distance) {
        this.score = score;
        this.distance = distance;
    }

    @JsonProperty("score")
    public Integer getScore() {
        return score;
    }

    @JsonProperty("distance")
    public Double getDistance() {
        return distance;
    }

    public Score add(Score other) {
        if (other == null) {
            return this;
        }
        var otherScore = other.score;
        var otherDistance = other.distance;
        if (this.score == null) {
            this.score = 0;
        }
        if (this.distance == null) {
            this.distance = 0.0;
        }
        if (other.score == null) {
            otherScore = 0;
        }
        if (other.distance == null) {
            otherDistance = 0.0;
        }
        return new Score(this.score + otherScore, this.distance + otherDistance);
    }
}
