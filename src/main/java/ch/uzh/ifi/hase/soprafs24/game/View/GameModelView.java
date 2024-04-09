package ch.uzh.ifi.hase.soprafs24.game.View;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.History;
import ch.uzh.ifi.hase.soprafs24.game.entity.Player;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Score;

public interface GameModelView {
    @JsonProperty("gameState")
    public GameState getGameState();

    @JsonProperty("currentRound")
    public int getCurrentRound();

    @JsonProperty("roundState")
    public RoundState getRoundState();

    @JsonProperty("currentQuestion")
    public Question getCurrentQuestion();

    @JsonProperty("players")
    public List<Player> getPlayers();

    @JsonProperty("powerUps")
    public Map<String, PowerUp> getPowerUps();

    @JsonProperty("answers")
    public Map<String, Answer> getAnswers();

    @JsonProperty("currentScores")
    public Map<String, Score> getCurrentScores();

    @JsonProperty("cumulativeScores")
    public Map<String, Score> getCumulativeScores();

    @JsonProperty("histories")
    public Map<String, History> getHistories();

    @JsonProperty("questions")
    public List<Question> getQuestions();
}
