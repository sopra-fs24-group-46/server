//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;
import ch.uzh.ifi.hase.soprafs24.user.User;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Long createGame(User hostPlayer) {
        Game game = new Game(hostPlayer);
        return gameRepository.save(game).getId();
    }

    public void deleteGame(Long gameId, User hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        gameRepository.deleteById(gameId);
    }

    public void updateSettings(Long gameId, Settings settings, User hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        game.updateSettings(settings);
    }

    public SettingView getSettingsView(Long gameId) {
        Game game = findGameById(gameId);
        return game.getSettings();
    }

    public String joinGame(Long gameId, String displayName) {
        Game game = findGameById(gameId);
        String playerId = game.joinGame(displayName);
        return playerId;
    }

    public void leaveGame(Long gameId, String playerId) {
        findGameById(gameId).leaveGame(playerId);
    }

    public Boolean openLobby(Long gameId, User hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        return game.openLobby();
    }

    // at this time Lobby info is also a GameModelView
    public GameModelView getLobbyView(Long gameId) {
        return findGameById(gameId).getGameModelView();
    }

    public void startGame(Long gameId, User hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        game.startGame();
    }

    public void submitAnswer(Long gameId, String playerId, Answer answer) {
        Game game = findGameById(gameId);
        game.guess(playerId, answer);
    }

    public GameModelView getGameView(Long gameId) {
        Game game = findGameById(gameId);
        return game.getGameModelView();
    }

    // Private functions-------------------------------------------------
    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Game with id: " + gameId + " not found"));
    }
}
