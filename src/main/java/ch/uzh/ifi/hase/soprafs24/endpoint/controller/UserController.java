package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @DeleteMapping("/users")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@RequestBody CredentialsDTO credentials) {
    User userToDelete = DTOMapper.INSTANCE.convertCredentialsDTOtoUser(credentials);
    userService.deleteUser(userToDelete);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LoginResponseDTO login(@RequestBody UserPostDTO userPostDTO) {// let user service do that
    // retrieve the user by username
    User user = userService.getUserByUsername(userPostDTO.getUsername());
    if (user == null || !user.getPassword().equals(userPostDTO.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    // userService.updateUserStatus(user.getId(), UserStatus.ONLINE); //status
    // ONLINE

    // Convert user to API representation
    UserGetDTO userDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // Create a LoginResponseDTO object containing both the user DTO and the token
    LoginResponseDTO response = new LoginResponseDTO();
    response.setUser(userDTO);
    response.setToken(user.getToken());

    return response;
  }

  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
    userService.updateUser(id, updatedUser);
  }
}
