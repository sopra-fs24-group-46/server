package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class LoginResponseDTO {
    
    private UserGetDTO user;
    private String token;

    public UserGetDTO getUser() {
        return user;
    }
    
    public void setUser(UserGetDTO user) {
    this.user = user;
    }
    public String getToken() {
    return token;
    }
    
      public void setToken(String token) {
        this.token = token;
      }
}
