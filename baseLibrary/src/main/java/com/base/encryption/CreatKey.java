package com.base.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * com.encryption
 * 2018/9/28 11:30
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class CreatKey {
    RSAPublicKey publicKey;
    RSAPrivateKey privateKey;
    public CreatKey() throws Exception{
        // TODO Auto-generated constructor stub
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance("RSA");
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

// 公钥
        publicKey = (RSAPublicKey) keyPair.getPublic();

// 私钥
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }
    /**
     * 取得私钥
     *
     * @return
     * @throws Exception
     */
    public  String getPrivateKeyString()
            throws Exception {
        return Base64.encodeToString(getPrivateKeyByte(), 0);
    }
    /**
     * 取得私钥
     * @return
     * @throws Exception
     */
    public byte[] getPrivateKeyByte()throws Exception{
        return privateKey.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @return
     * @throws Exception
     */
    public  String getPublicKeyString()
            throws Exception {
        return Base64.encodeToString(getPublicKeybyte(), 0);
    }
    /**
     * 取得公钥
     * @return
     * @throws Exception
     */
    public byte[] getPublicKeybyte() throws Exception{
        return publicKey.getEncoded();
    }
}
