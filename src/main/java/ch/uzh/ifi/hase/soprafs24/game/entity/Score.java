package ch.uzh.ifi.hase.soprafs24.game.entity;

public class Score {
    private Integer score;
    private Double distance;

    public Score(int score, Double distance) {
        this.score = score;
        this.distance = distance;
    }

    public int getScore() {
        if (score == null) {
            return 0;
        }
        return score;
    }

    public double getDistance() {
        if (score == null) {
            return 0;
        }
        return distance;
    }
}