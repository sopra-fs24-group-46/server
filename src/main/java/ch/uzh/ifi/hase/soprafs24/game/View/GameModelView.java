package ch.uzh.ifi.hase.soprafs24.game.View;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.History;
import ch.uzh.ifi.hase.soprafs24.game.entity.Player;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Score;

@JsonPropertyOrder({
        "gameState",
        "roundState",
        "currentRound",
        "host",
        "players",
        "currentQuestion",
        "answers",
        "currentScores",
        "cumulativeScores",
        "powerUps",
        "questions",
        "histories",
})
public interface GameModelView {
    @JsonProperty("players")
    public List<Player> getPlayers();

    @JsonProperty("gameState")
    public GameState getGameState();

    @JsonProperty("currentRound")
    public int getCurrentRound();

    @JsonProperty("roundState")
    public RoundState getRoundState();

    @JsonProperty("currentQuestion")
    public Question getCurrentQuestion();

    @JsonProperty("answers")
    public Map<String, Answer> getAnswers();

    @JsonProperty("currentScores")
    public Map<String, Score> getCurrentScores();

    @JsonProperty("cumulativeScores")
    public Map<String, Score> getCumulativeScores();

    @JsonProperty("powerUps")
    public Map<String, PowerUp> getPowerUps();

    @JsonProperty("questions")
    public List<Question> getQuestions();

    @JsonProperty("histories")
    public Map<String, History> getHistories();

    @JsonProperty("host")
    public Player getHostPlayer();
}
