package ch.uzh.ifi.hase.soprafs24.game.entity;

import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Immutable
public class Round implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private int roundNumber;
    private PowerUp powerUp;
    @ManyToOne
    private Question question;
    @ManyToOne
    private Answer answer;
    private Score score;

    private Round() {
    }

    public Round(int roundNumber, Question question, PowerUp powerUp, Answer answer, Score score) {
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