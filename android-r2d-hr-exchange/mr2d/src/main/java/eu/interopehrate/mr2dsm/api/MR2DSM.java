package eu.interopehrate.mr2dsm.api;

import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;

public interface MR2DSM {
    /**
     *
     * Responsible for login by requesting the authentication token from keycloak
     *
     * @param username
     * @param password
     *
     */

    void login(String username, String password) throws MR2DSecurityException;

    /**
     * Responsible for logout
     */
    void logout() throws MR2DSecurityException;

    /**
     *
     * Responsible for retrive the token from SharePreferences
     *
     */
    String getToken();

    default boolean isAuthenticated() {
        return getToken() != null;
    }
}
