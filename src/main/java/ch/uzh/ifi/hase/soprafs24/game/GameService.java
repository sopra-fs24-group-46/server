//this provides functions for the endpoints and takes care of navigating traffic to the right place
package ch.uzh.ifi.hase.soprafs24.game;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
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

    public CreateGameResponseDTO createGame(User userCredentials) {
        Game game = new Game(userCredentials);
        gameRepository.save(game);
        gameRepository.flush();

        CreateGameResponseDTO response = new CreateGameResponseDTO();
        response.setGameId(game.getId());
        response.setPlayerId(game.getHostPlayerId());
        return response;
    }

    public void deleteGame(Long gameId, User userCredentials) {
        Game game = findGameById(gameId);
        game.verifyHost(userCredentials);
        gameRepository.deleteById(gameId);
    }

    public void updateSettings(Long gameId, Settings settings, User userCredentials) {
        Game game = findGameById(gameId);
        game.verifyHost(userCredentials);
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

    public Boolean openLobby(Long gameId, User userCredentials) {
        Game game = findGameById(gameId);
        game.verifyHost(userCredentials);
        return game.openLobby();
    }

    // at this time Lobby info is also a GameModelView
    public String getLobbyView(Long gameId) {
        return getGameView(gameId);
    }

    public void startGame(Long gameId, User userCredentials) {
        Game game = findGameById(gameId);
        game.verifyHost(userCredentials);
        game.startGame();
    }

    public void submitAnswer(Long gameId, String playerId, Answer answer) {
        Game game = findGameById(gameId);
        game.guess(playerId, answer);
    }

    // for an example JSON string check:
    // src\main\resources\GameModelView_gameEnded.json
    public String getGameView(Long gameId) {
        Game game = findGameById(gameId);
        GameModelView gameModelView = game.getGameModelView();
        ObjectMapper mapper = new ObjectMapper();
        String gameModelViewJson = null;

        try {// this handles the json conversion (DTO -> JSON) since i don't trust the DTO
             // mappers to handle multilayer objects
            gameModelViewJson = mapper.writeValueAsString(gameModelView);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not convert GameModelView to JSON string", e);
        }

        return gameModelViewJson;
    }

    // Private functions-------------------------------------------------
    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalStateException("Game with id: " + gameId + " not found"));
    }
}
