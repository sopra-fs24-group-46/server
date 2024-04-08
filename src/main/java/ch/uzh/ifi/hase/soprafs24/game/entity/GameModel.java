package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;

public class GameModel implements GameModelView {

    // for each player:
    private List<Player> players;
    private Map<Player, Score> scores;
    private Map<Player, PowerUp> powerUps; // this allows for only one PowerUp at a time
    private Map<Player, Answer> answers;

    // game state
    private GameState gameState;
    private RoundState roundState;
    private int currentRound;

    // general
    private List<Question> questions; // one question for each round. Use current round as Index to get current
                                      // question.

    public GameModel() {

        // set default values
        gameState = GameState.SETUP;
        roundState = RoundState.QUESTION;
        currentRound = 0;

        // init lists
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
        this.powerUps = new HashMap<>();
        this.answers = new HashMap<>();

    }

    public GameModel(List<Player> players, Map<Player, Score> scores, Map<Player, PowerUp> powerUps,
            Map<Player, Answer> answers, GameState gameState, RoundState roundState, int currentRound,
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

    public Map<Player, Score> getScores() {
        return scores;
    }

    public void updateScore(Player player, int score, Double distance) {
        Player playerFromList = playerFromList(player);
        scores.get(playerFromList).addScore(score, distance);
    }

    public Map<Player, PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUp(Player player, PowerUp powerUp) {
        Player playerFromList = playerFromList(player);
        powerUps.put(playerFromList, powerUp);
    }

    public Map<Player, Answer> getAnswers() {
        return answers;
    }

    public void setAnswer(Player player, Answer answer) {
        Player playerFromList = playerFromList(player);
        answers.put(playerFromList, answer);
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

    public void addPlayer(Player player) {
        if (players.stream().anyMatch(p -> p.getDisplayName() == player.getDisplayName())) {
            throw new IllegalStateException("Player with this name already exists");
        }
        if (players.stream().anyMatch(p -> p.getToken().equals(player.getToken()))) {
            throw new IllegalStateException("Player with this token already exists");
        }
        if (gameState != GameState.LOBBY) {
            throw new IllegalStateException(
                    "You can only join games which are in Lobby state. Current state: " + gameState);
        }
        players.add(player);
        // init maps
        scores.put(player, new Score());
        powerUps.put(player, null);
        answers.put(player, null);
    }

    public void removePlayer(Player player) {
        Player playerFromList = playerFromList(player);
        players.remove(playerFromList);
    }

    private Player playerFromList(Player player) {
        return players.stream().filter(p -> p.getToken().equals(player.getToken())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Player with this token not found is not in the game"));
    }
}
