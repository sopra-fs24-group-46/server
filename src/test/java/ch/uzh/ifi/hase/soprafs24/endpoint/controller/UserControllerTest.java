package ch.uzh.ifi.hase.soprafs24.endpoint.controller;

import ch.uzh.ifi.hase.soprafs24.endpoint.controller.UserController;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UpdateUserDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.user.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setToken("1");

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @Test
  public void getAllUsers_validInput_usersReturned() throws Exception {
    // given
    User user = new User();
    user.setUsername("testUsername");

    List<User> allUsers = Collections.singletonList(user);

    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }

  @Test
  public void getUser_validInput_userReturned() throws Exception {
    // given
    User user = new User();
    user.setUsername("testUsername");
    user.setId(1L);
    user.setToken("token_1234");
    user.setPassword("12345678");

    // User user = userService.getUserById(credentials);
    given(userService.getUserById(Mockito.any())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/{id}/{token}", user.getId(), user.getToken())
        .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  @Test
  public void deleteUser_validInput_userDeleted() throws Exception {
    // given
    CredentialsDTO credentials = new CredentialsDTO(1L, "token_1234");

    given(userService.deleteUser(Mockito.any())).willReturn(null);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder deleteRequest = delete("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(credentials));

    // then
    mockMvc.perform(deleteRequest).andExpect(status().isNoContent());
  }

  @Test
  public void login_validInput_loginSuccessful() throws Exception {
    // given
    User user = new User();
    user.setUsername("testUsername");
    user.setPassword("12345678");
    user.setToken("1");

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername(user.getUsername());
    userPostDTO.setPassword(user.getPassword());

    LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
    loginResponseDTO.setToken("1");

    given(userService.loginUser(Mockito.any(), Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$.token", is(loginResponseDTO.getToken())));
  }

  @Test
  public void updateUser_validInput_userUpdated() throws Exception {
    // given
    User user = new User();
    user.setUsername("testUsername");
    user.setPassword("12345678");
    user.setToken("token_1234");
    user.setId(1L);
    UpdateUserDTO updateUserDTO = new UpdateUserDTO();
    updateUserDTO.setUser(user);

    given(userService.updateUser(Mockito.eq(new CredentialsDTO(user.getId(), user.getToken())), Mockito.any())).willReturn(null);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(updateUserDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNoContent());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}

