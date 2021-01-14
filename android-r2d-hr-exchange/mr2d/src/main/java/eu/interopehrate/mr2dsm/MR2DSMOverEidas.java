package eu.interopehrate.mr2dsm;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import eu.interopehrate.mr2dsm.util.*;
import eu.interopehrate.mr2dsm.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.interopehrate.mr2d.exceptions.MR2DSecurityException;
import eu.interopehrate.mr2dsm.api.MR2DSM;
import eu.interopehrate.mr2dsm.rest.AuthenticationKeycloak;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *		Author: UBITECH
 *		Project: InteropEHRate - www.interopehrate.eu
 *
 *	    Description: Implementation of MR2DSM using Keycloak as authentication server
 */
class MR2DSMOverEidas implements MR2DSM {
    private static final String CLIENT_NAME = "S-EHR";
    private static final String CLIENT_SECRET = "864452c3-3bc1-4f09-ad06-b3c4dde0ee7b";

    private String eIDASURL;
    private String accessToken;
    private String refreshToken;

    MR2DSMOverEidas(String eIDASURL) {
        // Stores eIDAS server endpoint
        this.eIDASURL = eIDASURL;
    }

    // TODO: Exception handling
    // TODO: better management of clientID and clientSecret
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @WorkerThread
    public void login(String username, String password) throws MR2DSecurityException {

        //Connection to local or cloud instance of Eidas Node
        String connectionUrl = eIDASURL;

        Log.d(getClass().getSimpleName(), "Executing login()");

        try {
            ExampleBuilder example = new ExampleBuilder(username,password);

            example.generateSmsspTokenRequest(connectionUrl);

            //Create an example AuthRequest Object
            example.generateAuthRequest();
            AuthRequest auth = example.getAuthRequest();

            Connector connector = new Connector(connectionUrl);
            connector.setEntity(auth);

            HttpResponse response = connector.sendPost();

            HttpEntity entity = response.getEntity();

            //Get server Response
            String results = connector.getResponse(entity);
            System.out.println(results);

            if (results.contains("error")){
                System.out.println("An authentication error has occurred - make sure the user credentials are correct");
            }
            else {
                JWT jwt = new JWT();
                Claims jwtClaims = jwt.decode(results);
                String assertion = jwtClaims.get("assertion").toString();
                String encryptedData = jwtClaims.get("attributes").toString();
                String data = jwt.decode(encryptedData).get("data3").toString();
                data = data. replaceAll("\n","");
                data = data. replaceAll("\r","");

                //Extract only the response part with REGEX
                Pattern pattern = Pattern.compile("(?<=\"response\" : )(.*)(?=}</textarea)");
                Matcher matcher = pattern.matcher(data);
                if (matcher.find()) data = matcher.group(1);


                //Map JSON to JAVA object
                Response res = new ObjectMapper().readValue(data, Response.class);

                if (res.getStatus().getSub_status_code() != null){
                    System.out.println("Successful Response with id: " + res.getId() +
                            "\n and status " + res.getStatus().getSub_status_code() +
                            "\n and issuer:" + res.getIssuer());}
                else {
                    System.out.println("Successful Response with id " + res.getId() +
                            " \n with status " + res.getStatus().getStatus_code() +
                            " \n and with attributes " + res.getAttribute_list());
                }

                //Check if the user was authenticated
                boolean authenticated = false;
                if (res.getStatus().getSub_status_code() != null && res.getStatus().getSub_status_code().equals(SubStatusCode.AuthnSuccess)) authenticated = true;
                if (res.getStatus().getSub_status_code() == null && res.getStatus().getStatus_code().equals("success")) authenticated = true;

                //Only display the extraJwt if the Authentication process was successful
                if (authenticated) {
                    System.out.println("The assertion is: "+ assertion);
                }
            }}
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO: Exception handling
    @Override
    @WorkerThread
    public void logout() throws MR2DSecurityException {
        Log.d(getClass().getSimpleName(), "Executing logout()");

        accessToken = null;
        refreshToken = null;
        Log.d(getClass().getSimpleName(), "Succesfully executed logout()");
    }

    public String getToken() {
        return accessToken;
    }

    public String getRefreshTokenToken() {
        return refreshToken;
    }

}
