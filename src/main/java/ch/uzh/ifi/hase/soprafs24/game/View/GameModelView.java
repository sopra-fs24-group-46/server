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

    @JsonProperty("players")
    public List<Player> getPlayers();

    @JsonProperty("powerUps")
    public Map<String, PowerUp> getPowerUps();

    @JsonProperty("answers")
    public Map<String, Answer> getAnswers();

    @JsonProperty("gameState")
    public GameState getGameState();

    @JsonProperty("roundState")
    public RoundState getRoundState();

    @JsonProperty("currentRound")
    public int getCurrentRound();

    @JsonProperty("questions")
    public List<Question> getQuestions();

    @JsonProperty("currentQuestion")
    public Question getCurrentQuestion();

    @JsonProperty("currentScores")
    public Map<String, Score> getCurrentScores();

    @JsonProperty("cumulativeScores")
    public Map<String, Score> getCumulativeScores();

    @JsonProperty("histories")
    public Map<String, History> getHistories();
}
