/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author webapp
 */
import java.util.Random;

public final class OTPGenerator {
    private int otp;
    
    public OTPGenerator () {
        setOTP();
    }
    
    public int getOTP() {
        return otp;
    }
    
    public String toString() {
        String s = "" + otp;
        return s;
    }
    
    public void setOTP (){
    	Random randomGenerator = new Random();
        int min = 1000;
        int max = 9999;
	 otp = randomGenerator.nextInt((max - min) + 1) + min; //Generate otp between 1000 and 9999
         this.otp = otp;
    }
}
