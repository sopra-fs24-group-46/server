package ch.uzh.ifi.hase.soprafs24.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;

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

    // @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
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

    public User loginUser(String username, String password) {
        validateUsernameExists(username);
        User user = userRepository.findByUsername(username);
        validatePassword(user.getPassword(), password);

        // here user has entered correct credentials

        user.setToken(UUID.randomUUID().toString()); // token is used to identify user. Old tokens now invalid.
        user = userRepository.save(user);
        userRepository.flush();

        return user;
    }

    public User updateUser(CredentialsDTO credentials, User userInput) {
        User user = authenticateUser(credentials);
        // user is authenticated

        // validating user input.
        // if a field is null then it will not be updated (no validation)
        if (userInput.getUsername() != null) {
            validateUsernameUniqueness(userInput.getUsername());
        }
        if (userInput.getPassword() != null) {
            validatePasswordLength(userInput.getPassword());
        }

        // user input has to be valid at this stage!!
        user.updateField(userInput);// all updatable non null values are updated

        // storing to repository
        user = userRepository.save(user);
        userRepository.flush();

        return user;
    }

    public Boolean deleteUser(CredentialsDTO credentials) {
        User user = authenticateUser(credentials);
        // valid input

        // Delete the user by ID to avoid accidentally deleting the wrong user
        userRepository.deleteById(credentials.getId());
        userRepository.flush();
        return true;
    }

    public Boolean logoutUser(CredentialsDTO credentials) {
        User user = authenticateUser(credentials);

        user.setToken(""); // token no longer valid;

        user = userRepository.save(user);
        userRepository.flush();
        return true;
    }

    public Boolean isValidUser(CredentialsDTO credentials) {
        User user = authenticateUser(credentials);
        // valid input
        if (user == null) {// this should always be true
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unexpected Error. Credentials are valid but no user was found.");
        }
        return true;
    }

    private User authenticateUser(CredentialsDTO credentials) {
        User user = userRepository.findById(credentials.getId()).get();
        validateToken(user.getToken(), credentials.getToken());
        // valid input
        return user;
    }

    private void validateToken(String token, String token2) {
        if (token == null || token2 == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The tokens do not match. Please try again.");
        }
        if (!token.equals(token2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The tokens do not match. Please try again.");
        }
    }

    private void validatePassword(String password, String password2) {
        if (!password.equals(password2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords do not match. Please try again.");
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
    private void validateUsernameExists(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            String errorMessage = "The username " + username + " does not exist. Please enter an existing username.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private void validateUsernameUniqueness(String username) {
        User existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {
            String errorMessage = "The username is already taken. Please choose a different one.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private void validatePasswordLength(String password) {
        if (password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The password is too short. It must be at least 8 characters long.");
        }
    }

    @Deprecated
    public User updateUser(Long id, User userInput) {
        // Get the user from the database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // validating user input.
        // if a field is null then it will not be updated (no validation)
        if (userInput.getUsername() != null) {
            validateUsernameUniqueness(userInput.getUsername());
        }
        if (userInput.getPassword() != null) {
            validatePasswordLength(userInput.getPassword());
        }

        // user input has to be valid at this stage!!
        user.updateField(userInput);// all updatable non null values are updated

        // storing to repository
        user = userRepository.save(user);
        userRepository.flush();

        return user;
    }

    public User verifyUserCredentials(CredentialsDTO userCredentials) {
        return authenticateUser(userCredentials);// throws exception if not valid
    }

    public User getUserById(CredentialsDTO credentials) {
        return authenticateUser(credentials);
    }

}