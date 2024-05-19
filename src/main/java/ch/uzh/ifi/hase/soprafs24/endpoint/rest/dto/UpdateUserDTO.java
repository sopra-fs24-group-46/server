package ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto;

import ch.uzh.ifi.hase.soprafs24.user.User;

public class UpdateUserDTO {
    private CredentialsDTO credentialsDTO;
    private User user;

    public CredentialsDTO getCredentialsDTO() {
        return credentialsDTO;
    }
    public void setCredentialsDTO(CredentialsDTO credentialsDTO) {
        this.credentialsDTO = credentialsDTO;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
