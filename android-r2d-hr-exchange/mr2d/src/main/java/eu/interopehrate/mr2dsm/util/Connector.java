package eu.interopehrate.mr2dsm.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import eu.interopehrate.mr2dsm.model.AuthRequest;

public class Connector {
    private final String url;
    private final HttpClient httpClient;
    private AuthRequest auth;
    private HttpPost httpPost;

    public Connector(String url) {
        this.url = url;
        this.httpClient = new DefaultHttpClient();
    }

    public void setEntity(AuthRequest auth){
        this.httpPost = new HttpPost(url);
        this.httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        this.httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        this.auth = auth;
        //create form data pairs
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username",auth.getUsername()));
        params.add(new BasicNameValuePair("password",auth.getPassword()));
        params.add(new BasicNameValuePair("eidasloa",auth.getEidasloa().toString()));
        params.add(new BasicNameValuePair("checkBoxIpAddress",auth.getCheckBoxIpAddress()));
        params.add(new BasicNameValuePair("smsspToken",auth.getSmsspToken() + "=="));
        params.add(new BasicNameValuePair("callback",auth.getCallback()));
        params.add(new BasicNameValuePair("jSonRequestDecoded",auth.getjSonRequestDecoded()));
        params.add(new BasicNameValuePair("doNotmodifyTheResponse",auth.getDoNotmodifyTheResponse()));

        String request = formatRequest(params);
        try {

        this.httpPost.setEntity(new StringEntity(request, "UTF-8"));}
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }
    public HttpResponse sendPost(){
        try {
            return httpClient.execute(httpPost);
        }
        catch (Exception e){
            return null;
        }
    }

    //Get the server response
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getResponse(HttpEntity httpEntity){
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader(httpEntity.getContent(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
        catch (Exception e){
            System.out.println("Error during response retrieval");
            e.printStackTrace();
            return " ";
        }

    }

    private String formatRequest(List<NameValuePair> params){
        //Build String in the only format accepted by the Eidas Server
        try {
            StringBuilder formatedRequest = new StringBuilder();
            for (int i=0;i<params.size()-2;i++) {
                formatedRequest.append(params.get(i).toString()).append("&");
            }
            //Delete the last "&"
            formatedRequest.deleteCharAt(formatedRequest.length()-1);

            //Encode the request JSON to match the desired server format
            String urlencodedJSON = URLEncoder.encode(auth.getjSonRequestDecoded(),"UTF-8");
            //Remove all "+" signs
            urlencodedJSON = urlencodedJSON.replace("+","");
            formatedRequest.append("&jSonRequestDecoded=");
            formatedRequest.append(urlencodedJSON);
            formatedRequest.append("&doNotmodifyTheResponse=").append(auth.getDoNotmodifyTheResponse());

            return formatedRequest.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public AuthRequest getAuth() {
        return auth;
    }

    public void setAuth(AuthRequest auth) {
        this.auth = auth;
    }
}
