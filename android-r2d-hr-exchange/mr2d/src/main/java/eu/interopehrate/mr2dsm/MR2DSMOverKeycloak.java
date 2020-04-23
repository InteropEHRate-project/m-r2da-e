package eu.interopehrate.mr2dsm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.mr2dsm.model.AccessTokenResponce;
import eu.interopehrate.mr2dsm.rest.AuthenticationKeycloak;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MR2DSMOverKeycloak implements MR2DSM {
    private View view;
    public AuthenticationKeycloak postsService;
    String keycloakURL;

    public MR2DSMOverKeycloak(View view) {
        this.view = view;
    }

    public void setKeycloakURL(String keycloakURL) {
        this.keycloakURL = keycloakURL;
    }

    @Override
    public void login(String username, String password) {
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
                Log.d("access_token",accessToken);
                storeToken(accessToken);
            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                Log.e("access_token",t.getMessage());
            }
        });
    }

    @Override
    public void logout() {
        Log.d("MSSG logout", "and remove token");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("access_token", "");
        editor.commit();
        editor.apply();
    }

    @Override
    public String getToken() {
        Log.d("MSSG getToken", "from SharePreferences");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        String token = settings.getString("access_token", "");
        return token;
    }

    public void storeToken(String token) {
        Log.d("MSSG storeToken", token);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("access_token", token);
        editor.commit();
        editor.apply();
    }
    
}
