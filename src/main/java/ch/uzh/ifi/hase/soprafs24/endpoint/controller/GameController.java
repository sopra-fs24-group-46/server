//Endpoints for Game
package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.PostGuessDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.game.GameService;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs24.user.User;
import org.springframework.web.bind.annotation.GetMapping;

//CredentialsD credentials refer to a User object with valid id and token of the user. These are needed to do Host actions like create, start... a game
//for joining a game no credentials are needed
//todo move credentials to request parameters and update the calls
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
    public CreateGameResponseDTO createGame(@RequestBody CredentialsDTO credentials) { // HTTP POST to /game/create

        User userCredentials = DTOMapper.INSTANCE.convertCredentialsDTOtoEntity(credentials);
        CreateGameResponseDTO response = gameService.createGame(userCredentials);
        return response;
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
            @RequestBody String playerId) {
        gameService.leaveGame(gameId, playerId);
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
    @ResponseStatus(HttpStatus.FOUND)
    public String joinGame(@PathVariable String gameId,
            @RequestBody String displayName) {
        return gameService.joinGame(gameId, displayName);
    }

    /**
     * Retrieves the content of a JSON file with the specified name from the
     * GameModelViews directory. The file name must be a valid file name.
     * 
     * @param jsonName the name of the JSON file to retrieve
     * @return the content of the JSON file as a String, or an error message if
     *         the file does not exist
     */
    @GetMapping("/developer/getView/{jsonName}")
    @ResponseStatus(HttpStatus.OK)
    public String getMethodName(@PathVariable String jsonName) {
        String jsonContent;

        String filePath = "src/main/resources/GameModelViews/" + jsonName + ".json";

        try {
            // Read JSON file content as a String
            jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            jsonContent = String.format(
                    "Error: File %s not found. Check src/main/resources/GameModelViews/ for valid file names",
                    jsonName);
        }

        return jsonContent;
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
        User userCredentials = DTOMapper.INSTANCE.convertCredentialsDTOtoEntity(credentials);
        gameService.openLobby(gameId, userCredentials);
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
        User userCredentials = DTOMapper.INSTANCE.convertCredentialsDTOtoEntity(credentials);
        gameService.startGame(gameId, userCredentials);
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
        User userCredentials = DTOMapper.INSTANCE.convertCredentialsDTOtoEntity(credentials);
        gameService.deleteGame(gameId, userCredentials);
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

        return gameService.getGameView(gameId);
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
    @PutMapping("/{gameId}/updateSettings") // todo discuss the right way for parameters
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSettings(@PathVariable String gameId,
            @RequestBody GameSettingsDTO settingsDTO,
            @RequestHeader(value = "userId") String userId,
            @RequestHeader(value = "userToken") String userToken) {
        // Update the settings of the game.

        CredentialsDTO credentials = new CredentialsDTO();
        credentials.setId(Long.parseLong(userId));
        credentials.setToken(userToken);
        Settings settings = DTOMapper.INSTANCE.gameSettingsDTOtoSettings(settingsDTO);
        User userCredentials = DTOMapper.INSTANCE.convertCredentialsDTOtoEntity(credentials);
        gameService.updateSettings(gameId, settings, userCredentials);
    }

}
