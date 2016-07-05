package io.github.b_lam.cryptogram;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Brandon on 7/4/2016.
 */
public class RSA {

    private BigInteger n, d, e;

    private int bitlen = 1024;

    /** Create an instance that can encrypt using someone elses public key. */
    public RSA(BigInteger newn, BigInteger newe) {
        n = newn;
        e = newe;
    }

    /** Create an instance that can both encrypt and decrypt. */
    public RSA(int bits) {
        bitlen = bits;
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("70001");
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("1"));
        }
        d = e.modInverse(phi);
    }

    public String decrypt(String message){
        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }

    public String encrypt(String message){
        return (new BigInteger(message.getBytes())).modPow(e, n).toString();
    }

    public void generateKeys(){
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
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
}
