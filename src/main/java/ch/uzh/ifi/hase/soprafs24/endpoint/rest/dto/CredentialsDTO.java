package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

public class CredentialsDTO {
    private Long id;
    private String token;

    public CredentialsDTO() {
        
    }
    public CredentialsDTO(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long userId) {
        this.id = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
