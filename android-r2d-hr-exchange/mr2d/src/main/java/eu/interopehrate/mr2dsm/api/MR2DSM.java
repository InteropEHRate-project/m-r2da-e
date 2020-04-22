package eu.interopehrate.mr2dsm.api;

public interface MR2DSM {
    /**
     *
     * Responsible for login by requesting the authentication token from keycloak
     *
     * @param username
     * @param password
     *
     */

    void login(String username, String password);

    /**
     * Responsible for logout
     */

    void logout();

    /**
     *
     * Responsible for retrive the token from SharePreferences
     *
     */

    String getToken();
}
