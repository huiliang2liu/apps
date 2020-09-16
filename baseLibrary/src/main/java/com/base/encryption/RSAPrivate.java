package com.base.encryption;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * com.encryption
 * 2018/9/28 11:33
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class RSAPrivate extends AEncrytion {
    private byte[] publicKey;
    private byte[] privateKey;

    public RSAPrivate(Key key) {
        // TODO Auto-generated constructor stub
        if (key == null)
            throw new RuntimeException("you key is null");
        if (key.getType() != Key.TYPE_RSA)
            throw new RuntimeException("you type is wrong");
        publicKey = Base64.decode(key.getPublicKey(), 0);
        privateKey = Base64.decode(key.getPrivateKey(), 0);
    }

    @Override
    public byte[] encryption(byte[] text, byte[] vector) throws Exception {
        // TODO Auto-generated method stub
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        java.security.Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(text);
    }

    @Override
    public byte[] decryption(byte[] text, byte[] vector) throws Exception {
        // TODO Auto-generated method stub
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        java.security.Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(text);
    }

}

