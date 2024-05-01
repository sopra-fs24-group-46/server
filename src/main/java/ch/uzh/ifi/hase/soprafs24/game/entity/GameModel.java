package ch.uzh.ifi.hase.soprafs24.game.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;

public class GameModel implements GameModelView {

    // for each player:
    private List<Player> players;
    // immutables are stored in history
    private Map<String, Score> scores;
    private Map<String, PowerUp> powerUps; // this allows for only one PowerUp at a time
    private Map<String, Answer> answers;
    // mutable
    private Map<String, History> histories;
    private Map<String, Score> cumulativeScores;

    // game state
    private GameState gameState;
    private RoundState roundState;
    private int currentRound;

    // general
    private List<Question> questions; // one question for each round. Use current round as Index to get current
                                      // question.
    private int serialPlayerNumber; // used to assign player numbers
    private Player hostPlayer;

    // helpers
    private Map<String, List<PowerUp>> usedPowerUps;

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
        this.histories = new HashMap<>();
        this.cumulativeScores = new HashMap<>();
        this.usedPowerUps = new HashMap<>();
        this.questions = new ArrayList<>();
    }

    public void initRound() {
        for (Player player : players) {
            powerUps.put(player.getId(), null);
            answers.put(player.getId(), null);
            scores.put(player.getId(), null);
        }
    }

    public void pushHistory() {// responsible adding scores and managing the history
        for (Player player : players) {

            var oldCumulativeScore = cumulativeScores.get(player.getId());
            if (oldCumulativeScore == null) {
                oldCumulativeScore = new Score(0, 0.0);
            }
            var currentScore = scores.get(player.getId());
            var newScore = oldCumulativeScore.add(currentScore);

            cumulativeScores.put(player.getId(), newScore);

            var powerUp = powerUps.get(player.getId());
            var answer = answers.get(player.getId());
            var score = scores.get(player.getId());

            var round = new Round(
                    currentRound,
                    getCurrentQuestion(),
                    powerUp,
                    answer,
                    score);

            var history = histories.get(player.getId());
            history.addRound(round);
        }
    }

    public String addPlayer(String displayName) {
        String playerId = "player_id_" + serialPlayerNumber + "_" + UUID.randomUUID().toString().substring(0, 4);
        serialPlayerNumber++;

        players.add(new Player(playerId, displayName));
        // init maps
        scores.put(playerId, null);
        powerUps.put(playerId, null);
        answers.put(playerId, null);
        histories.put(playerId, new History());
        cumulativeScores.put(playerId, new Score(0, 0.0));
        usedPowerUps.put(playerId, new ArrayList<>());
        return playerId;
    }

    public void removePlayer(String playerId) {
        Player playerFromList = players.stream().filter(player -> player.getId().equals(playerId)).findFirst().get();
        scores.remove(playerId);
        powerUps.remove(playerId);
        answers.remove(playerId);
        histories.remove(playerId);
        cumulativeScores.remove(playerId);
        players.remove(playerFromList);
    }

    // only one at the time and only once a powerup can be used. Once thi function
    // is called the powerup is used. Idea to refactor instead of used powerups work
    // with powerups to be used.
    public void usePowerUp(String playerId, PowerUp powerUp) {
        // check if a powerup is already in use
        if (powerUps.get(playerId) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Player already has a powerup. Powerup: " + powerUps.get(playerId));
        }
        // check if powerup is already used
        if (usedPowerUps.get(playerId).contains(powerUp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Powerup: " + powerUp + " is already used. Used powerups: " + usedPowerUps.get(playerId));
        }
        powerUps.put(playerId, powerUp);
        usedPowerUps.get(playerId).add(powerUp);
    }

    // setters----------------------------------------------------------

    public void setHostPlayer(String playerId) {
        hostPlayer = players.stream().filter(player -> player.getId().equals(playerId)).findFirst().get();
    }

    public void setScore(String playerId, int score, Double distance) {
        scores.put(playerId, new Score(score, distance));
    }

    public void setPowerUp(String playerId, PowerUp powerUp) {
        powerUps.put(playerId, powerUp);
    }

    public void setAnswer(String playerId, Answer answer) {
        answers.put(playerId, answer);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setRoundState(RoundState roundState) {
        this.roundState = roundState;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    // getters----------------------------------------------------------
    public List<String> getPlayersIds() {
        return players.stream().map(player -> player.getId()).collect(Collectors.toList());
    }

    public Player getHostPlayer() {
        return hostPlayer;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public Map<String, Score> getCurrentScores() {
        return scores;
    }

    public Map<String, Score> getCumulativeScores() {
        return cumulativeScores;
    }

    public Map<String, PowerUp> getPowerUps() {
        return powerUps;
    }

    public Map<String, Answer> getAnswers() {
        return answers;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public Question getCurrentQuestion() {
        if (currentRound > questions.size() || currentRound < 1) {
            return null;
        }
        Question question = questions.get(currentRound - 1);
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No question for this round");
        }
        return question;
    }

    public Map<String, History> getHistories() {
        return histories;
    }

    public Map<String, List<PowerUp>> getUsedPowerUps() {
        return usedPowerUps;
    }
}