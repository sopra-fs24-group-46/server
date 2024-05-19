//this acts as the interface for the game.
//Data is stored in GameModel and Settings (Java Classes)
//Logic is implemented in GameEngine

package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.game.Enum.GameState;
import ch.uzh.ifi.hase.soprafs24.game.Enum.PowerUp;
import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GameModel;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;

public class Game{

    private String id;

    protected Settings settings;

    protected GameModel gameModel;

    // Public constructor allows creation of new games which can be stored to the
    // database
    public Game(User hostPlayer) {
        settings = Settings.defaultSettings();
        settings.setHostUserId(hostPlayer.getId());
        gameModel = new GameModel();
        var host = gameModel.addPlayer(hostPlayer.getDisplayName());
        gameModel.setHostPlayer(host);

        var number = Math.abs(java.util.UUID.randomUUID().getLeastSignificantBits()) % 1000000L;
        id = Long.toString(number);
    }

    public Game() {// needed for JPA

    }

    public String getId() {
        return id;
    }

    public String getHostPlayerId() {
        return gameModel.getHostPlayer().getId();
    }

    public Boolean openLobby() {
        GameEngine.initGame(gameModel, settings);
        return true;
    }

    public Boolean closeGame() {
        gameModel.setGameState(GameState.CLOSED);
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
        if (gameModel.getGameState() != GameState.LOBBY && gameModel.getGameState() != GameState.SETUP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can only join games which are in Lobby or Setup. Current state: " + gameModel.getGameState());
        }
        if (gameModel.getPlayers().stream().anyMatch(player -> player.getDisplayName().equals(displayName))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Player name " + displayName + " is already taken. Choose another name");
        }
        if (gameModel.getPlayers().size() >= settings.getMaxPlayers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game is already full: " + gameModel.getPlayers().size() + "\\" + settings.getMaxPlayers());
        }
        String playerId = gameModel.addPlayer(displayName);
        return playerId;

    }

    public Boolean leaveGame(String playerId) {
        validatePlayerId(playerId);
        // close game if host leaves
        if(gameModel.getHostPlayer().getId().equals(playerId)) {
            closeGame();
        }
        gameModel.removePlayer(playerId);
        return true;
    }

    public Boolean startGame() {
        GameEngine.scheduleGame(gameModel, settings);
        return true;
    }

    public Boolean guess(String playerId, Answer guess) {
        validatePlayerId(playerId);
        GameEngine.addAnswer(gameModel, guess, playerId);
        return true;
    }

    public Boolean usePowerUp(String playerId, PowerUp powerUp) {
        gameModel.usePowerUp(playerId, powerUp);
        return true;
    }

    public GameModelView getGameModelView() {
        return gameModel;
    }

    public GameStateDTO getGameState() {
        GameStateDTO gameState = new GameStateDTO();
        gameState.setGameState(gameModel.getGameState());
        gameState.setRoundState(gameModel.getRoundState());
        gameState.setCurrentRound(gameModel.getCurrentRound());
        gameState.setTimeTillNextPhaseInMillis(GameEngine.timeTillNextPhase(gameModel, settings));

        return gameState;
    }

    public void verifyHost(User hostPlayer) {
        var hostId = settings.getHostUserId();
        if (!hostPlayer.getId().equals(hostId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Host player does not match");
        }
    }

    private boolean validatePlayerId(String playerId) {
        if (gameModel.getPlayersIds().contains(playerId)) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Player with id: \"" + playerId + "\" is not in the game. Provide a valid player id");
    }

}
