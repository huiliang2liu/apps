package com.base.encryption;

/**
 * com.encryption
 * 2018/9/28 11:36
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class EncryptionFactory {
    /**
     * 获取AES加密
     *
     * @param key
     * @return
     */
    public static IEncryption getAES(Key key) {
        return new AES(key);
    }

    /**
     * 获取DES加密
     *
     * @param key
     * @return
     */
    public static IEncryption getDES(Key key) {
        return new DES(key);
    }

    /**
     * 获取RSA公钥加密私钥解密
     *
     * @param key
     * @return
     */
    public static IEncryption getRSAPublic(Key key) {
        return new RSAPublic(key);
    }

    /**
     * 获取RSA私钥加密公钥解密
     *
     * @param key
     * @return
     */
    public static IEncryption getRSAPrivate(Key key) {
        return new RSAPrivate(key);
    }

    /**
     * 获取md5签名
     *
     * @return
     */
    public static ISign getMD5() {
        return new MD5();
    }

    /**
     * description：获取sha1签名
     */
    public static ISign getSha1() {
        return new Sha1();
    }

    /**
     * description：获取sha256签名
     */
    public static ISign getSha256() {
        return new Sha256();
    }

    /**
     * 获取RSA签名
     *
     * @param key
     * @return
     */
    public static ISign getRSASign(Key key) {
        return new RSASign(key);
    }

    public static Key createRSAKey() throws Exception {
        return creatKey2Key(getCreatKey());
    }

    public static Key createAESKey(String key) {
        return new Key(Key.TYPE_AES, key, "");
    }

    public static Key CreateDESKey(String key) {
        return new Key(Key.TYPE_DES, key, "");
    }

    public static CreatKey getCreatKey() throws Exception {
        return new CreatKey();
    }

    public static Key creatKey2Key(CreatKey creatKey) throws Exception {
        return new Key(Key.TYPE_RSA, creatKey.getPublicKeyString(), creatKey.getPrivateKeyString());
    }
}
