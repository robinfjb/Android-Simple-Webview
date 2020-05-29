package robin.scaffold.lib.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class Logger {
    public static final int MAX_LOG_FILE_LENGTH = 128*1024;//200k

    public static void writeToFile(String data, String fileName){
        writeToFile(null, data, fileName, MAX_LOG_FILE_LENGTH);
    }

    private static  void writeToFile(String tag, String data, String fileName, final int maxLength){
        if(TextUtils.isEmpty(data)){
            return;
        }
        Date date =new Date();
        String preStr = "Time:" + date.toString() + "\n";
        if(!TextUtils.isEmpty(tag)){
            preStr = preStr + tag + "\n";
        }
        final String logInfo = preStr + data + "\n";
        if (SDCardUtil.checkSDCardMounted()) {
            String sdCardDir = SDCardUtil.getSDPath();
            String filePath = sdCardDir + "/robin/" + fileName;
            final File file = new File( filePath );
            FileWriter fw = null;
            try {
                if (file.exists() ) {
                    if (logInfo.length() + file.length() > maxLength) {
                        if(file.delete()) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        } else {
                            return;
                        }
                    }
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                fw = new FileWriter( file, true );
                fw.write(logInfo);
                fw.flush();
            }catch (IOException e) {

            }catch(OutOfMemoryError e){

            }finally {
                if (fw != null) {
                    try {
                        fw.close();
                    }catch (IOException e1) {
                    }
                }
            }
        }
    }

    public static String getStackTraceString(Throwable t) {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
