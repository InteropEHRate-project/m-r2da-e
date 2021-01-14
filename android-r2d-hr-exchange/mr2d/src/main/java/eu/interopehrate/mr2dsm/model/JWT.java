package eu.interopehrate.mr2dsm.model;


import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Claims decode(String jwt){

        PublicKey publicKey = null;
        try {
            String key = readFile("private.pub", StandardCharsets.UTF_8);

            //Remove extra Strings
            key = key.replace("-----BEGIN PUBLIC KEY-----\n","");
            key = key.replace("-----END PUBLIC KEY-----", "");

            //Decode the public key and convert it to bytes
            //BASE64Decoder base64Decoder = new BASE64Decoder();
            //byte[] publicKeyBytes = base64Decoder.decodeBuffer(key);
            byte[] publicKeyBytes = Base64.decode(key, Base64.DEFAULT);

            // create a key object from the bytes
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwt).getBody();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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
