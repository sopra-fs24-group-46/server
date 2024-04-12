package ch.uzh.ifi.hase.soprafs24.game;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.user.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;

public class GameIntegrationTest {

    @Test
    void game1() {
        // game with Host and one player
        TestGame game = new TestGame(new User(), "Game1");
        String player1 = game.getHostPlayerId();
        game.storeState("Created");
        game.updateSettings(new Settings(2, 2, 2));
        game.storeState("SettingsUpdated");
        // lobby --------------------------
        game.openLobby();
        game.storeState("LobbyOpen");
        String player2 = game.joinGame("player2");
        game.storeState("PlayerJoined");

        // start game --------------------
        game.gameEngine.loadGame(game.gameModel, game.settings);
        // round one
        game.gameModel.setCurrentRound(1);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.storeState("Round1Started");
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.storeState("Round1Guessed");
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round1Ended");
        // round two
        game.gameModel.setCurrentRound(2);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.storeState("Round2Started");
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.storeState("Round2Guessed");
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round2Ended");
        // end game
        game.gameEngine.endGame(game.gameModel, game.settings);
        game.storeState("GameEnded");
    }

    @Test
    void game2() {
        // game with Host and 3 players
        // too many players will join
        // some players will be kicked
        // some players won't hand in answers in time

        TestGame game = new TestGame(new User(), "Game2");
        String player1 = game.getHostPlayerId();
        game.storeState("Created");
        var settings = new Settings(4, 4, 2);
        game.updateSettings(settings);
        game.storeState("SettingsUpdated");
        // lobby --------------------------
        game.openLobby();
        game.storeState("LobbyOpen");
        String player2 = game.joinGame("player2");
        String player3 = game.joinGame("player3");
        String playerToKick = game.joinGame("someone");
        // next line should throw an exception
        assertThrows(IllegalStateException.class, () -> game.joinGame("player5"));
        game.leaveGame(playerToKick);
        String player4 = game.joinGame("player4");

        game.storeState("PlayerJoined");

        // start game --------------------
        game.gameEngine.loadGame(game.gameModel, game.settings);
        assertThrows(IllegalStateException.class, () -> game.joinGame("late guy"));
        // round one
        game.gameModel.setCurrentRound(1);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);

        assertThrows(IllegalStateException.class,
                () -> game.guess(playerToKick, new Answer(new GeoLocation(10.0, 10.0))));
        assertThrows(IllegalStateException.class,
                () -> game.guess(player1, new Answer(new GeoLocation(10.0, 10.0))));

        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);

        assertThrows(IllegalStateException.class,
                () -> game.guess(player1, new Answer(new GeoLocation(10.0, 10.0))));

        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round1Ended");
        // round two
        game.gameModel.setCurrentRound(2);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(8000.0, 8000.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round2Ended");

        // round three
        game.gameModel.setCurrentRound(3);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(608000.0, 108000.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round3Ended");

        // round four
        game.gameModel.setCurrentRound(4);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(8000.0, 8000.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round4Ended");
        // end game
        game.gameEngine.endGame(game.gameModel, game.settings);
        game.storeState("GameEnded");
    }
}

/**
 * TestGame has helper functions for testing
 */
class TestGame extends Game {
    private String gameName;
    private int storedStateNumber = 0;
    private final static boolean writeJSON = false; // set to true to write JSON files

    public TestGame(User hostPlayer, String gameName) {
        super(hostPlayer);
        this.gameName = gameName;
    }

    public void storeState(String state) {
        String content = toJsonString((GameModelView) this.getGameModelView());
        writeStringToFile(content, gameName + "_" + storedStateNumber++ + "_" + state);
    }

    public void printState() {
        String content = toJsonString((GameModelView) this.getGameModelView());
        System.out.println(content);
    }

    // local function for testing
    private static String toJsonString(GameModelView gameModelView) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not convert GameModelView to JSON string", e);
        }
    }

    private static void writeStringToFile(String content, String fileName) {
        if (!writeJSON) {
            return;
        }
        try (FileWriter fileWriter = new FileWriter(
                "src/main/resources/GameModelViews/" + fileName
                        + ".json")) {
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
