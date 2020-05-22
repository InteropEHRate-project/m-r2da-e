package eu.interopehrate.mr2dsm;

import android.util.Log;

import androidx.annotation.WorkerThread;

import java.io.IOException;

import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.mr2dsm.model.AccessTokenResponce;
import eu.interopehrate.mr2dsm.rest.AuthenticationKeycloak;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *		Author: UBITECH
 *		Project: InteropEHRate - www.interopehrate.eu
 *
 *	    Description: Implementation of MR2DSM using Keycloak as authentication server
 */
class MR2DSMOverKeycloak implements MR2DSM {
    private static final String CLIENT_NAME = "S-EHR";
    private static final String CLIENT_SECRET = "f05b9442-0fac-4c33-81d5-026366e54f1c";

    private AuthenticationKeycloak postsService;
    private String keycloakURL;
    private String accessToken;
    private String refreshToken;
    private Retrofit retrofitKeycloak;

    MR2DSMOverKeycloak(String keycloakURL) {
        // Stores Keycloack server endpoint
        this.keycloakURL = keycloakURL;
    }

    // TODO: Exception handling
    // TODO: better management of clientID and clientSecret
    @Override
    @WorkerThread
    public void login(String username, String password) throws MR2DSecurityException {
        Log.d(getClass().getSimpleName(), "Executing login()");
        if (retrofitKeycloak == null)
            loadRetrofit();

        // creates an instance of AuthenticationKeycloak
        postsService = retrofitKeycloak.create(AuthenticationKeycloak.class);

        // Executes a synchronous call to the REST
        try {
            Call<AccessTokenResponce> call = postsService.requestAuthToken("password", username,
                    password, CLIENT_NAME, CLIENT_SECRET);
            AccessTokenResponce token = call.execute().body();
            accessToken = token.getAccess_token();
            refreshToken = token.getRefresh_token();
        } catch (IOException e) {
            throw new MR2DSecurityException(e);
        }

        /*
        call.enqueue(new Callback<AccessTokenResponce>() {
            @Override
            public void onResponse(Call<AccessTokenResponce> call, Response<AccessTokenResponce> response) {
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccess_token().toString();
                    String refreshToken = response.body().getRefresh_token().toString();
                    Log.d(getClass().getName(), accessToken);
                    storeToken(accessToken);
                    storeRefreshToken(refreshToken);
                } else
                    throw new MR2DSecurityException("Error " + response.code() + ": " + response.message());
            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
            }
        });
         */

    }

    //TODO: Exception handling
    @Override
    @WorkerThread
    public void logout() throws MR2DSecurityException {
        Log.d(getClass().getSimpleName(), "Executing logout()");
        if (retrofitKeycloak == null)
            loadRetrofit();

        postsService = retrofitKeycloak.create(AuthenticationKeycloak.class);

        try {
            Call call = postsService.logout("refresh_token",refreshToken, CLIENT_NAME);
            call.execute().body();
            accessToken = null;
            refreshToken = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(getClass().getName(), "Logged out");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //TODO: Exception handling
                Log.e(getClass().getName(), t.getMessage());
            }
        });
         */
    }

    public String getToken() {
        return accessToken;
    }

    public String getRefreshTokenToken() {
        return refreshToken;
    }

    private void loadRetrofit() {
        // Creates the Retrofit builders
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(keycloakURL);
        builder.addConverterFactory(GsonConverterFactory.create());
        retrofitKeycloak = builder.build();
    }
}
