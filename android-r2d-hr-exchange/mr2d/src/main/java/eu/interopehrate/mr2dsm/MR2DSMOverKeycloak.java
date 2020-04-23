package eu.interopehrate.mr2dsm;

import android.util.Log;

import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.mr2dsm.model.AccessTokenResponce;
import eu.interopehrate.mr2dsm.rest.AuthenticationKeycloak;
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
    private AuthenticationKeycloak postsService;
    private String keycloakURL;
    private String accessToken;

    MR2DSMOverKeycloak(String keycloakURL) {
        this.keycloakURL = keycloakURL;
    }

    //TODO: Exception handling
    @Override
    public void login(String username, String password) throws MR2DSecurityException {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(keycloakURL);
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitKeycloak = builder
                .build();
        postsService = retrofitKeycloak.create(AuthenticationKeycloak.class);

        Call<AccessTokenResponce> call = postsService.requestAuthToken("password",username,password,"S-EHR");

        call.enqueue(new Callback<AccessTokenResponce>() {
            @Override
            public void onResponse(Call<AccessTokenResponce> call, Response<AccessTokenResponce> response) {
                String accessToken = response.body().getAccess_token().toString();
                Log.d(getClass().getName(), accessToken);
                storeToken(accessToken);
            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                //TODO: Exception handling
                Log.e(getClass().getName(), t.getMessage());
            }
        });
    }

    //TODO: Exception handling
    @Override
    public void logout() throws MR2DSecurityException {
        Log.d(getClass().getName(), "Executing logout()");
        this.accessToken = null;
        /*
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("access_token", "");
        editor.commit();
        editor.apply();
         */
    }

    public String getToken() {
        /*
        Log.d(getClass().getName(), "from SharePreferences");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        String token = settings.getString("access_token", "");
         */
        return accessToken;
    }

    private void storeToken(String token) {
        Log.d("MSSG storeToken", token);
        this.accessToken = token;
        /*
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("access_token", token);
        editor.commit();
        editor.apply();
         */
    }
    
}
