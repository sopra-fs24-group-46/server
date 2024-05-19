package ch.uzh.ifi.hase.soprafs24.user;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserRepository;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private User otherUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setToken("token_1234");

    otherUser = new User();
    otherUser.setId(2L);
    otherUser.setUsername("otherUsername");
    otherUser.setPassword("password");
    otherUser.setToken("token_1234");
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(otherUser);
    Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser));
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(otherUser));
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

    @Test
    public void getUsers_validInputs_success() {
        // given
        List<User> users = userService.getUsers();

        // then
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
        assertEquals(testUser.getUsername(), users.get(0).getUsername());
    }

    @Test
    public void getUserByUsername_validInputs_success() {
        // given
        User userByUsername = userService.getUserByUsername(testUser.getUsername());

        // then
        assertEquals(otherUser.getId(), userByUsername.getId());
        assertEquals(otherUser.getUsername(), userByUsername.getUsername());
    }

    @Test
    public void createUser_validInputs_success2() {

        // when
        assertThrows(ResponseStatusException.class, () -> userService.createUser(otherUser));
      }

    @Test
    public void loginUser_validInputs_success() {
        // given
        User loginUser = userService.loginUser(testUser.getUsername(), testUser.getPassword());

        // then
        assertEquals(testUser.getId(), loginUser.getId());
        assertEquals(testUser.getUsername(), loginUser.getUsername());
        assertNotNull(loginUser.getToken());
    }

    @Test
    public void updateUser_validInputs_success() {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("password2");

        // when
        User updatedUser = userService.updateUser(new CredentialsDTO(testUser.getId(), testUser.getToken()), user);

        // then
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(testUser.getUsername(), updatedUser.getUsername());
        assertNotNull(updatedUser.getToken());
    }

    @Test
    public void deleteUser_validInputs_success() {
        // given
        boolean deleteUser = userService.deleteUser(new CredentialsDTO(testUser.getId(), testUser.getToken()));

        // then
        assertTrue(deleteUser);
    }

    @Test
    public void logoutUser_validInputs_success() {
        // given
        boolean logoutUser = userService.logoutUser(new CredentialsDTO(testUser.getId(), testUser.getToken()));

        // then
        assertTrue(logoutUser);
    }

    @Test
    public void isValidUser_validInputs_success() {
        // given
        boolean isValidUser = userService.isValidUser(new CredentialsDTO(testUser.getId(), testUser.getToken()));

        // then
        assertTrue(isValidUser);
    }
}
