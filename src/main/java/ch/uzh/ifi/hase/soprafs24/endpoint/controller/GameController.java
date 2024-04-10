//Endpoints for Game
package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.controller.UserController;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CreateGameResponseDTO;
import ch.uzh.ifi.hase.soprafs24.game.GameService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs24.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateGameResponseDTO createGame(@RequestBody User hostPlayer) {
        return gameService.createGame(hostPlayer);
    }

    // @PutMapping("/start")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void startGame(@PathVariable Long id) {
    // gameService.startGame(id);
    // }

    // @PutMapping("/end")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void endGame(@PathVariable Long id) {
    // gameService.endGame(id);
    // }

    // Example usage get /game/developer/getView/Game1_0_Created
    // this returns the content of the Game1_0_Created.json file
    // Check src/main/resources/GameModelViews/ for valid file names
    @GetMapping("/developer/getView/{jsonName}")
    @ResponseStatus(HttpStatus.OK)
    public String getMethodName(@PathVariable String jsonName) {

        String jsonContent = "No such file. Check src/main/resources/GameModelViews/ for valid file names";
        try {
            // Read JSON file content as a String
            jsonContent = new String(Files.readAllBytes(
                    Paths.get(
                            "src/main/resources/GameModelViews/" + jsonName + ".json")));
            // Use the JSON content String as needed
            // System.out.println("JSON content read from file as a String: " +
            // jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonContent;
    }

    // player can leave a game
    @PutMapping("/{gameId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGame(@PathVariable Long gameId, @RequestBody String player) {
        gameService.leaveGame(gameId, player);
    }

    // player can join
    @PostMapping("/{gameId}/join")
    @ResponseStatus(HttpStatus.FOUND)
    public String joinGame(@PathVariable Long gameId, @RequestBody User user) {
        return gameService.joinGame(gameId, user.getDisplayName());
    }

}
