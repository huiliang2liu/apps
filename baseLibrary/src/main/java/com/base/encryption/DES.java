package com.base.encryption;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * com.encryption
 * 2018/9/28 11:32
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class DES extends AEncrytion {
    private byte[] key;
    private SecretKey securekey;
    private Exception exception;

    public DES(Key key) {
        // TODO Auto-generated constructor stub
        if (key == null)
            throw new RuntimeException("you key is null");
        this.key = key.getPublicKey().getBytes();
        try {
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(this.key);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            // 将DESKeySpec对象转换成SecretKey对象
            securekey = keyFactory.generateSecret(desKey);
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
        if (vector.length != 8) {
            vector = Arrays.copyOf(vector, 8);
        }
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES_CBC);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, new IvParameterSpec(vector));
        // 现在，获取数据并加密
        // 正式执行加密操作
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
            vector = Arrays.copyOf(vector, 8);
        }
        Cipher cipher = Cipher.getInstance(DES_CBC);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, new IvParameterSpec(vector));
        // 真正开始解密操作
        return cipher.doFinal(text);
    }
}

