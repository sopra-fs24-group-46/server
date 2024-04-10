//this acts as the interface for the game.
//Data is stored in GameModel and Settings (Java Classes)
//Logic is implemented in GameEngine

package ch.uzh.ifi.hase.soprafs24.game;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.RoundState;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;
    private String publicId;

    // For the moment don't save Infos in the database
    @Transient
    protected Settings settings;
    @Transient
    protected GameModel gameModel;
    @Transient
    protected GameEngine gameEngine;

    // Public constructor allows creation of new games which can be stored to the
    // database
    public Game(User hostPlayer) {
        settings = new Settings();
        settings.setHostPlayer(hostPlayer);
        gameModel = new GameModel();
        gameModel.addPlayer(hostPlayer.getDisplayName());
        gameEngine = new GameEngine();
        // returns a random string of 8 characters
        publicId = UUID.randomUUID().toString().substring(0, 8);
    }

    public String getId() {
        return publicId;
    }

    public String getHostPlayerId() {
        return gameModel.getHostPlayerId();
    }

    public Boolean openLobby() {
        GameEngine.initGame(gameModel, settings);
        return true;
    }

    public Boolean deleteGame() {
        gameEngine.deleteGame(gameModel, settings);
        return true;
    }

    public Boolean updateSettings(Settings settings) {
        this.settings.update(settings);
        return true;
    }

    public SettingView getSettings() {
        return settings;
    }

    public String joinGame(String displayName) {
        if (gameModel.getGameState() != GameState.SETUP && gameModel.getGameState() != GameState.LOBBY) {
            throw new IllegalStateException(
                    "You can only join games which are in Lobby state. Current state: " + gameModel.getGameState());
        }
        if (gameModel.getPlayers().size() >= settings.getMaxPlayers()) {
            throw new IllegalStateException("Game is full");
        }
        String playerId = gameModel.addPlayer(displayName);
        return playerId;

    }

    public Boolean leaveGame(String playerId) {
        gameModel.removePlayer(playerId);
        return true;
    }

    public Boolean startGame() {
        gameEngine.startGame(gameModel, settings);
        return true;
    }

    public Boolean guess(String playerId, Answer guess) {
        GameEngine.addAnswer(gameModel, guess, playerId);
        return true;
    }

    public GameModelView getGameModelView() {
        return gameModel;
    }

    public void verifyHost(User hostPlayer) {
        var hostId = settings.getHostUserId();
        if (hostPlayer.getId() != hostId) {
            throw new IllegalStateException("Host player does not match");
        }
    }

    // M2
    // public Boolean usePowerUp(Player player, PowerUp powerUp) {
    // //implement method
    // throw new UnsupportedOperationException("Unimplemented method 'usePowerUp'");
    // }

    public static void main(String[] args) {
        Game game = new Game(new User());
        game.updateSettings(new Settings(2, 2, 2));
        game.openLobby();
        // lobby --------------------------
        String player1 = game.joinGame("player1");
        System.out.println(toJsonString((GameModelView) game.getGameModelView()));
        String player2 = game.joinGame("player2");

        // start game --------------------
        game.gameEngine.loadGame(game.gameModel, game.settings);
        // round one
        game.gameModel.setCurrentRound(1);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        // round two
        game.gameModel.setCurrentRound(2);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.QUESTION);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.GUESSING);
        game.guess(player1, new Answer(new GeoLocation(10.0, 10.0)));
        game.guess(player2, new Answer(new GeoLocation(10.0, 10.0)));
        game.gameEngine.nextRoundState(game.gameModel, RoundState.MAP_REVEAL);
        game.gameEngine.nextRoundState(game.gameModel, RoundState.LEADERBOARD);
        // end game
        game.gameEngine.endGame(game.gameModel, game.settings);
        System.out.println(toJsonString((GameModelView) game.getGameModelView()));
    }

    private static String toJsonString(GameModelView gameModelView) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not convert GameModelView to JSON string", e);
        }
    }
}
