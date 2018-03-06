
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author webapp
 */
public class EncryptHelper {
    public String encrypt(String s) {
      try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes("UTF-16")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            s = bigInt.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex){
         ex.printStackTrace();   
        }
        return s;
    }
    
}
