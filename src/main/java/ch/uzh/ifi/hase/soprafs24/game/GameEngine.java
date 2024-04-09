//This class provides static functions for the game logic
//Maybe exception if game pace thing
package ch.uzh.ifi.hase.soprafs24.game;

import java.util.Map;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.APIService;

public class GameEngine {

    public void startGame(GameModel gameModel, Settings settings) {
        // this starts the game and automatically goes to the next round after a certain
        // time
        if (gameModel.getGameState() != GameState.LOBBY) {
            throw new IllegalStateException(
                    "Only games in Lobby State can be started. Current state: " + gameModel.getGameState());
        }
        gameModel.setGameState(GameState.PLAYING);
        var numberOfRounds = settings.getRounds();

        // ---------------------------------------------------------------------
        for (int i = 0; i < numberOfRounds; i++) {
            gameModel.setCurrentRound(i);
            startRound(gameModel, settings);
            waitTime(5000);
        }
        // ---------------------------------------------------------------------

        if (gameModel.getGameState() != GameState.PLAYING) {
            throw new IllegalStateException(
                    "Can't end the game. Something went wrong while Playing the game. Current state: "
                            + gameModel.getGameState() + " but should be PLAYING");
        }
        gameModel.setGameState(GameState.ENDED);
    }

    public void startRound(GameModel gameModel, Settings settings) {
        // this starts the game and automatically goes to the next round state after a
        // certain time
        gameModel.setRoundState(RoundState.QUESTION);

        waitTime(5000); // 5 seconds
        // -------------------------------------------
        gameModel.setRoundState(RoundState.GUESSING);

        waitTime(settings.getGuessingTimePerRound() * 1000);
        // -------------------------------------------
        gameModel.setRoundState(RoundState.MAP_REVEAL);
        evaluateAnswers(gameModel);

        waitTime(5000);
        // -------------------------------------------
        gameModel.setRoundState(RoundState.LEADERBOARD);
    }

    public static void addAnswer(GameModel gameModel, Answer answer, String playerId) {
        if (gameModel.getRoundState() != RoundState.GUESSING) {
            throw new IllegalStateException(
                    "Answers are only allowed during guessing. Current state: " + gameModel.getRoundState());
        }
        gameModel.setAnswer(playerId, answer);
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
                score = (int) (1000 / Math.pow(distance, 2));
            }

            gameModel.setScore(playerId, score, distance);
            gameModel.pushHistory(); // this seals the deal. No more changes to round.
                                     // cumulative scores are
        }
    }

    public static void initGame(GameModel gameModel, Settings settings) {
        var roundNumber = settings.getRounds();
        var questions = APIService.getQuestions(roundNumber);
        if (questions.size() < roundNumber) {
            throw new IllegalArgumentException(
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

    private void waitTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
