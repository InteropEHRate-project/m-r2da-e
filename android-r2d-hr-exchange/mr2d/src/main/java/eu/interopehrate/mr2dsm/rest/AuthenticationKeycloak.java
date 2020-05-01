package eu.interopehrate.mr2dsm.rest;

/*
 *		Author: UBITECH
 *		Project: InteropEHRate - www.interopehrate.eu
 *
 *	    Description: Authentication library using keycloak
 */

import eu.interopehrate.mr2dsm.model.AccessTokenResponce;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthenticationKeycloak {

    /**
     *
     * Responsible for requesting the authentication token from keycloak
     *
     * @param grantType,
     * @param username
     * @param password
     * @param client_id
     *
     */

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/auth/realms/interopEHRate/protocol/openid-connect/token")
    Call<AccessTokenResponce> requestAuthToken(@Field("grant_type") String grantType,
                                               @Field("username") String username,
                                               @Field("password") String password,
                                               @Field("client_id") String client_id);

    /**
     *
     * Responsible for logout the active session
     *
     * @param grantType,
     * @param refresh_token
     * @param client_id
     *
     */

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/auth/realms/interopEHRate/protocol/openid-connect/logout")
    Call<ResponseBody> logout(@Field("grant_type") String grantType,
                              @Field("refresh_token") String refresh_token,
                              @Field("client_id") String client_id);
}
