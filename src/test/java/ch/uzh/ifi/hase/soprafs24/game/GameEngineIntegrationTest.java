package ch.uzh.ifi.hase.soprafs24.game;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GameEngineIntegrationTest {

    @Test
    void game1() {
        // game with Host and one player
        var host = new User();
        host.setUsername("host_player_name");
        TestGame game = new TestGame(host, "Game1");
        String player1 = game.getHostPlayerId();
        game.storeState("Created");
        var settings = new Settings(2, 2, 2);
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        game.updateSettings(settings);
        game.storeState("SettingsUpdated");
        // lobby --------------------------
        game.openLobby();
        game.storeState("LobbyOpen");
        String player2 = game.joinGame("player2");
        game.storeState("PlayerJoined");

        // start game --------------------
        GameEngine.loadGame(game.gameModel, game.settings);
        // round one
        game.gameModel.setCurrentRound(1);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.storeState("Round1Started");
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.storeState("Round1Guessed");
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round1Ended");
        // round two
        game.gameModel.setCurrentRound(2);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.storeState("Round2Started");
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.storeState("Round2Guessed");
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round2Ended");
        // end game
        GameEngine.endGame(game.gameModel, game.settings);
        game.storeState("GameEnded");
    }

    @Test
    void game2() {
        // game with Host and 3 players
        // too many players will join
        // some players will be kicked
        // some players won't hand in answers in time
        var host = new User();
        host.setUsername("host_player_name");
        TestGame game = new TestGame(host, "Game2");
        String player1 = game.getHostPlayerId();
        game.storeState("Created");
        var settings = new Settings(4, 4, 2);
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        game.updateSettings(settings);
        game.storeState("SettingsUpdated");
        // lobby --------------------------
        game.openLobby();
        game.storeState("LobbyOpen");
        String player2 = game.joinGame("player2");
        String player3 = game.joinGame("player3");
        String playerToKick = game.joinGame("someone");
        // next line should throw an exception
        assertThrows(ResponseStatusException.class, () -> game.joinGame("player5"));
        game.leaveGame(playerToKick);
        String player4 = game.joinGame("player4");

        game.storeState("PlayerJoined");

        // start game --------------------
        GameEngine.loadGame(game.gameModel, game.settings);
        assertThrows(ResponseStatusException.class, () -> game.joinGame("late guy"));
        // round one
        game.gameModel.setCurrentRound(1);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);

        assertThrows(ResponseStatusException.class,
                () -> game.guess(playerToKick, new Answer(guessLocation(game.gameModel))));
        assertThrows(ResponseStatusException.class,
                () -> game.guess(player1, new Answer(guessLocation(game.gameModel))));

        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);

        assertThrows(ResponseStatusException.class,
                () -> game.guess(player1, new Answer(guessLocation(game.gameModel))));

        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round1Ended");
        // round two
        game.gameModel.setCurrentRound(2);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(8000.0, 8000.0)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round2Ended");

        // round three
        game.gameModel.setCurrentRound(3);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(608000.0, 108000.0)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round3Ended");

        // round four
        game.gameModel.setCurrentRound(4);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(40.0, 8.0)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round4Ended");
        // end game
        GameEngine.endGame(game.gameModel, game.settings);
        game.storeState("GameEnded");
    }

    @Test
    void game3() {
        // and usage of power ups
        // game with Host and 3 players
        // too many players will join
        // some players will be kicked
        // some players won't hand in answers in time
        var host = new User();
        host.setUsername("host_player_name");
        TestGame game = new TestGame(host, "Game3");
        String player1 = game.getHostPlayerId();
        game.storeState("Created");
        var settings = new Settings(4, 4, 2);
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        game.updateSettings(settings);
        game.storeState("SettingsUpdated");
        // lobby --------------------------
        game.openLobby();
        game.storeState("LobbyOpen");
        String player2 = game.joinGame("player2");
        String player3 = game.joinGame("player3");
        String playerToKick = game.joinGame("someone");
        // next line should throw an exception
        assertThrows(ResponseStatusException.class, () -> game.joinGame("player5"));
        game.leaveGame(playerToKick);
        String player4 = game.joinGame("player4");

        game.storeState("PlayerJoined");

        // start game --------------------
        GameEngine.loadGame(game.gameModel, game.settings);
        assertThrows(ResponseStatusException.class, () -> game.joinGame("late guy"));
        // round one
        game.gameModel.setCurrentRound(1);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.storeState("Round1Question");

        assertThrows(ResponseStatusException.class,
                () -> game.guess(playerToKick, new Answer(guessLocation(game.gameModel))));
        assertThrows(ResponseStatusException.class,
                () -> game.guess(player1, new Answer(guessLocation(game.gameModel))));

        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        var location = guessLocation(game.gameModel);
        game.usePowerUp(player1, PowerUp.X2);
        game.guess(player1, new Answer(location));
        game.guess(player2, new Answer(location));
        game.storeState("Round1Guessing");
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);

        var scores = game.gameModel.getCurrentScores();
        assertEquals(scores.get(player1).getScore(), scores.get(player2).getScore() * 2);

        assertThrows(ResponseStatusException.class,
                () -> game.guess(player1, new Answer(guessLocation(game.gameModel))));

        game.storeState("Round1MapReveal");
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round1Ended");
        // round two
        game.gameModel.setCurrentRound(2);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        assertThrows(ResponseStatusException.class,
                () -> game.usePowerUp(player1, PowerUp.X2));
        game.usePowerUp(player1, PowerUp.SHIELD);
        game.usePowerUp(player2, PowerUp.SHIELD);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(guessLocation(game.gameModel)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round2Ended");

        scores = game.gameModel.getCurrentScores();
        assertEquals(scores.get(player1).getScore(), scores.get(player2).getScore());
        assertNotEquals(0, scores.get(player1).getScore());

        // round three
        game.gameModel.setCurrentRound(3);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round3Ended");

        // round four
        game.gameModel.setCurrentRound(4);
        GameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        GameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(guessLocation(game.gameModel)));
        game.guess(player2, new Answer(guessLocation(game.gameModel)));
        game.guess(player3, new Answer(game.gameModel.getCurrentQuestion().getLocation()));
        game.guess(player4, new Answer(new GeoLocation(40.0, 8.0)));
        GameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        GameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        game.storeState("Round4Ended");
        // end game
        GameEngine.endGame(game.gameModel, game.settings);
        game.storeState("GameEnded");
    }

    private static GeoLocation guessLocation(GameModel gameModel) {
        var location = gameModel.getCurrentQuestion().getLocation();
        double dx = Math.random() * 2 - 1;
        double dy = Math.random() * 2 - 1;
        return new GeoLocation(location.getX() + dx, location.getY() + dy);
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
