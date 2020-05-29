package robin.scaffold.lib.util;

import android.os.Environment;

import java.io.File;


public class SDCardUtil {
    /**
     * check the SD card is prepared
     *
     * @return
     */
    public static boolean checkSDCardMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        if (sdDir != null)
            return sdDir.toString();
        else
            return null;

    }
}
