//Endpoints for Game
package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.JoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.NextGameDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.PlayerIdDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.PostGuessDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.PowerUpDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.game.GameService;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

//CredentialsD credentials refer to a User object with valid id and token of the user. These are needed to do Host actions like create, start... a game
//for joining a game no credentials are needed
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Creates a new game and returns the game ID and the ID of the host player.
     * 
     * @param credentials the user credentials containing the ID and token of the
     *                    user
     * @return the created game response DTO containing the gameId and the playerId
     *         of the host player
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateGameResponseDTO createGame(@RequestBody CreateGameDTO createGameDTO) { // HTTP POST to /game/create

        return gameService.createGame(createGameDTO);
    }

    @GetMapping("/{gameId}/next")
    @ResponseStatus(HttpStatus.OK)
    public NextGameDTO nextGame(@PathVariable String gameId) {
        return gameService.getNextGameId(gameId);
    }

    /**
     * This endpoint returns all game ids.
     *
     * @response 200 OK - with a list of game ids
     */
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllGameIds() {
        return gameService.getAllGameIds();
    }

    /**
     * This endpoint is used by a player to leave a game.
     *
     * @param gameId   The ID of the game.
     * @param playerId The ID of the player.
     * @response 204 No Content - if the player has successfully left the game.
     */
    @PutMapping("/{gameId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGame(@PathVariable String gameId,
            @RequestBody PlayerIdDTO playerId) {
        gameService.leaveGame(gameId, playerId.getPlayerId());
    }

    /**
     * This endpoint is used by a player to join a game.
     *
     * @param gameId      The ID of the game.
     * @param displayName The display name of the player.
     * @response 302 FOUND - if the player has successfully joined the game.
     * @return The redirect URL to the game page.
     */
    @PostMapping("/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    public String joinGame(@PathVariable String gameId,
            @RequestBody JoinPostDTO DTO) {
        return gameService.joinGame(gameId, DTO.getDisplayName());
    }

    /**
     * This endpoint is used by a player to use a powerup in a game.
     *
     * @param gameId     The ID of the game.
     * @param powerUpDTO The PowerUpDTO containing the player's ID and the powerup
     *                   to
     *                   use.
     * @response 200 OK - if the powerup has been successfully used.
     * @return error if something went wrong
     */
    @PostMapping("/{gameId}/powerup")
    @ResponseStatus(HttpStatus.OK)
    public void usePowerUp(@PathVariable String gameId,
            @RequestBody PowerUpDTO powerUpDTO) {
        gameService.usePowerUp(gameId, powerUpDTO.getPlayerId(), powerUpDTO.getPowerUp());
    }

    /**
     * Opens the lobby for a game.
     * 
     * @param gameId      The ID of the game.
     * @param credentials The credentials of the player.
     */
    @PostMapping("/{gameId}/openLobby")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void openLobby(@PathVariable String gameId,
            @RequestBody CredentialsDTO credentials) {
        // Open the lobby of the game for the player.
        gameService.openLobby(gameId, credentials);
    }

    /**
     * Starts a game.
     * 
     * @param gameId      The ID of the game.
     * @param credentials The credentials of the player.
     */
    @PostMapping("/{gameId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startGame(@PathVariable String gameId,
            @RequestBody CredentialsDTO credentials) {
        // Start the game.
        gameService.startGame(gameId, credentials);
    }

    /**
     * Deletes a game.
     * 
     * @param gameId      The ID of the game.
     * @param credentials The credentials of the player.
     */
    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable String gameId,
            @RequestBody CredentialsDTO credentials) {
        // Delete the game.
        gameService.deleteGame(gameId, credentials);
    }

    /**
     * Submits a guess for a game.
     * 
     * @param gameId       The ID of the game.
     * @param postGuessDTO Contains the player ID and the geographical coordinates
     *                     of the guess.
     */
    @PostMapping("/{gameId}/guess")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeGuess(@PathVariable String gameId,
            @RequestBody PostGuessDTO postGuessDTO) {
        // Submit a guess for the game.
        gameService.submitAnswer(gameId, postGuessDTO.getPlayerId(),
                new Answer(new GeoLocation(postGuessDTO.getX(), postGuessDTO.getY())));
    }

    /**
     * Retrieves the view of a game.
     * 
     * @param gameId The ID of the game.
     * @return The view of the game.
     */
    @GetMapping("/{gameId}/getView")
    @ResponseStatus(HttpStatus.OK)
    public String getGameView(@PathVariable String gameId) {
        // Get the view of the game.

        return GameService.toJsonString(gameService.getGameView(gameId));
    }

    /**
     * Retrieves the round number, phase, and time till next phase of a game.
     * 
     * @param gameId The ID of the game.
     * @return get the State of the game.
     */
    @GetMapping("/{gameId}/getGameState")
    @ResponseStatus(HttpStatus.OK)
    public GameStateDTO getGameState(@PathVariable String gameId) {

        return gameService.getGameState(gameId);
    }

    /**
     * Retrieves the settings of a game.
     * 
     * @param gameId The ID of the game.
     * @return The settings of the game.
     */
    @GetMapping("/{gameId}/settings")
    @ResponseStatus(HttpStatus.OK)
    public GameSettingsDTO getSettings(@PathVariable String gameId) {
        // Get the settings of the game.
        SettingView settings = gameService.getSettingsView(gameId);
        return DTOMapper.INSTANCE.convertSettingsToDTO(settings);
    }

    /**
     * Updates the settings of a game.
     * 
     * @param gameId      The ID of the game.
     * @param settingsDTO The new settings of the game.
     * @param credentials The credentials of the player.
     */
    @PutMapping("/{gameId}/updateSettings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSettings(@PathVariable String gameId,
            @RequestBody GameSettingsDTO settingsDTO) {
        // Update the settings of the game.

        Settings settings = DTOMapper.INSTANCE.gameSettingsDTOtoSettings(settingsDTO);
        CredentialsDTO userCredentials = DTOMapper.INSTANCE.convertSettingsDTOtoCredentialsDTO(settingsDTO);
        gameService.updateSettings(gameId, settings, userCredentials);
    }

    /**
     * Stores the settings for a user
     * 
     * @param settingsDTO The settings of the game to store.
     */
    @PostMapping("/storeSettings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void storeSettings(@RequestBody GameSettingsDTO settingsDTO) {
        // Store the settings of the game.

        CredentialsDTO userCredentials = DTOMapper.INSTANCE.convertSettingsDTOtoCredentialsDTO(settingsDTO);
        Settings settings = DTOMapper.INSTANCE.gameSettingsDTOtoSettings(settingsDTO);
        gameService.storeSettings(settings, userCredentials);
    }

    /**
     * Retrieves the stored settings of a user
     * 
     * @param userId The ID of the player.
     * @param token  The token of the player.
     * @return The stored settings of a user
     */
    @GetMapping("/{userId}/{token}/getStoredSettings")
    @ResponseStatus(HttpStatus.OK)
    public List<Settings> getStoredSettings(@PathVariable Long userId, @PathVariable String token) {
        // Get the stored settings of the game.
        var settings = gameService.getSettings(new CredentialsDTO(userId, token));
        return settings;
    }

}
