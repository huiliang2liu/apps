package com.base.encryption;

import com.base.util.L;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Message implements ISign {
    private static final String TAG = "Message";
    MessageDigest md5;

    protected Message(String name) {
        if (name == null || name.isEmpty())
            name = "MD5";
        L.d(TAG, name);
        try {
            md5 = MessageDigest.getInstance(name);
        } catch (Exception e) {
            if (name.equals("SHA-1"))
                try {
                    md5 = MessageDigest.getInstance("SHA1");
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
            else if (name.equals("SHA-256"))
                try {
                    md5 = MessageDigest.getInstance("SHA256");
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
        }
    }

    @Override
    public final String sign(String text) {
        // TODO Auto-generated method stub
        md5.update(text.getBytes());
        return EncryptionUtil.bytes2hex(md5.digest());
    }

    @Override
    public final byte[] sign(byte[] text) {
        // TODO Auto-generated method stub
        md5.update(text);
        return EncryptionUtil.bytes2hex(md5.digest()).getBytes();
    }

    @Override
    public final boolean verify(String text, String signText) {
        // TODO Auto-generated method stub
        return sign(text).equals(signText);
    }

    @Override
    public final boolean verify(byte[] text, byte[] signText) {
        // TODO Auto-generated method stub
        return verify(new String(text), new String(signText));
    }
}
