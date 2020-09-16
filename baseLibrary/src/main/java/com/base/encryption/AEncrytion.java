package com.base.encryption;

/**
 * com.encryption
 * 2018/9/28 11:30
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public abstract class AEncrytion implements IEncryption {

    @Override
    public String encryption(String text) throws Exception {
        // TODO Auto-generated method stub
        return encryption(text, null);
    }

    @Override
    public String encryption(String text, String vector) throws Exception {
        // TODO Auto-generated method stub
        return Base64.encodeToString(
                encryption(text.getBytes(),
                        vector == null ? null : vector.getBytes()), 0);
    }

    @Override
    public byte[] encryption(byte[] text) throws Exception {
        // TODO Auto-generated method stub
        return encryption(text, null);
    }

    @Override
    public String decryption(String text) throws Exception {
        // TODO Auto-generated method stub
        return decryption(text, null);
    }

    @Override
    public String decryption(String text, String vector) throws Exception {
        // TODO Auto-generated method stub
        return new String(decryption(Base64.decode(text, 0),
                vector == null ? null : vector.getBytes()));
    }

    @Override
    public byte[] decryption(byte[] text) throws Exception {
        // TODO Auto-generated method stub
        return decryption(text, null);
    }

}
