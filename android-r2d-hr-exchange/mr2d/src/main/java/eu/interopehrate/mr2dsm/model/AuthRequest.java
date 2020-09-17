package eu.interopehrate.mr2dsm.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthRequest {
    private String username;
    private String password;
    private ContextClass eidasloa;
    private String checkBoxIpAddress;
    private String smsspToken;
    private String callback;
    private String jSonRequestDecoded;
    private String doNotmodifyTheResponse;

    public AuthRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ContextClass getEidasloa() {
        return eidasloa;
    }

    public void setEidasloa(ContextClass eidasloa) {
        this.eidasloa = eidasloa;
    }

    public String getCheckBoxIpAddress() {
        return checkBoxIpAddress;
    }

    public void setCheckBoxIpAddress(String checkBoxIpAddress) {
        this.checkBoxIpAddress = checkBoxIpAddress;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getjSonRequestDecoded() {
        return jSonRequestDecoded;
    }

    public void setjSonRequestDecoded(String jSonRequestDecoded) {
        this.jSonRequestDecoded = jSonRequestDecoded;
    }

    public String getDoNotmodifyTheResponse() {
        return doNotmodifyTheResponse;
    }

    public void setDoNotmodifyTheResponse(String doNotmodifyTheResponse) {
        this.doNotmodifyTheResponse = doNotmodifyTheResponse;
    }

    public String getSmsspToken() {
        return smsspToken;
    }

    public void setSmsspToken(String smsspToken) {
        this.smsspToken = smsspToken;
    }

    public String convertToJSON(){
        ObjectMapper obj = new ObjectMapper();
        try {
            //Wrap enfolded SmsspToken as an authentication_request
            return obj.writeValueAsString(this).replace("\"attributeList\"","\"authentication_request\" : {\"attributeList\"");
        }
        catch (JsonProcessingException e){
            System.out.println("Error converting to JSON");
            e.printStackTrace();
            return "";
        }
    }
}
