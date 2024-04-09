package ch.uzh.ifi.hase.soprafs24.game.entity;

import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("roundNumber")
    public int getRoundNumber() {
        return roundNumber;
    }

    @JsonProperty("powerUp")
    public PowerUp getPowerUp() {
        return powerUp;
    }

    @JsonProperty("question")
    public Question getQuestion() {
        return question;
    }

    @JsonProperty("answer")
    public Answer getAnswer() {
        return answer;
    }

    @JsonProperty("score")
    public Score getScore() {
        return score;
    }
}