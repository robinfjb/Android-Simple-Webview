package robin.scaffold.lib.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static String getWebFilePath(Context context){
        File fileCache = context.getExternalCacheDir();
        if(fileCache == null) {
            fileCache = context.getCacheDir();
        }
        File mFile = new File(fileCache.getAbsolutePath(), "robincache");
        try {
            if (!mFile.exists())
                mFile.mkdirs();
        } catch (Throwable throwable) {
        }
        return mFile.getAbsolutePath();
    }

    public static File createFileByName(Context context, String name, boolean cover) throws IOException {
        String path = getWebFilePath(context);
        File finalFile = new File(path, name);
        if (finalFile.exists()) {
            if (cover) {
                finalFile.delete();
                finalFile.createNewFile();
            }
        } else {
            finalFile.createNewFile();
        }
        return finalFile;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriFromFileForN(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static Uri getUriFromFileForN(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        return fileUri;
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
      /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
        /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
        else {
//        /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    private static String InputStreamToString(InputStream in, String encoding, int BUFFER_SIZE) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }
        data = null;
        String out = outStream.toString(encoding);
        try {
            outStream.close();
        } catch (Exception e) {
        }
        return out;
    }

    /**
     * 直接从指定文件读取字符串
     */
    public static String readStringFromFile(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            return InputStreamToString(inputStream, "UTF-8", 4096);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
