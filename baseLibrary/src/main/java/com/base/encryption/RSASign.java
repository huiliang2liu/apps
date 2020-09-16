package com.base.encryption;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * com.encryption
 * 2018/9/28 11:35
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class RSASign implements ISign {
    private byte[] publicKey;
    private byte[] privateKey;
    public RSASign(Key key) {
        // TODO Auto-generated constructor stub
        if(key==null)
            throw new RuntimeException("you key is null");
        if(key.getType()!=Key.TYPE_RSA)
            throw new RuntimeException("you type is wrong");
        publicKey=Base64.decode(key.getPublicKey(), 0);
        privateKey=Base64.decode(key.getPrivateKey(), 0);
    }

    @Override
    public String sign(String text) throws Exception{
        // TODO Auto-generated method stub
        return Base64.encodeToString(sign(text.getBytes()), 0);
    }

    @Override
    public byte[] sign(byte[] text) throws Exception{
        // TODO Auto-generated method stub
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(priKey);
        signature.update(text);

        return signature.sign();
    }

    @Override
    public boolean verify(String text, String signText) throws Exception{
        // TODO Auto-generated method stub
        return verify(text.getBytes(), Base64.decode(signText, 0));
    }

    @Override
    public boolean verify(byte[] text, byte[] signText) throws Exception{
        // TODO Auto-generated method stub
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(pubKey);
        signature.update(text);

        // 验证签名是否正常
        return signature.verify(signText);
    }

}
