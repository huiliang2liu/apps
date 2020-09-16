package com.base.encryption;

public class EncryptionUtil {
    private static final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] hexArrayUpperCase = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytes2hex(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len << 1];
        for (int i = 0; i < len; i++) {
            int index = i << 1;
            int v = bytes[i] & 0xff;
            hexChars[index] = hexArray[v >>> 4];
            hexChars[index + 1] = hexArray[v & 0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hexUpperCase(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len << 1];
        for (int i = 0; i < len; i++) {
            int index = i << 1;
            int v = bytes[i] & 0xff;
            hexChars[index] = hexArrayUpperCase[v >>> 4];
            hexChars[index + 1] = hexArrayUpperCase[v & 0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hexHalf(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len];
        for (int i = 0; i < len; i++) {
            hexChars[i] = hexArray[(bytes[i] >>> 4)&0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hexUpperCaseHalf(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len];
        for (int i = 0; i < len; i++) {
            hexChars[i] = hexArrayUpperCase[(bytes[i] >>> 4)&0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hexHalf1(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len];
        for (int i = 0; i < len; i++) {
            hexChars[i] = hexArray[bytes[i] & 0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hexUpperCaseHalf1(byte[] bytes) {
        int len = bytes.length;
        char[] hexChars = new char[len];
        for (int i = 0; i < len; i++) {
            hexChars[i] = hexArrayUpperCase[bytes[i] & 0xf];
        }
        return new String(hexChars);
    }

    public static String bytes2hex1(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String bytes2hexUpperCase1(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
}
