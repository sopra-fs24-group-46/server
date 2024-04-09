package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;

public class GameModel implements GameModelView {

    // for each player:
    private List<Player> players;
    private Map<String, Score> scores;
    private Map<String, PowerUp> powerUps; // this allows for only one PowerUp at a time
    private Map<String, Answer> answers;

    // game state
    private GameState gameState;
    private RoundState roundState;
    private int currentRound;

    // general
    private List<Question> questions; // one question for each round. Use current round as Index to get current
                                      // question.
    private int serialPlayerNumber; // used to assign player numbers

    public GameModel() {

        // set default values
        gameState = GameState.SETUP;
        roundState = RoundState.QUESTION;
        currentRound = 0;
        serialPlayerNumber = 0;

        // init lists
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
        this.powerUps = new HashMap<>();
        this.answers = new HashMap<>();

    }

    public GameModel(List<Player> players, Map<String, Score> scores, Map<String, PowerUp> powerUps,
            Map<String, Answer> answers, GameState gameState, RoundState roundState, int currentRound,
            List<Question> questions) {
        // set default values
        gameState = GameState.SETUP;
        roundState = RoundState.QUESTION;
        currentRound = 0;
    }

    public GameModelView getGameModelView() {
        // this creates shallow copies of Lists and Maps
        // and returns a GameModelView
        // this helps with immutability
        return new GameModel(
                new ArrayList<>(players),
                new HashMap<>(scores),
                new HashMap<>(powerUps),
                new HashMap<>(answers),
                gameState,
                roundState,
                currentRound,
                new ArrayList<>(questions));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<String, Score> getScores() {
        return scores;
    }

    public void updateScore(String playerId, int score, Double distance) {
        scores.get(playerId).addScore(score, distance);
    }

    public Map<String, PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUp(String playerId, PowerUp powerUp) {
        powerUps.put(playerId, powerUp);
    }

    public Map<String, Answer> getAnswers() {
        return answers;
    }

    public void setAnswer(String playerId, Answer answer) {
        answers.put(playerId, answer);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Question getCurrentQuestion() {
        Question question = questions.get(currentRound);
        if (question == null) {
            throw new IllegalStateException("No question for this round");
        }
        return question;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String addPlayer(String displayName) {
        if (gameState != GameState.LOBBY) {
            throw new IllegalStateException(
                    "You can only join games which are in Lobby state. Current state: " + gameState);
        }
        String playerId = serialPlayerNumber + UUID.randomUUID().toString();
        serialPlayerNumber++;

        players.add(new Player(playerId, displayName));
        // init maps
        scores.put(displayName, new Score());
        powerUps.put(displayName, null);
        answers.put(displayName, null);
        return playerId;
    }

    public void removePlayer(String playerId) {
        Player playerFromList = players.stream().filter(player -> player.getId().equals(playerId)).findFirst().get();
        players.remove(playerFromList);
    }

}
