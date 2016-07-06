package io.github.b_lam.cryptogram;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Brandon on 7/4/2016.
 */
public class RSA {

    private BigInteger n, d, e;

    private int bitlen = 2048;

    /** Create an instance that can encrypt using someone else's public key. */
    public RSA(BigInteger newN, BigInteger newE) {
        n = newN;
        e = newE;
    }

    /** Create an instance to decrypt using a given private key. */
    public RSA(BigInteger newN, BigInteger newE, BigInteger newD){
        n = newN;
        e = newE;
        d = newD;
    }

    /** Create an instance that can both encrypt and decrypt. */
    public RSA(int bits) {
        bitlen = bits;
        SecureRandom r = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitlen / 2, r);
        BigInteger q = BigInteger.probablePrime(bitlen / 2, r);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("70001");
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("1"));
        }
        d = e.modInverse(phi);
    }

    public String decrypt(String message){
        String decryptedMessage = (new BigInteger(message)).modPow(d,n).toString();
        String decodedMessage = "";
        while(decryptedMessage.length()%3 != 0){
            decryptedMessage = "0".concat(decryptedMessage);
        }
        for(int i = 0; i < decryptedMessage.length(); i+=3){
            String letter = "" + decryptedMessage.charAt(i) + decryptedMessage.charAt(i+1) + decryptedMessage.charAt(i+2);
            decodedMessage = decodedMessage.concat(String.valueOf((char)Integer.parseInt(letter)));
        }
        return decodedMessage;

//        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }

    public String encrypt(String message){
        String encodedMessage = "";
        for(int i = 0; i < message.length(); i++){
            String letter = String.valueOf((int)message.charAt(i));
            while(letter.length() < 3){
                letter = "0".concat(letter);
            }
            encodedMessage = encodedMessage.concat(letter);
        }
        return (new BigInteger(encodedMessage).modPow(e,n)).toString();
//        return (new BigInteger(message.getBytes())).modPow(e, n).toString();
    }

    public void generateKeys(){
        SecureRandom r = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitlen / 2, r);
        BigInteger q = BigInteger.probablePrime(bitlen / 2, r);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("70001");
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("1"));
        }
        d = e.modInverse(phi);
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getD() {
        return d;
    }
}
