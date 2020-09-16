package com.base.encryption;

/**
 * com.encryption
 * 2018/9/28 11:31
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class Key {
    public static final int TYPE_AES = 1;
    public static final int TYPE_DES = 2;
    public static final int TYPE_RSA = 3;
    private String publicKey;
    private String privateKey;
    private int type = -1;
    private final static String MODE_KEY = "01234567";

    public Key(int type, String publicKey, String privateKey) {
        // TODO Auto-generated constructor stub
        if (type == TYPE_RSA)
            throw new RuntimeException("you private key is empty");
        int mode = publicKey.length() % 8;
        this.publicKey = type == TYPE_DES ? mode == 0 ? publicKey : publicKey
                + MODE_KEY.substring(0, 8 - mode) : publicKey;
        this.privateKey = privateKey;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
