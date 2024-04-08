package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.List;

public class Score {
    private HelperScore sum;
    private List<HelperScore> history;

    public Score() {
        sum = null;
        history = new ArrayList<>();
    }

    public int getCumulativeScore() {
        if (sum == null) {
            return 0;
        }
        return sum.score;
    }

    public double getCumulativeDistance() {
        if (sum == null) {
            return 0;
        }
        return sum.distance;
    }

    public int getCurrentScore() {
        if (history.size() == 0) {
            return 0;
        }
        return history.get(history.size() - 1).score;
    }

    public double getCurrentDistance() {
        if (history.size() == 0) {
            return 0;
        }
        return history.get(history.size() - 1).distance;
    }

    public void addScore(int score, Double distance) {
        if (sum == null) {
            sum = new HelperScore();
        }
        sum.score += score;
        if (distance != null) {
            sum.distance += distance;
        }
        history.add(new HelperScore(score, distance));
    }

    // for the moment an ugly function. What would be a better return type?
    public List<Double[]> getHistory() {
        return history.stream()
                .map(h -> new Double[] { (double) h.score, h.distance })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

class HelperScore {
    public int score;
    public Double distance;

    public HelperScore() {
    }

    public HelperScore(int score, Double distance) {
        this.score = score;
        this.distance = distance;
    }
}
