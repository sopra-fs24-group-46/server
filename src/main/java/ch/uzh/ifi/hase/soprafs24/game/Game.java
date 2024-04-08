//this acts as the interface for the game.
//Data is stored in GameModel and Settings (Java Classes)
//Logic is implemented in GameEngine

package ch.uzh.ifi.hase.soprafs24.game;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Player;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

import java.io.Serializable;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private Settings settings;
    private GameModel gameModel;
    private GameEngine gameEngine;

    // Public constructor allows creation of new games which can be stored to the
    // database
    public Game(Player hostPlayer) {
        settings = new Settings();
        settings.setHostPlayer(hostPlayer);
        gameModel = new GameModel();
        gameEngine = new GameEngine();
    }

    public Long getId() {
        return id;
    }

    public Boolean deleteGame() {
        // todo implement method
        throw new UnsupportedOperationException("Unimplemented method 'deleteGame'");
    }

    public Boolean updateSettings(Settings settings) {
        this.settings.update(settings);
        return true;
    }

    public SettingView getSettings() {
        return settings;
    }

    public Boolean joinGame(Player player) {
        if (gameModel.getPlayers().size() >= settings.getMaxPlayers()) {
            throw new IllegalStateException("Game is full");
        }
        gameModel.addPlayer(player);
        return true;
    }

    public Boolean leaveGame(Player player) {
        gameModel.removePlayer(player);
        return true;
    }

    public Boolean startGame() {
        // todo implement method
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    public Boolean guess(Player player, Answer guess) {
        // todo implement method
        throw new UnsupportedOperationException("Unimplemented method 'guess'");
    }

    public GameModelView getGameModelView() {
        return gameModel.getGameModelView();
    }

    public void verifyHost(Player hostPlayer) {
        var host = settings.getHostPlayer();
        if (hostPlayer.getId() != host.getId()) {
            throw new IllegalStateException("Host player does not match");
        }
    }

    // M2
    // public Boolean usePowerUp(Player player, PowerUp powerUp) {
    // //implement method
    // throw new UnsupportedOperationException("Unimplemented method 'usePowerUp'");
    // }
}
