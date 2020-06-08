package robin.scaffold.lib.function.download;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;
import robin.scaffold.lib.util.FileUtil;
import robin.scaffold.lib.util.MathUtils;
import robin.scaffold.lib.util.NetUtil;

public class DownloadFunc extends BaseFunction implements ExtendDownloadListener, PermissionCallbackListener {
    private PermissionInterceptor permissionInterceptor;
    private Context context;
    private DownloadProgressListener progressListener;
    private IDownloadUi iDownloadUi;
    private static ExecuteTasksMap executeTasksMap = new ExecuteTasksMap();
    private static int taskId = 0;
    private String tmpUrl;
    private String tmpUserAgent;
    private String tmpContentDisposition;
    private long tmpContentLength;
    private Downloader downloader;
    private File file;

    public DownloadFunc(Context context, IDownloadUi iDownloadUi, PermissionInterceptor permissionInterceptor, DownloadProgressListener progressListener) {
        this.permissionInterceptor = permissionInterceptor;
        this.context = context;
        this.progressListener = progressListener;
        this.iDownloadUi = iDownloadUi;
    }


    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (context == null || (((Activity)context).isFinishing())) {
            return;
        }
        tmpUrl = url;
        tmpUserAgent = userAgent;
        tmpContentDisposition = contentDisposition;
        tmpContentLength = contentLength;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            boolean hasPermission=(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                    && (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
            if(!hasPermission){
                ((Activity)context).requestPermissions(WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_DOWNLOAD);
                return;
            }
        } else if(permissionInterceptor != null){
            if(permissionInterceptor.intercept(null, WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_DOWNLOAD, this)) {
                return;
            }
        }
        onDownloadStartInternal(url, contentDisposition, mimetype, contentLength);
    }



    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {

        if(WebPermissionConstant.REQUESTCODE_STORAGE == requestCode) {
            if(grantResults.length == 0) {
                if(progressListener != null) {
                    progressListener.onDownloadError("", tmpUrl, "no grantResults");
                }
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onDownloadStartInternal(tmpUrl, tmpUserAgent, tmpContentDisposition, tmpContentLength);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                TextView title= new TextView(context);
                title.setText("无法使用SD卡");
                title.setPadding(10,10,10,10);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(21);
                title.setTextColor(context.getResources().getColor(android.R.color.white));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setCustomTitle(title)
                        .setMessage("请在对应的权限管理中，将应用“使用SD卡”的权限修改为允许")
                        .setPositiveButton("知道了", null).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if(progressListener != null) {
                    progressListener.onDownloadError("", tmpUrl, "sdcard permission deny");
                }
            }
        } else if(WebPermissionConstant.REQUESTCODE_INSTALL == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                TextView title= new TextView(context);
                title.setText("无法安装");
                title.setPadding(10,10,10,10);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(21);
                title.setTextColor(context.getResources().getColor(android.R.color.white));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setCustomTitle(title)
                        .setMessage("请在对应的权限管理中，将应用“安装应用”的权限修改为允许")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if(progressListener != null) {
                    progressListener.onDownloadError("", tmpUrl, "install permission deny");
                }
            }
        }
    }

    @Override
    public boolean cancelDownload() {
        if(downloader != null) {
            return downloader.cancel(tmpUrl);
        }
        return false;
    }

    private void openMimeTypeFile() {
        Intent intent = new Intent().setAction(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(FileUtil.getUriFromFile(context.getApplicationContext(), file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), FileUtil.getMIMEType(file));
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } catch (Throwable throwable) {
        }
    }


    private void onDownloadStartInternal(String url, String contentDisposition, String mimetype, long contentLength) {
        file = getFile(contentDisposition, url);
        if (file == null)
            return;
        //已下载，打开目录
        if (file.exists() && file.length() >= contentLength) {
            String mimeType = FileUtil.getMIMEType(file);
            if(mimeType.equals("application/vnd.android.package-archive")) {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    boolean hasPermission = context.getPackageManager().canRequestPackageInstalls();
                    if(!hasPermission){
                        ((Activity)context).requestPermissions(WebPermissionConstant.INSTALL, WebPermissionConstant.REQUESTCODE_INSTALL);
                        return;
                    }
                } else if(permissionInterceptor != null){
                    if(permissionInterceptor.intercept(null, WebPermissionConstant.INSTALL, WebPermissionConstant.REQUESTCODE_INSTALL, this)) {
                        return;
                    }
                }
            }
            openMimeTypeFile();
        }


       if (executeTasksMap.contains(url, file.getAbsolutePath())) { //该链接正在下载
           if(iDownloadUi != null)
                iDownloadUi.showTaskRunningWarnMessage();
            return;
        }


        if (NetUtil.checkNetworkType(context) > NetUtil.TYPE_WIFI) { //移动数据
            if(iDownloadUi != null)
                iDownloadUi.showNetWarnMessage(url, contentLength, file, new ForceDownloadCallback() {
                    @Override
                    public void forceDowload(String url, long contentLength, File file) {
                        performDownload(url, contentLength, file);
                    }
                });
            return;
        }
        performDownload(url, contentLength, file);
    }

    private void performDownload(String url, long contentLength, File file) {
        executeTasksMap.addTask(url, file.getAbsolutePath());
        if(iDownloadUi != null)
            iDownloadUi.showStartDownloadMessage(file.getName());
        DownLoadTaskData data = new DownLoadTaskData(++taskId, url, file, contentLength);
        downloader = new Downloader();
        downloader.downloadFile(data, progressListener);
    }

    private File getFile(String contentDisposition, String url) {
        try {
            String fileName = getFileName(contentDisposition);
            if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(url)) {
                Uri mUri = Uri.parse(url);
                fileName = mUri.getPath().substring(mUri.getPath().lastIndexOf('/') + 1);
            }
            if (!TextUtils.isEmpty(fileName)&&fileName.length() > 64) {
                fileName = fileName.substring(fileName.length() - 64, fileName.length());
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = MathUtils.md5(url);
            }
            return FileUtil.createFileByName(context.getApplicationContext(), fileName, false);
        } catch (Throwable e) {
        }

        return null;
    }

    private String getFileName(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) {
            return "";
        }
        Matcher m = Pattern.compile(".*filename=(.*)").matcher(contentDisposition.toLowerCase());
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    public interface ForceDownloadCallback {
        void forceDowload(String url, long contentLength, File file);
    }
}
