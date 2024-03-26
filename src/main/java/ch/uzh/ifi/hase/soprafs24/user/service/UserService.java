package ch.uzh.ifi.hase.soprafs24.user.service;

import ch.uzh.ifi.hase.soprafs24.user.entity.User;
import ch.uzh.ifi.hase.soprafs24.user.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    validateUsernameUniqueness(newUser.getUsername());
    validatePasswordLength(newUser.getPassword());
    newUser.setToken(UUID.randomUUID().toString());

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  private void validatePasswordLength(String password) {
    if (password.length() < 6) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is too short. It must be at least 8 characters long.");
    }
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void validateUsernameUniqueness(String username) {
    User existingUser = userRepository.findByUsername(username);

    if (existingUser != null) {
      String errorMessage = "The username is already taken. Please choose a different one.";
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }
  }
}
