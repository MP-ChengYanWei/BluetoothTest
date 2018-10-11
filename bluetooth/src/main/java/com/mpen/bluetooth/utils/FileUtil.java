package com.mpen.bluetooth.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mpen on 2018/9/8.
 */

public class FileUtil {

    /****************************蓝牙测试********************************/
    /**
     * 蓝牙测试数据文件保存路径
     */
    private static String filePath = TextUtils.isEmpty(getSDPath()) ? null : getSDPath() + "/BluetoothTestDataLog.txt";

    public static void bluetoothDataWrite(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("毫秒值：" + System.currentTimeMillis() + "----");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "----");
        sb.append(msg).append("\r\n");
        stringToFile(filePath, sb.toString(), true);
    }
    /****************************蓝牙测试********************************/

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
                    deleteFile(dest + File.separator + srcFile.getName());    // 目标同名文件删除
                    return srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
                } else if (overwriteIfExist && backupOverwrite) {                       // 目标文件存在并且需要覆盖但是备份旧文件，move 失败恢复旧文件
                    String bachupName = dest + File.separator + "backup-" + destFile.getName();
                    destFile.renameTo(new File(bachupName));                            // 备份目标文件
                    boolean status = srcFile.renameTo(new File(dest + File.separator + srcFile.getName()));
                    if (status) {
                        deleteFile(bachupName);                               // 删除备份文件
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
                deleteFile(dest);                                     // 目标同名文件删除
                return srcFile.renameTo(destFile);
            } else if (overwriteIfExist && backupOverwrite) {                   // 目标文件存在并且需要覆盖但是备份旧文件，move 失败恢复旧文件
                String bachupName = destFile.getParent() + File.separator + "backup-" + destFile.getName();
                destFile.renameTo(new File(bachupName));                        // 备份目标文件
                boolean status = srcFile.renameTo(new File(dest));
                if (status) {
                    deleteFile(bachupName);                           // 删除备份文件
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

}
