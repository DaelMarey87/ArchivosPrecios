package util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DesEncrypter {
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DesEncrypter.class);

    private Cipher ecipher;
    private Cipher dcipher;
    // 8-byte Salt
    private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3,
            (byte) 0x03 };

    public DesEncrypter(){            
        try {
        	ecipher = Cipher.getInstance(Constantes.CIPHER_TRANSFORMATION);
			dcipher = Cipher.getInstance(Constantes.CIPHER_TRANSFORMATION);
			SecretKey key = new SecretKeySpec(salt, Constantes.CIPHER_TRANSFORMATION);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);	
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			LOGGER.error("fallo constructor encripter" + e);
		}            
    }

    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8.displayName());

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.getEncoder().encodeToString(enc);
        } catch (BadPaddingException | IllegalBlockSizeException |UnsupportedEncodingException e) {
        	LOGGER.error("error al encriptar la cadena: " + e);
        } 
        return "FAIL!!";
    }

    public String decrypt(String str) {
            // Decode base64 to get bytes
        try{
            byte[] dec = Base64.getDecoder().decode(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, StandardCharsets.UTF_8);
        }catch( IllegalBlockSizeException | BadPaddingException e){
        	LOGGER.error("error al desencriptar la cadena: "+str+":"+ e);
        }
        return "FAIL!!!!";
    }    
}

