package ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers;

import ch.uzh.ifi.hase.soprafs24.user.User;

public class TestUser extends User {

    private TestUser() {
    }

    public static TestUser from(User user) {
        TestUser testUser = new TestUser();
        testUser.setId(user.getId());
        testUser.setUsername(user.getUsername());
        testUser.setToken(user.getToken());
        return testUser;
    }

    public String credentialsJSON() {
        return String.format("{ \"id\": \"%s\", \"token\": \"%s\" }",
                this.getId(), this.getToken());
    }
}