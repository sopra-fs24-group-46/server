//This class provides static functions for the game logic
//Maybe exception if game pace thing
package ch.uzh.ifi.hase.soprafs24.game;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.APIService;

//use a scheduler
public class GameEngine {

    static private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5); // Adjust the number of
                                                                                             // threads as

    static public void scheduleGame(GameModel gameModel, Settings settings) {
        if (gameModel.getGameState() == GameState.SETUP) {
            initGame(gameModel, settings);// set Game into Lobby mode. loading questions...
        }
        if (gameModel.getGameState() == GameState.LOBBY) {
            loadGame(gameModel, settings);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only games in Lobby or Setup State can be started. Current state: " + gameModel.getGameState());
        }
        // ---------------------------------------------------------------------
        var numberOfRounds = settings.getRounds();
        for (int i = 1; i <= numberOfRounds; i++) {
            gameModel.setCurrentRound(i);
            scheduleRound(gameModel, settings);
        }
        // ---------------------------------------------------------------------
        scheduler.schedule(() -> endGame(gameModel, settings), settings.getTotalTime(),
                java.util.concurrent.TimeUnit.SECONDS);
    }

    static public void scheduleRound(GameModel gameModel, Settings settings) {
        int scheduleTime = gameModel.getCurrentRound() * settings.getRoundTime();// initial delay
                                                                                 //
        scheduler.schedule(() -> nextRoundState(gameModel, RoundState.QUESTION), scheduleTime,
                java.util.concurrent.TimeUnit.SECONDS);
        scheduleTime = +settings.getQuestionTime();

        scheduler.schedule(() -> nextRoundState(gameModel, RoundState.GUESSING), scheduleTime,
                java.util.concurrent.TimeUnit.SECONDS);
        scheduleTime = +settings.getGuessingTime();

        scheduler.schedule(() -> nextRoundState(gameModel, RoundState.MAP_REVEAL), scheduleTime,
                java.util.concurrent.TimeUnit.SECONDS);
        scheduleTime = +settings.getMapRevealTime();

        scheduler.schedule(() -> nextRoundState(gameModel, RoundState.LEADERBOARD), scheduleTime,
                java.util.concurrent.TimeUnit.SECONDS);
    }

    public static void addAnswer(GameModel gameModel, Answer answer, String playerId) {
        if (gameModel.getRoundState() != RoundState.GUESSING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Answers are only allowed during guessing. Current state: " + gameModel.getRoundState());
        }
        gameModel.setAnswer(playerId, answer);
    }

    static public void loadGame(GameModel gameModel, Settings settings) {

        if (gameModel.getGameState() != GameState.LOBBY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only games in Lobby State can be started. Current state: " + gameModel.getGameState());
        }
        gameModel.setGameState(GameState.PLAYING);
        gameModel.setCurrentRound(1);
    }

    static public void endGame(GameModel gameModel, Settings settings) {
        if (gameModel.getGameState() != GameState.PLAYING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't end the game. Something went wrong while Playing the game. Current state: "
                            + gameModel.getGameState() + " but should be PLAYING");
        }
        gameModel.setGameState(GameState.ENDED);
    }

    static public void nextRoundState(GameModel gameModel, RoundState roundState) {
        RoundState previousRoundState = gameModel.getRoundState();
        // force stepping through states
        switch (roundState) {
            case QUESTION:
                gameModel.setRoundState(RoundState.QUESTION);
                gameModel.initRound();
                break;
            case GUESSING:
                if (previousRoundState != RoundState.QUESTION) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Round state is not QUESTION");
                }
                gameModel.setRoundState(RoundState.GUESSING);
                break;
            case MAP_REVEAL:
                if (previousRoundState != RoundState.GUESSING) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Round state is not GUESSING");
                }
                gameModel.setRoundState(RoundState.MAP_REVEAL);
                evaluateAnswers(gameModel);// evaluate answers
                break;
            case LEADERBOARD:
                if (previousRoundState != RoundState.MAP_REVEAL) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Round state is not MAP_REVEAL");
                }
                gameModel.setRoundState(RoundState.LEADERBOARD);
                break;
        }
    }

    private static void evaluateAnswers(GameModel gameModel) {
        // iterates over map of answers
        for (Map.Entry<String, Answer> entry : gameModel.getAnswers().entrySet()) {
            String playerId = entry.getKey();
            Answer answer = entry.getValue();

            var question = gameModel.getCurrentQuestion();
            Double distance;
            int score;

            if (answer == null) {// no answer submitted
                distance = null;
                score = 0;
            } else {
                distance = question.getLocation().getDistanceTo(answer.getLocation());
                score = (int) (1000 / Math.pow(distance / 10000, 2));
            }

            gameModel.setScore(playerId, score, distance);
        }

        gameModel.pushHistory(); // this seals the deal. No more changes to round.
        // cumulative scores are
    }

    public static void initGame(GameModel gameModel, Settings settings) {
        if (gameModel.getGameState() != GameState.SETUP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game should only be initialized once. Current state: " + gameModel.getGameState() + " expected: "
                            + GameState.SETUP);
        }
        var roundNumber = settings.getRounds();
        var questions = APIService.getQuestions(roundNumber);
        if (questions.size() < roundNumber) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough questions (" + questions.size() + "). Consider lowering the round number ("
                            + roundNumber + ") or increasing region or removing some filter");
        }
        gameModel.setQuestions(questions);
        gameModel.setGameState(GameState.LOBBY);
    }

    public void deleteGame(GameModel gameModel, Settings settings) {
        gameModel.setGameState(GameState.CLOSED);
        // add cleanup here
    }
}
