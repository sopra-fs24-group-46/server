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
    public Integer getScore() {
        return score;
    }

    @JsonProperty("distance")
    public Double getDistance() {
        return distance;
    }

    public Score add(Score score) {
        if (score == null) {
            return this;
        }
        var other_score = score.score;
        var other_distance = score.distance;
        if (this.score == null) {
            this.score = 0;
        }
        if (this.distance == null) {
            this.distance = 0.0;
        }
        if (score.score == null) {
            other_score = 0;
        }
        if (score.distance == null) {
            other_distance = 0.0;
        }
        return new Score(this.score + other_score, this.distance + other_distance);
    }
}
