package eu.interopehrate.mr2dsm.model;


import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWT {
    private SignatureAlgorithm signatureAlgorithm;
    private String id;
    private String secret;
    private long timetoliveMillis;
    private Response response;
    private String encoded;

    public JWT() {
    }

    public JWT(SignatureAlgorithm signatureAlgorithm, String id, String secret, long timetoliveMillis) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.id = id;
        this.secret = secret;
        this.timetoliveMillis = timetoliveMillis;
    }

    public void encode(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingkey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //build jwt
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(signatureAlgorithm, signingkey);

        //Handle Expiration
        if (timetoliveMillis > 0){
            builder.setExpiration(new Date(System.currentTimeMillis() + timetoliveMillis));
        }
        this.encoded = builder.compact();
    }

    public Claims decode(String jwt){

        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(jwt).getBody();
    }



    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getTimetoliveMillis() {
        return timetoliveMillis;
    }

    public void setTimetoliveMillis(long timetoliveMillis) {
        this.timetoliveMillis = timetoliveMillis;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getEncoded() {
        return encoded;
    }
}
