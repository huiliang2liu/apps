package com.base.encryption;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * com.encryption
 * 2018/9/28 11:32
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class AES extends AEncrytion {
    private byte[] key;
    private SecretKeySpec keySpec;
    private Exception exception;

    public AES(Key key) {
        // TODO Auto-generated constructor stub
        if (key == null)
            throw new RuntimeException("you key is null");
        this.key = key.getPublicKey().getBytes();
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);
            kgen.init(128, new SecureRandom(this.key));
            keySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), AES);
        } catch (Exception e) {
            // TODO: handle exception
            exception = e;
        }
    }

    @Override
    public byte[] encryption(byte[] text, byte[] vector) throws Exception {
        // TODO Auto-generated method stub
        if (exception != null)
            throw exception;
        if (vector == null)
            vector = key;
        if (vector.length != 16) {
            vector = Arrays.copyOf(vector, 16);
        }
        Cipher cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(vector));
        return cipher.doFinal(text);
    }

    @Override
    public byte[] decryption(byte[] text, byte[] vector) throws Exception {
        // TODO Auto-generated method stub
        if (exception != null)
            throw exception;
        if (vector == null)
            vector = key;
        if (vector.length != 8) {
            vector = Arrays.copyOf(vector, 16);
        }
        Cipher cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(vector));
        return cipher.doFinal(text);
    }
}

