package eu.interopehrate.mr2dsm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.interopehrate.mr2dsm.model.Attribute;
import eu.interopehrate.mr2dsm.model.AuthRequest;
import eu.interopehrate.mr2dsm.model.ContextClass;
import eu.interopehrate.mr2dsm.model.NameIDPolicy;
import eu.interopehrate.mr2dsm.model.RequestedAuthenticationContext;
import eu.interopehrate.mr2dsm.model.SPType;
import eu.interopehrate.mr2dsm.model.SmsspTokenRequest;

public class ExampleBuilder {
    private SmsspTokenRequest smsspTokenRequest;
    private AuthRequest authRequest;
    private String username;
    private String password;

    public ExampleBuilder(String username, String password) {
        this.username = username;// "xavi";
        this.password = password; // "creus";
    }


    public void generateSmsspTokenRequest(String string_url){
        try {
            List<Attribute> attribute_list = new ArrayList<Attribute>();
            attribute_list.add(new Attribute("requested_attribute", "LegalName", true,null));
            attribute_list.add(new Attribute("requested_attribute", "LegalPersonIdentifier", true,null));
            attribute_list.add(new Attribute("requested_attribute", "CurrentAddress", true,null));
            attribute_list.add(new Attribute("requested_attribute", "FamilyName", true,null));
            attribute_list.add(new Attribute("requested_attribute", "FirstName", true,null));
            attribute_list.add(new Attribute("requested_attribute", "DateOfBirth", true,null));
            attribute_list.add(new Attribute("requested_attribute", "Gender", false,null));
            attribute_list.add(new Attribute("requested_attribute", "PersonIdentifier", true,null));
            SmsspTokenRequest smsspToken = new SmsspTokenRequest("2020-07-28T08:06:53.321Z", UUID.randomUUID(),string_url,"1");
            smsspToken.setAttribute_list(attribute_list);
            smsspToken.setCitizen_country("CA");
            smsspToken.setProvider_name("DEMO-SP-CA");
            smsspToken.setForce_authentication(true);
            smsspToken.setServiceUrl("http://212.101.173.84:8080/SpecificProxyService/IdpResponse");
            smsspToken.setNameIDPolicy(NameIDPolicy.PERSISTENT);
            smsspToken.setSPType(SPType.PUBLIC);
            smsspToken.setRequestedAuthenticationContext(new RequestedAuthenticationContext("minimum", ContextClass.C));
            this.smsspTokenRequest = smsspToken;}
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateAuthRequest(){
        AuthRequest auth = new AuthRequest();
        auth.setUsername(this.username);
        auth.setPassword(this.password);
        auth.setSmsspToken(smsspTokenRequest.encodeToken());
        auth.setEidasloa(ContextClass.A);
        auth.setCallback("http://212.101.173.84:8080/SpecificProxyService/IdpResponse");
        auth.setCheckBoxIpAddress("on");
        auth.setDoNotmodifyTheResponse("on");
        auth.setjSonRequestDecoded(smsspTokenRequest.convertToJSON());
        this.authRequest = auth;
    }

    public SmsspTokenRequest getSmsspTokenRequest() {
        return smsspTokenRequest;
    }

    public void setSmsspTokenRequest(SmsspTokenRequest smsspTokenRequest) {
        this.smsspTokenRequest = smsspTokenRequest;
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

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    public void setAuthRequest(AuthRequest authRequest) {
        this.authRequest = authRequest;
    }
}
