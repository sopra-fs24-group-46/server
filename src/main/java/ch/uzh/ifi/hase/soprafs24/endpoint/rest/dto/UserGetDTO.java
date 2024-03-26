package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;


public class UserGetDTO {

  private Long id;
  private String token;
  private String username;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setToken(String token) {
    this.token = token;
  }
  public String getToken() {
    return this.token;
  }
}
