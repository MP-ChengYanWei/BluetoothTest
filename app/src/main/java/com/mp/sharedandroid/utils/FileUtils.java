package com.mp.sharedandroid.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 一些和文件、目录读、写、修改等等相关常见操作
 * <p>
 * getSDPath: 获取sd卡路径
 * stringToFile: 字符串写入文件(追加或者覆盖)
 * makeDir: 文件夹创建
 * fileToString: 读取文件内容到String
 * inputToString： 读取InputStream内容到String
 * copyFromAssetsToSdcard： 将工程需要的资源文件拷贝到SD卡中使用
 * deleteFile：删除一个文件
 * deleteAll: 删除本路径：如果是文件，删除文件；如果是目录，会删除本目录所有文件和子目录
 * createDirectoryIfNot: 检查路径是否存在和是否是目录；如果不是，删除（如果是文件），并且建立目录
 * AudioNameValidator: 通过文件名后缀判断是不是一个语音文件
 */
public class FileUtils {
    /**
     * 获取sd卡路径
     *
     * @return sd卡路径
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory(); //获取跟目录
            return sdDir == null ? null : sdDir.toString();
        }
        return null;
    }

    /**
     * 字符串写入文件
     * 如果是覆盖方式写文件，那么此方法是安全，会保证文件的完整性
     * 如果是追加方式写文件，则不是安全的
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否是追加的方式
     */
    public static boolean stringToFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(content)) {
            return false;
        }

        String tempFileName;
        if (append) {
            tempFileName = filePath;
        } else {
            final File file = new File(filePath);
            tempFileName = file.getParent() + "/" + "temp-" + file.getName();
        }

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(tempFileName, append);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        if (append) {
            return safeMove(tempFileName, filePath, false, false);
        } else {
            return safeMove(tempFileName, filePath, true, false);
        }
    }

    /**
     * 文件夹创建
     *
     * @param dirPath
     */
    public static boolean makeDir(String dirPath) {
        final File file = new File(dirPath);
        if (file.exists()) {
            return file.isDirectory();
        } else {
            return file.mkdirs();
        }
    }

    /**
     * 从文本文件中读取内容
     *
     * @param filePath 要读取的文件
     */
    public static String fileToString(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            InputStream is = new FileInputStream(file);
            return inputToString(is);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 读取InputStream内容到一个String。
     *
     * @param is
     * @return 读取的String
     */
    public static String inputToString(InputStream is) {
        try {
            // 空文件会造成 Scanner异常，所以这里先检查
            if (is.available() == 0) {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
        final Scanner scanner = new Scanner(is, "UTF-8");
        final String str = scanner.useDelimiter("\\A").next();
        scanner.close();
        return str;
    }

    /**
     * 将工程需要的资源文件拷贝到SD卡中使用
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    public static boolean copyFromAssetsToSdcard(Context context, boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除一个文件
     *
     * @param path 文件的绝对路径
     * @return
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        boolean result = true;
        if (file.exists()) {
            result = file.delete();
        }
        // 文件不存在也算删除成功
        return result;
    }

    /**
     * 删除本路径：如果是文件，删除文件；如果是目录，会删除本目录所有文件和子目录
     * ＊＊危险操作＊＊
     *
     * @return true 如果操作成功；false如果有地方出错
     */
    public static boolean deleteAll(File path) {
        boolean result = true;
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File child : path.listFiles()) {
                    result &= deleteAll(child);
                }
                result &= path.delete(); // Delete empty directory.
            } else if (path.isFile()) {
                result &= path.delete();
            }
            return result;
        } else {
            return false;
        }
    }

    /**
     * 检查路径是否存在和是否是目录；如果不是，删除（如果是文件），并且建立目录；
     * ＊＊危险操作＊＊
     *
     * @return true 如果操作成功；false如果有地方出错
     */
    public static boolean createDirectoryIfNot(File directory) {
        if (directory.exists() && !directory.isDirectory()) {
            if (!deleteAll(directory)) {
                return false;
            }
        }
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    /**
     * 通过文件名后缀判断是不是一个语音文件
     */
    public static class AudioNameValidator {
        private static final String AUDIO_PATTERN =
                "(^.+(\\.(?i)(mp3|wav|opus))$)";
        private static Pattern PATTERN = Pattern.compile(AUDIO_PATTERN);

        /**
         * Validate audio with regular expression
         *
         * @param audio audio for validation
         * @return true valid audio, false invalid audio
         */
        public static boolean validate(final String audio) {
            return PATTERN.matcher(audio).matches();
        }
    }

    /**
     * 获取文件 MD5 值
     *
     * @param fileName 文件路径
     * @return md5
     */
    public static String getMD5FromFile(String fileName) {
        if (fileName == null) {
            return null;
        }
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * safeMove方法
     * public static boolean safeMove(String src, String dest, boolean overwriteIfExist, boolean backupOverwrite)
     * -- 自动判断 dest是文件还是目录
     * ＊＊ 如果存在而且是目录，还需要判断这个目录下是否有和源文件同名的文件
     * ＊＊ 如果不存在，则当成一个文件，直接move （调用方要保证父目录存在）
     * ＊＊ 如果已经存在，需要判断下面两个参数
     * -- overwriteIfExist 指定如果dest已经存在，是不是overwrite。
     * -- backupOverwrite 指如果要overwrite，是不是要把老的dest存到一个备份文件中
     * ＊＊ move成功，就把备份删除
     * ＊＊ 如果move不成功，看看能不能把备份恢复成原来的文件名
     * ＊＊ 备份文件可以在同一目录，比如 dest.getPath() + ".backup-" + dest.getBaseName()
     * <p>
     * 然后moveFile和renameFile就是safeMove的不同参数调用
     *
     * @param src              源文件路径
     * @param dest             目标文件路径
     * @param overwriteIfExist 是否覆盖
     * @param backupOverwrite  是否备份
     * @return 是否成功
     * <p>
     * TODO：此方法有代码冗余，需要整理，返回值也需要修改成enum
     */
    public static boolean safeMove(String src, String dest, boolean overwriteIfExist, boolean backupOverwrite) {

        if (src == null || dest == null) {
            Log.e("FileUtils", "src == null || dest == null");
            return false;
        }
        File srcFile = new File(src);
        if (!srcFile.exists()) {
            Log.e("FileUtils", "!srcFile.exists()");
            return false;
        }
        File destFile = new File(dest);

        if (!destFile.exists() && destFile.getParentFile().exists() &&                  // 目标文件不存在，但是父目录存在,直接重命名
                destFile.getParentFile().isDirectory()) {
            return srcFile.renameTo(destFile);
        } else if (destFile.exists() && destFile.isDirectory()) {                       // 目标文件存在并且是目录判断目录下是否有和源文件同名的文件
            File tempDestFile = new File(destFile + File.separator + srcFile.getName());
            if (!tempDestFile.exists()) {                                                // 判断目标目录下是否有和源文件名相同的文件
                return srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
            } else {
                if (!overwriteIfExist) {                                                // 目标文件已经存在并且不覆盖，直接返回成功
                    return true;
                } else if (overwriteIfExist && !backupOverwrite) {                      // 目标文件存在并且需要覆盖但是不备份旧文件
                    FileUtils.deleteFile(dest + File.separator + srcFile.getName());    // 目标同名文件删除
                    return srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
                } else if (overwriteIfExist && backupOverwrite) {                       // 目标文件存在并且需要覆盖但是备份旧文件，move 失败恢复旧文件
                    String bachupName = dest + File.separator + "backup-" + destFile.getName();
                    destFile.renameTo(new File(bachupName));                            // 备份目标文件
                    boolean status = srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
                    if (status) {
                        FileUtils.deleteFile(bachupName);                               // 删除备份文件
                        return true;
                    } else {
                        new File(bachupName).renameTo(new File(dest + File.separator + srcFile.getName()));  // 恢复备份文件
                        return false;
                    }
                }
            }
            srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
        } else if (destFile.exists() && destFile.isFile()) {                    // 目标文件存在并且是文件
            if (!overwriteIfExist) {                                            // 目标文件已经存在并且不覆盖，直接返回成功
                return true;
            } else if (overwriteIfExist && !backupOverwrite) {                  // 目标文件存在并且需要覆盖但是不备份旧文件
                FileUtils.deleteFile(dest);                                     // 目标同名文件删除
                return srcFile.renameTo(destFile);
            } else if (overwriteIfExist && backupOverwrite) {                   // 目标文件存在并且需要覆盖但是备份旧文件，move 失败恢复旧文件
                String bachupName = destFile.getParent() + File.separator + "backup-" + destFile.getName();
                destFile.renameTo(new File(bachupName));                        // 备份目标文件
                boolean status = srcFile.renameTo(new File(dest));
                if (status) {
                    FileUtils.deleteFile(bachupName);                           // 删除备份文件
                    return true;
                } else {
                    new File(bachupName).renameTo(new File(dest));              // 恢复备份文件
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取assets指定目录文件名列表
     *
     * @param dir 指定目录名
     * @return 文件名列表
     */
    public static String[] getAssetsFileList(Context context, String dir) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(dir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (String str : files) {
            Log.d("FileUtils", "file:" + str);
        }
        return files;
    }

    /**
     * 保存bitmap到文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bm, String fileDir, String fileName) {
        final File dirFile = new File(fileDir);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        final File captureFile = new File(fileDir + "/" + fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(captureFile));
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != bos) {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 刷新多媒体文件数据
     *
     * @param context
     * @param path
     */
    public static void scanFile(Context context, String path) {
        try {
            MediaScannerConnection.scanFile(context,
                    new String[]{path}, null, null);
        } catch (Exception e) {
            Log.d("scanFile", "e:" + e.getMessage());
        }
    }

    /**
     * 判断文件是否存在，不存在创建
     *
     * @param file
     */
    public static void isFileExistsAndCreate(File file) {
        if (file.exists()) {
            Log.d("fileUtils", "file exists");
        } else {
            Log.d("fileUtils", "file not exists, create it ...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 输入流保存到文件
     *
     * @param inputStream 输入流
     * @param fileName    全路径文件名
     */
    public static boolean inputStreamToFile(InputStream inputStream, String fileName) {
        if (inputStream == null || TextUtils.isEmpty(fileName)) {
            return false;
        }
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            final File file = new File(fileName);
            fos = new FileOutputStream(file);
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            Log.d("FileUtils", "文件下载失败：" + e.getMessage());
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
        return true;
    }

    /**
     * 获取序列化的对象
     *
     * @param fileName 对象全路径
     * @return
     */
    public static Object getObjectFromFile(String fileName) {
        if (fileName == null) {
            return null;
        }
        Object temp = null;
        final File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 对象序列化
     *
     * @param fileName 序列化全路径
     * @param object
     */
    public static void putObjectToFileWithSafe(String fileName, Object object) {
        if (fileName == null || object == null) {
            return;
        }
        final String temp = fileName + "-temp";
        final File file = new File(temp);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(object);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
            return;
        }
        FileUtils.safeMove(temp, fileName, true, false);
    }

    /**
     * 文件信息
     */
    static final class FileInfo {
        protected FileInfo(File file) {
            this.file = file;
            this.length = getFileSizeWithDir(file);
        }

        private static long getFileSizeWithDir(File fileDir) {
            return new File(fileDir.getAbsolutePath() + "/" + fileDir.getName() + ".MP").length();
        }

        private File file;
        private long length;
    }

    /**
     * 对目录列表快速排序
     *
     * @param array 目录列表
     */
    private static List<FileInfo> sortFileList(File[] array) {
        final ArrayList<FileInfo> fileInfoList = new ArrayList<>();
        for (File file : array) {
            fileInfoList.add(new FileInfo(file));
        }
        Collections.sort(fileInfoList, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo lhs, FileInfo rhs) {
                if (lhs.length == rhs.length) {
                    return lhs.toString().compareTo(rhs.toString());
                }
                return (lhs.length > rhs.length) ? 1 : -1;
            }
        });
        return fileInfoList;
    }

    /**
     * 获取目录下指定前缀的第一个文件
     *
     * @param dir    目录名
     * @param prefix 前缀名
     * @return 文件全名称
     */
    public static String getFirstFileNameWithPrefix(String dir, String prefix) {
        if (TextUtils.isEmpty(dir) || TextUtils.isEmpty(prefix)) {
            return null;
        }
        final File fileDir = new File(dir);
        if (fileDir.length() == 0) {
            return null;
        }
        for (File file : fileDir.listFiles()) {
            final String fileName = file.getName();
            if (fileName.startsWith(prefix)) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * 校验文件路径是否真实存在
     * public static final 代码静态分析会有警告提示
     *
     * @param filePath 文件路径
     * @return 是否真实存在
     */
    public static boolean checkFilePath(String filePath) {
        return !TextUtils.isEmpty(filePath) && new File(filePath).exists();
    }

    /**
     * 格式化文件大小
     *
     * @param fileS
     * @return 字符串格式大小
     */
    public static String formetFileSize(long fileS) {
        String result;
        long KB = 1048576;
        long MB = 1073741824;
        long GB = 1;
        DecimalFormat format = new DecimalFormat("#.00");
        if (fileS == 0) {
            result = "0MB";
        } else {
            if (fileS < MB) {
                if (fileS / KB < GB) {
                    format = new DecimalFormat("0.00");
                }

                result = String.valueOf(format.format((((double) fileS)) / KB)) + "MB";
            } else {
                if (fileS / MB < GB) {
                    format = new DecimalFormat("0.00");
                }

                result = String.valueOf(format.format((((double) fileS)) / MB)) + "GB";
            }
        }
        return result;
    }

    /**
     * 检查sdcard
     *
     * @param path
     * @param context
     * @return true or false
     */
    public static boolean checkSDCardMount(String path, Context context) {
        Method method;
        boolean result = false;
        if (path != null) {
            final Object object = context.getSystemService(Context.STORAGE_SERVICE);
            try {
                method = object.getClass().getMethod("getVolumeState", String.class);
                final Object obj = method.invoke(object, path);
                result = "mounted".equals(obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取文件大小
     *
     * @param filepath 文件路径
     * @return 文件大小
     * @throws Exception
     */
    public static long getFileSize(String filepath) throws Exception {
        long result = 0;
        final File[] array = new File(filepath).listFiles();
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                result += array[i].isDirectory() ? FileUtils.getFileSize(array[i].getPath()) : array[i].length();
            }
        }
        return result;
    }
}
