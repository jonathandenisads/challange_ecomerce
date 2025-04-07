package com.ecommerce.infrastructure.security;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class GenerateSecretyKey {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512"); // Use HS512 como no código
        keyGen.init(512); // Garante uma chave de tamanho adequado
        SecretKey secretKey = keyGen.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Nova chave secreta: " + encodedKey);
    }
}
