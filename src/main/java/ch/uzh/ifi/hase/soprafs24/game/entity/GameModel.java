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
    }

    public GameModel(List<Player> players, Map<String, Score> scores, Map<String, PowerUp> powerUps,
            Map<String, Answer> answers, GameState gameState, RoundState roundState, int currentRound,
            List<Question> questions, Map<String, History> histories) {
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
                new ArrayList<>(questions),
                new HashMap<>(histories));
    }

    public void pushHistory() {
        for (Player player : players) {

            var oldCumulativeScore = cumulativeScores.get(player.getId());
            var currentScore = scores.get(player.getId());
            var newScore = new Score(oldCumulativeScore.getScore() + currentScore.getScore(),
                    oldCumulativeScore.getDistance() + currentScore.getDistance());

            cumulativeScores.put(player.getId(), newScore);

            histories.get(player.getId()).addRound(new Round(
                    currentRound,
                    powerUps.get(player.getId()),
                    getCurrentQuestion(),
                    answers.get(player.getId()),
                    scores.get(player.getId())));
            powerUps.put(player.getId(), null);
            answers.put(player.getId(), null);
            scores.put(player.getId(), null);
        }
    }

    public String addPlayer(String displayName) {
        String playerId = serialPlayerNumber + UUID.randomUUID().toString();
        serialPlayerNumber++;

        players.add(new Player(playerId, displayName));
        // init maps
        scores.put(displayName, null);
        powerUps.put(displayName, null);
        answers.put(displayName, null);
        histories.put(displayName, new History());
        cumulativeScores.put(displayName, new Score(0, 0.0));
        return playerId;
    }

    public void removePlayer(String playerId) {
        Player playerFromList = players.stream().filter(player -> player.getId().equals(playerId)).findFirst().get();
        players.remove(playerFromList);
    }

    // setters----------------------------------------------------------
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
        Question question = questions.get(currentRound);
        if (question == null) {
            throw new IllegalStateException("No question for this round");
        }
        return question;
    }
}
