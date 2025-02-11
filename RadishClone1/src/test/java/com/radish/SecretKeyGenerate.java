package com.radish;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

public class SecretKeyGenerate {
    @Test
    void generateSecretKey(){
        SecretKey key= Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String encodedKey= DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.println("Secret Key: "+encodedKey);
    }
}
