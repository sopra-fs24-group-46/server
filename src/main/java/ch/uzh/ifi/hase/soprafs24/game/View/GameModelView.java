package ch.uzh.ifi.hase.soprafs24.game.View;

import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.Player;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Score;

public interface GameModelView {
    public List<Player> getPlayers();

    public Map<Player, Score> getScores();

    public Map<Player, PowerUp> getPowerUps();

    public Map<Player, Answer> getAnswers();

    public GameState getGameState();

    public RoundState getRoundState();

    public int getCurrentRound();

    public List<Question> getQuestions();

    public Question getCurrentQuestion();
}
