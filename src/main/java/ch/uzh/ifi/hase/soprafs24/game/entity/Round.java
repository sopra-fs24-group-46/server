package ch.uzh.ifi.hase.soprafs24.game.entity;

import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;

public class Round {
    private int roundNumber;
    private PowerUp powerUp;
    private Question question;
    private Answer answer;
    private Score score;

    public Round(int roundNumber, PowerUp powerUp, Question question, Answer answer, Score score) {
        this.roundNumber = roundNumber;
        this.powerUp = powerUp;
        this.question = question;
        this.answer = answer;
        this.score = score;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public Question getQuestion() {
        return question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Score getScore() {
        return score;
    }

}
