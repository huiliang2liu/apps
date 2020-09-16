package com.io.sava;



import com.io.StreamUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 2018/7/30 10:42
 * instructions：文件管理
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class FileUtil {
    /**
     * instruction:判断文件为空 2018-6-7 下午3:26:37
     *
     * @param file
     * @return
     */
    public static boolean isEmpty(File file) {
        if (file == null) {
            System.out.println("file is null");
            return true;
        }
        if (!file.exists()) {
            System.out.println("file is not exists");
            return true;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                System.out.println("file is directory,but listFiles is null");
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * instruction:将文件转化为输出流 2018-6-7 下午12:06:06
     *
     * @param file
     * @return
     */
    public static FileInputStream file2inputStream(File file) {
        if (isEmpty(file))
            return null;
        if (file.isDirectory())
            return null;
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    /**
     * description：文件转化为string
     */
    public static String file2string(File file) {
        FileInputStream fileInputStream = file2inputStream(file);
        if (fileInputStream == null)
            return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * instruction:将文件转化为输出流 2018-6-7 下午12:35:09
     *
     * @param file
     * @return
     */
    public static FileInputStream file2inputStream(String file) {
        if (file == null || file.isEmpty())
            return null;
        return file2inputStream(new File(file));
    }

    /**
     * instruction:将文件转化为输入流 2018-6-7 下午12:12:57
     *
     * @param file
     * @param add  是否是添加数据
     * @return
     */
    public static FileOutputStream file2outputStream(File file, boolean add) {
        if (file == null)
            return null;
        File parent = file.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        try {
            return new FileOutputStream(file, add);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            try {
                return new FileOutputStream(file, add);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    /**
     * instruction:将文件转化为输入流 2018-6-7 下午12:36:34
     *
     * @param file
     * @param add  是否是添加数据
     * @return
     */
    public static FileOutputStream file2outputStream(String file, boolean add) {
        if (file == null || file.isEmpty())
            return null;
        return file2outputStream(new File(file), add);
    }

    /**
     * instruction:将文件转化为输入流 2018-6-7 下午12:14:16
     *
     * @param file
     * @return
     */
    public static FileOutputStream file2outputStream(File file) {
        return file2outputStream(file, false);
    }

    /**
     * instruction:将文件转化为输入流 2018-6-7 下午12:37:26
     *
     * @param file
     * @return
     */
    public static FileOutputStream file2outputStream(String file) {
        return file2outputStream(file, false);
    }

    /**
     * instruction:复制文件夹 2018-6-7 下午5:10:38
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean copyDirectory(File source, File target) {
        if (isEmpty(source))
            return false;
        if (source.isDirectory()) {
            boolean success = true;
            List<File> files = listAll(source);
            if (files == null || files.size() <= 0)
                return false;
            int len = files.size();
            for (int i = 0; i < len; i++) {
                File file = files.get(i);
                String fileName = target.getAbsolutePath()
                        + file.getAbsolutePath().substring(
                        source.getAbsolutePath().length());
                success &= copyFile(file, new File(fileName));
            }
            return success;
        }
        return false;

    }

    public static boolean copyDirectory(File source, File target,
                                        String[] contains) {
        if (isEmpty(source))
            return false;
        if (source.isDirectory()) {
            boolean success = true;
            List<File> files = listAll(source, contains);
            if (files == null || files.size() <= 0)
                return false;
            int len = files.size();
            for (int i = 0; i < len; i++) {
                File file = files.get(i);
                String fileName = target.getAbsolutePath()
                        + file.getAbsolutePath().substring(
                        source.getAbsolutePath().length());
                success &= copyFile(file, new File(fileName));
            }
            return success;
        }
        return false;

    }

    public static boolean copyDirectoryStart(File source, File target,
                                             String[] starts) {
        if (isEmpty(source))
            return false;
        if (source.isDirectory()) {
            boolean success = true;
            List<File> files = listAllStart(source, starts);
            if (files == null || files.size() <= 0)
                return false;
            int len = files.size();
            for (int i = 0; i < len; i++) {
                File file = files.get(i);
                String fileName = target.getAbsolutePath()
                        + file.getAbsolutePath().substring(
                        source.getAbsolutePath().length());
                success &= copyFile(file, new File(fileName));
            }
            return success;
        }
        return false;

    }

    public static boolean copyDirectoryEdn(File source, File target,
                                           String[] ends) {
        if (isEmpty(source))
            return false;
        if (source.isDirectory()) {
            boolean success = true;
            List<File> files = listAllEnd(source, ends);
            if (files == null || files.size() <= 0)
                return false;
            int len = files.size();
            for (int i = 0; i < len; i++) {
                File file = files.get(i);
                String fileName = target.getAbsolutePath()
                        + file.getAbsolutePath().substring(
                        source.getAbsolutePath().length());
                success &= copyFile(file, new File(fileName));
            }
            return success;
        }
        return false;

    }

    /**
     * instruction:复制文件 2018-6-7 下午12:44:16
     *
     * @param source 被复制的文件
     * @param target 存储文件
     * @return
     */
    public static boolean copyFile(File source, File target) {
        if (isEmpty(source))
            return false;
        FileInputStream fis = file2inputStream(source);
        if (fis == null)
            return false;
        FileOutputStream fos = file2outputStream(target);
        if (fos == null) {
            StreamUtil.close(fis);
            return false;
        }
        boolean success = false;
        FileChannel input = fis.getChannel();
        FileChannel output = fos.getChannel();
        try {
            output.transferFrom(input, 0, source.length());
            success = true;
            fos.flush();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            StreamUtil.close(output);
            StreamUtil.close(fos);
            StreamUtil.close(input);
            StreamUtil.close(fis);
        }
        return success;
    }

    /**
     * instruction: 复制 2018-6-7 下午5:11:59
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean copy(File source, File target) {
        if (isEmpty(source))
            return false;
        if (source.isDirectory())
            return copyDirectory(source, target);
        return copyFile(source, target);
    }

    /**
     * instruction:往文件添加内容 2018-6-7 下午2:34:59
     *
     * @param is     输入流
     * @param target 目标文件
     * @return
     */
    public static boolean add(InputStream is, File target) {
        if (is == null)
            return false;
        FileOutputStream fos = file2outputStream(target, true);
        if (fos == null) {
            StreamUtil.close(is);
            return false;
        }
        boolean success = false;
        try {
            byte[] buff = new byte[1024 * 1024];
            int len = is.read(buff);
            while (len > 0) {
                fos.write(buff, 0, len);
                len = is.read(buff);
            }
            success = true;
            fos.flush();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            StreamUtil.close(fos);
            StreamUtil.close(is);
        }
        return success;
    }

    /**
     * instruction:往文件添加内容 2018-6-7 下午2:40:26
     *
     * @param buff   添加的字节数组
     * @param target 目标文件
     * @return
     */
    public static boolean add(byte[] buff, File target) {
        if (buff == null || buff.length <= 0)
            return false;
        FileOutputStream fos = file2outputStream(target, true);
        if (fos == null)
            return false;
        boolean success = false;
        try {
            fos.write(buff);
            success = true;
            fos.flush();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            StreamUtil.close(fos);
        }
        return success;
    }

    /**
     * instruction:往文件添加内容 2018-6-7 下午2:56:32
     *
     * @param string
     * @param target 目标文件
     * @return
     */
    public static boolean add(String string, File target) {
        if (string == null || string.isEmpty())
            return false;
        try {
            return add(string.getBytes("utf-8"), target);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    /**
     * instruction:往文件添加内容 2018-6-7 下午2:56:21
     *
     * @param source 源文件
     * @param target 目标文件
     * @return
     */
    public static boolean add(File source, File target) {
        FileInputStream fis = file2inputStream(source);
        if (fis == null)
            return false;
        FileOutputStream fos = file2outputStream(target, true);
        if (fos == null) {
            StreamUtil.close(fis);
            return false;
        }
        boolean success = false;
        FileChannel input = fis.getChannel();
        FileChannel output = fos.getChannel();
        try {
            output.transferFrom(input, target.length(), source.length());
            success = true;
            fos.flush();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            StreamUtil.close(output);
            StreamUtil.close(fos);
            StreamUtil.close(input);
            StreamUtil.close(fis);

        }
        return success;
    }

    /**
     * instruction:查找目录下的所有文件,包括子目录的 2018-6-7 下午3:35:46
     *
     * @param target 目标目录
     * @return
     */
    public static List<File> listAll(File target) {
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            List<File> list = new ArrayList<File>();
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isEmpty(file))
                    continue;
                if (file.isDirectory()) {
                    List<File> list2 = listAll(file);
                    if (list2 != null && list2.size() > 0)
                        list.addAll(list2);

                } else
                    list.add(file);
            }
            return list;
        }
        return null;
    }

    /**
     * instruction:包含某写字段的文件 2018-6-7 下午5:00:17
     *
     * @param target
     * @param contains 包含的字段几个
     * @return
     */
    public static List<File> listAll(File target, String[] contains) {
        if (contains == null || contains.length <= 0)
            listAll(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            List<File> list = new ArrayList<File>();
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isEmpty(file))
                    continue;
                if (file.isDirectory()) {
                    List<File> list2 = listAll(file, contains);
                    if (list2 != null && list2.size() > 0)
                        list.addAll(list2);

                } else {
                    for (int j = 0; j < contains.length; j++) {
                        if (file.getName().contains(contains[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public static List<File> listAllStart(File target, String[] starts) {
        if (starts == null || starts.length <= 0)
            listAll(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            List<File> list = new ArrayList<File>();
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isEmpty(file))
                    continue;
                if (file.isDirectory()) {
                    List<File> list2 = listAllStart(file, starts);
                    if (list2 != null && list2.size() > 0)
                        list.addAll(list2);

                } else {
                    for (int j = 0; j < starts.length; j++) {
                        if (file.getName().startsWith(starts[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public static List<File> listAllEnd(File target, String[] ends) {
        if (ends == null || ends.length <= 0)
            listAll(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            List<File> list = new ArrayList<File>();
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isEmpty(file))
                    continue;
                if (file.isDirectory()) {
                    List<File> list2 = listAllEnd(file, ends);
                    if (list2 != null && list2.size() > 0)
                        list.addAll(list2);

                } else {
                    for (int j = 0; j < ends.length; j++) {
                        if (file.getName().endsWith(ends[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        }
        return null;
    }

    /**
     * instruction:查找该目录下的文件 2018-6-7 下午3:39:16
     *
     * @param target
     * @return
     */
    public static List<File> list(File target) {
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            int len = files.length;
            List<File> list = new ArrayList<File>(len);
            for (int i = 0; i < len; i++) {
                File file = files[i];
                if (file.isFile())
                    list.add(file);
            }
            return list;
        } else
            return null;
    }

    /**
     * instruction:查找该目录下以什么结尾的文件 2018-6-7 下午4:53:13
     *
     * @param target
     * @param ends   结尾符集合
     * @return
     */
    public static List<File> listEnd(File target, String[] ends) {
        if (ends == null || ends.length <= 0)
            return list(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            int len = files.length;
            List<File> list = new ArrayList<File>(len);
            for (int i = 0; i < len; i++) {
                File file = files[i];
                if (file.isFile()) {
                    for (int j = 0; j < ends.length; j++) {
                        if (file.getName().endsWith(ends[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        } else
            return null;

    }

    /**
     * instruction:查找该目录下包含什么的文件 2018-6-7 下午4:53:13
     *
     * @param target 包含符集合
     * @return
     */
    public static List<File> list(File target, String[] contains) {
        if (contains == null || contains.length <= 0)
            return list(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            int len = files.length;
            List<File> list = new ArrayList<File>(len);
            for (int i = 0; i < len; i++) {
                File file = files[i];
                if (file.isFile()) {
                    for (int j = 0; j < contains.length; j++) {
                        if (file.getName().contains(contains[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        } else
            return null;

    }

    /**
     * instruction:查找该目录下以什么开头的文件 2018-6-7 下午4:53:13
     *
     * @param target 开头符集合
     * @return
     */
    public static List<File> listStart(File target, String[] starts) {
        if (starts == null || starts.length <= 0)
            return list(target);
        if (isEmpty(target))
            return null;
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            int len = files.length;
            List<File> list = new ArrayList<File>(len);
            for (int i = 0; i < len; i++) {
                File file = files[i];
                if (file.isFile()) {
                    for (int j = 0; j < starts.length; j++) {
                        if (file.getName().startsWith(starts[j]))
                            list.add(file);
                    }
                }
            }
            return list;
        } else
            return null;

    }

    /**
     * 获取文件的编码格式
     *
     * @return
     */
    public static String getCharset(File file) {
        String code = "UTF-8";
        try {
            BufferedInputStream bin = new BufferedInputStream(
                    new FileInputStream(file));
            int p = (bin.read() << 8) + bin.read();

            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return code;
    }

    public static boolean insert(InputStream is, File target, long startWith) {
        return insert(StreamUtil.stream2bytes(is), target, startWith);
    }

    public static boolean insert(String string, String charset, File target,
                                 long startWith) {
        try {
            return insert(string.getBytes(charset), target, startWith);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    public static boolean insert(String string, File target, long startWith) {
        return insert(string, getCharset(target), target, startWith);
    }

    public static boolean insert(File soure, File target, long startWith) {
        return insert(file2inputStream(soure), target, startWith);
    }

    public static boolean insert(byte[] buff, File target, long startWith) {
        if (buff == null || buff.length <= 0)
            return false;
        if (isEmpty(target))
            return false;
        if (target.isDirectory())
            return false;
        if (startWith < 0)
            startWith = 0;
        RandomAccessFile accessFile = null;
        byte[] buf = new byte[(int) (target.length() - startWith)];
        boolean success = false;
        try {
            accessFile = new RandomAccessFile(target, "rw");
            accessFile.seek(startWith);
            accessFile.read(buf);
            accessFile.seek(startWith);
            accessFile.write(buff);
            accessFile.write(buf);
            success = true;
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (accessFile != null)
                StreamUtil.close(accessFile);
        }
        return success;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        if (file == null || !file.exists())
            return false;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (file != null && file.length() > 0) {
                for (File file2 : files) {
                    delete(file2);
                }
            }
            file.delete();
        } else
            file.delete();
        return true;
    }

    /**
     * 是否为文件
     *
     * @param file
     * @return
     */
    public static boolean isFile(File file) {
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        return true;
    }

    /**
     * instruction:获取文件大小 2018-6-29 上午9:51:30
     *
     * @param file
     * @return
     */
    public static long size(File file) {
        if (file != null)
            return 0;
        if (!file.exists())
            return 0;
        long size = 0;
        if (file.isFile())
            size = file.length();
        else {
            File[] files = file.listFiles();
            for (File file2 : files) {
                size += size(file2);
            }
        }
        return size;
    }

    public static String toString(File file) {
        if (isEmpty(file))
            return null;
        if (file.isDirectory())
            return null;
        return StreamUtil.byte2string(
                StreamUtil.stream2bytes(file2inputStream(file)),
                getCharset(file));
    }

    public static File[] ascendTime(File[] files) {
        return sort(files, new Comparator<File>() {

            @Override
            public int compare(File arg0, File arg1) {
                // TODO Auto-generated method stub
                long poor = arg1.lastModified() - arg0.lastModified();
                return poor > 0 ? 1 : poor < 0 ? -1 : 0;
            }
        });
    }

    public static File[] sortTime(File[] files) {
        return sort(files, new Comparator<File>() {

            @Override
            public int compare(File arg0, File arg1) {
                // TODO Auto-generated method stub
                long poor = arg1.lastModified() - arg0.lastModified();
                return poor < 0 ? 1 : poor > 0 ? -1 : 0;
            }
        });
    }

    public static File[] ascendSize(File[] files) {
        return sort(files, new Comparator<File>() {

            @Override
            public int compare(File arg0, File arg1) {
                // TODO Auto-generated method stub
                long poor = size(arg1) - size(arg0);
                return poor > 0 ? 1 : poor < 0 ? -1 : 0;
            }
        });
    }

    public static File[] sortSize(File[] files) {
        return sort(files, new Comparator<File>() {

            @Override
            public int compare(File arg0, File arg1) {
                // TODO Auto-generated method stub
                long poor = size(arg0) - size(arg1);
                return poor > 0 ? 1 : poor < 0 ? -1 : 0;
            }
        });
    }

    public static File[] sort(File[] files, Comparator<File> comparator) {
        if (files == null || files.length <= 0)
            return files;
        File[] files2 = Arrays.copyOf(files, files.length);
        Arrays.sort(files2, comparator);
        return files2;
    }
}
