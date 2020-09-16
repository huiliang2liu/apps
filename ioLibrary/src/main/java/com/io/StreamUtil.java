package com.io;

import com.io.sava.FileUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * 2018/7/30 10:40
 * instructions：数据流工具
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class StreamUtil {
    private final static int LENGTH = 1024 * 1024;
    private final static String CHARSET = "utf-8";

    /**
     * instruction:将输入流转化为字节数组 2018-6-7 上午11:26:12
     *
     * @param is
     * @return
     */
    public static byte[] stream2bytes(InputStream is) {
        if (is == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[LENGTH];
        byte[] arr = null;
        try {
            int len = is.read(buff);
            while (len > 0) {
                baos.write(buff, 0, len);
                len = is.read(buff);
            }
            arr = baos.toByteArray();
            baos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            close(baos);
            close(is);
        }
        return arr;
    }

    /**
     * instruction:将输入流转化为string 2018-6-7 上午11:29:39
     *
     * @param is
     * @param charset 编码
     * @return
     */
    public static String stream2string(InputStream is, String charset) {
        byte[] buff = stream2bytes(is);
        if (buff == null || buff.length <= 0)
            return null;
        if (charset == null || charset.isEmpty())
            charset = CHARSET;
        try {
            return new String(buff, charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * instruction:将输入流转化为string 2018-6-7 上午11:30:26
     *
     * @param is
     * @return
     */
    public static String stream2string(InputStream is) {
        return stream2string(is, CHARSET);
    }

    /**
     * instruction:将数据流保存到文件中 2018-6-7 上午11:45:50
     *
     * @param is
     * @param file 保存的文件
     * @return
     */
    public static boolean stream2file(InputStream is, File file) {
        if (is == null || file == null) {
            return false;
        }
        File target = new File(file.getParentFile(), System.currentTimeMillis() + "");
        FileOutputStream fos = FileUtil.file2outputStream(target);
        if (fos == null) {
            return false;
        }

        boolean success = false;
        try {
            byte[] buff = new byte[LENGTH];
            int len = is.read(buff);
            while (len > 0) {
                fos.write(buff, 0, len);
                len = is.read(buff);
            }
            success = target.renameTo(file);
            fos.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            close(fos);
            close(is);
        }
        return success;
    }

    /**
     * instruction: 2018-6-7 上午11:56:05
     *
     * @param is
     * @param file 文件名
     * @return
     */
    public static boolean stream2file(InputStream is, String file) {
        return stream2file(is, new File(file));
    }

    /**
     * instruction: 将输入流复制到输入流 2018-6-7 下午12:00:09
     *
     * @param is 输入流
     * @param os 输出流
     * @return
     */
    public static boolean copy(InputStream is, OutputStream os) {
        if (is == null || os == null)
            return false;
        boolean success = false;
        try {
            byte[] buff = new byte[LENGTH];
            int len = is.read(buff);
            while (len > 0) {
                os.write(buff, 0, len);
                len = is.read(buff);
            }
            os.flush();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            close(os);
            close(is);
        }
        return success;
    }

    /**
     * instruction:将字节数组转化为输入流 2018-6-7 上午11:35:27
     *
     * @param buff
     * @return
     */
    public static InputStream byte2stream(byte[] buff) {
        if (buff == null || buff.length <= 0)
            return null;
        return new ByteArrayInputStream(buff);
    }

    public static String byte2string(byte[] buff) {
        if (buff == null || buff.length <= 0)
            return null;
        return new String(buff);
    }

    public static String byte2string(byte[] buff, String charset) {
        if (buff == null || buff.length <= 0)
            return null;
        if (charset == null || charset.isEmpty())
            charset = CHARSET;
        try {
            return new String(buff, charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * instruction:将字符串转化为输入流 2018-6-7 上午11:38:08
     *
     * @param string
     * @param charset 编码
     * @return
     */
    public static InputStream string2stream(String string, String charset) {
        if (string == null || string.isEmpty())
            return null;
        if (charset == null || charset.isEmpty())
            charset = CHARSET;
        try {
            return byte2stream(string.getBytes(charset));
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    /**
     * 读取流中前面的字符，看是否有bom，如果有bom，将bom头先读掉丢弃
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(InputStream in) throws IOException {

        PushbackInputStream testin = new PushbackInputStream(in);
        int ch = testin.read();
        if (ch != 0xEF) {
            testin.unread(ch);
        } else if ((ch = testin.read()) != 0xBB) {
            testin.unread(ch);
            testin.unread(0xef);
        } else if ((ch = testin.read()) != 0xBF) {
            throw new IOException("错误的UTF-8格式文件");
        } else {
            // 不需要做，这里是bom头被读完了
            // System.out.println("still exist bom");
        }
        return testin;

    }

    /**
     * instruction:将字符串转化为输入流 2018-6-7 上午11:39:00
     *
     * @param string
     * @return
     */
    public static InputStream string2stream(String string) {
        return string2stream(string, CHARSET);
    }

    public static String read2string(Reader reader) {
        if (reader == null)
            return "";
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                line.trim();
                if (line.isEmpty())
                    continue;
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        close(br);
        close(reader);
        return sb.toString();
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
