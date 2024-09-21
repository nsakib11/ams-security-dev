package com.ams.api.ase;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Component
public class AesCbcUtil {
    @Value("${aes.KEY_SIZE}")
	private int KEY_SIZE;
    @Value("${aes.INTERACTION_COUNT}")
    private int INTERACTION_COUNT ;
    private final String  MODE = "AES/GCM/NoPadding";
    private final String  SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA1";
    private final String  SECRET_KEY_ALGORITHM = "AES";
    private final String  CHARSET_NAME = "UTF-8";
    @Value("${aes.SALT}")
    private String  SALT;
    @Value("${aes.IV}")
    private String  IV;
    @Value("${aes.PASSPHRASE}")
    private String  PASSPHRASE;
    private final Cipher cipher;

    public AesCbcUtil() {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw fail(e);
        }
    }

    public String encrypt(String plaintext) {
        try {
            SecretKey key = generateKey(SALT, PASSPHRASE);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, IV, plaintext.getBytes(CHARSET_NAME));
            return base64(encrypted);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    public String decrypt(String ciphertext) {
        try {
            SecretKey key = generateKey(SALT, PASSPHRASE);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, IV, base64(ciphertext));
            return new String(decrypted, CHARSET_NAME);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        }
        catch (InvalidKeyException
               | InvalidAlgorithmParameterException
               | IllegalBlockSizeException
               | BadPaddingException e) {
            throw fail(e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), INTERACTION_COUNT, KEY_SIZE);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), SECRET_KEY_ALGORITHM);
            return key;
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw fail(e);
        }
    }

    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }

    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        }
        catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }

    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }
}
