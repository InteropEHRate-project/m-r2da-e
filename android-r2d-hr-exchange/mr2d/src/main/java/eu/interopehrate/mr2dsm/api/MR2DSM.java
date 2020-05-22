package eu.interopehrate.mr2dsm.api;

import androidx.annotation.WorkerThread;

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
    @WorkerThread
    void login(String username, String password) throws MR2DSecurityException;

    /**
     * Responsible for logout
     */
    @WorkerThread
    void logout() throws MR2DSecurityException;

    /**
     *
     * Responsible for retrieve the token from SharePreferences
     *
     */
    String getToken();

    /**
     * Default implementation of method isAuthenticated() to obtain the status of MR2DSM
     * @return
     */
    default boolean isAuthenticated() {
        return getToken() != null;
    }
}
