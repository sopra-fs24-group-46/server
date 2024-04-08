//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import ch.uzh.ifi.hase.soprafs24.game.View.GameModelView;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GuestPlayer;
import ch.uzh.ifi.hase.soprafs24.game.entity.Player;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Long createGame(Player hostPlayer) {
        Game game = new Game(hostPlayer);
        return gameRepository.save(game).getId();
    }

    public void deleteGame(Long gameId, Player hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        gameRepository.deleteById(gameId);
    }

    public void updateSettings(Long gameId, Settings settings, Player hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        game.updateSettings(settings);
    }

    public SettingView getSettingsView(Long gameId) {
        Game game = findGameById(gameId);
        return game.getSettings();
    }

    public Boolean joinGameAsUser(Long gameId, Player player) {
        Game game = findGameById(gameId);
        return game.joinGame(player);
    }

    public Player joinGameAsGuest(Long gameId, String name) {
        Game game = findGameById(gameId);
        GuestPlayer player = new GuestPlayer();
        player.setNickname(name);
        if (game.joinGame(player)) {
            return player;
        } else {
            throw new IllegalStateException("Could not join game as guest");
        }
    }

    public void leaveGame(Long gameId, Player player) {
        findGameById(gameId).leaveGame(player);
    }

    // at this time Lobby info is also a GameModelView
    public GameModelView getLobbyView(Long gameId) {
        return findGameById(gameId).getGameModelView();
    }

    public void startGame(Long gameId, Player hostPlayer) {
        Game game = findGameById(gameId);
        game.verifyHost(hostPlayer);
        game.startGame();
    }

    public void submitAnswer(Long gameId, Player player, Answer answer) {
        Game game = findGameById(gameId);
        game.guess(player, answer);
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
