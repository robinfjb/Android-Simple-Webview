package robin.scaffold.lib.function.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import robin.scaffold.lib.util.CommUtil;
import robin.scaffold.lib.util.Constants;
import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;
import robin.scaffold.lib.util.FileUtil;

public class CameraFunction extends BaseFunction implements PermissionCallbackListener {
    private static final String JPEG_FILE_PREFIX = "JPEG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private PermissionInterceptor permissionInterceptor;
    private ICamerCallback iCamerCallback;
    private Uri uri;
    public CameraFunction(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length == 0) {
            if(iCamerCallback != null) {
                iCamerCallback.onResult(null);
            }
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callSafeGoCamera(context);
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            TextView title= new TextView(context);
            title.setText("无法使用相机");
            title.setPadding(10,10,10,10);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(21);
            title.setTextColor(context.getResources().getColor(android.R.color.white));

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setCustomTitle(title)
                    .setMessage("请在对应的权限管理中，将应用“使用相机”的权限修改为允许")
                    .setPositiveButton("知道了", null).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            if(iCamerCallback != null) {
                iCamerCallback.onResult(null);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.ACTIVITY_REQUEST_CODE_OPEN_CAMREA) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(CommUtil.getApp(), "无法获取照片", Toast.LENGTH_LONG).show();
                if(iCamerCallback != null) {
                    iCamerCallback.onResult(null);
                }
                return;
            }
            if(iCamerCallback != null) {
                Uri[] datas = null;
                if(uri != null)  {
                    datas = new Uri[]{uri};
                } else if(data != null){
                    String target = data.getDataString();
                    if (!TextUtils.isEmpty(target)) {
                        datas = new Uri[]{Uri.parse(target)};
                    } else {
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData mClipData = data.getClipData();
                            if (mClipData != null && mClipData.getItemCount() > 0) {
                                datas = new Uri[mClipData.getItemCount()];
                                for (int i = 0; i < mClipData.getItemCount(); i++) {
                                    ClipData.Item mItem = mClipData.getItemAt(i);
                                    datas[i] = mItem.getUri();
                                }
                            }
                        }
                    }
                }
                iCamerCallback.onResult(datas);
            }
        }
    }

    public void goCamera(Context context, ICamerCallback callback){
        iCamerCallback = callback;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            boolean hasPermission=(context.checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
            if(!hasPermission){
                ((Activity)context).requestPermissions(WebPermissionConstant.CAMERA, WebPermissionConstant.REQUESTCODE_CAMERA);
                return;
            }
        } else if(permissionInterceptor != null){
            if(permissionInterceptor.intercept(null, WebPermissionConstant.CAMERA, WebPermissionConstant.REQUESTCODE_CAMERA, this)) {
                return;
            }
        }

        callSafeGoCamera(context);
    }

    private void callSafeGoCamera(final Context context) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    goCameraInMainThread(context);
                }
            });
            return;
        }
        goCameraInMainThread(context);
    }


    private void goCameraInMainThread(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addCategory(Intent.CATEGORY_DEFAULT);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                Toast.makeText(CommUtil.getApp(), "无法打开相机", Toast.LENGTH_LONG).show();
                if(iCamerCallback != null) {
                    iCamerCallback.onResult(null);
                }
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                uri = FileUtil.getUriFromFile(context.getApplicationContext(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                if(context instanceof Activity) {
                    ((Activity)context).startActivityForResult(takePictureIntent, Constants.RequestCode.ACTIVITY_REQUEST_CODE_OPEN_CAMREA);
                } else {
                    context.startActivity(takePictureIntent);
                }
            }
        } else {
            if(iCamerCallback != null) {
                iCamerCallback.onResult(null);
            }
        }
    }



    private File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        File imageF = FileUtil.createFileByName(context.getApplicationContext(), imageFileName, true);
//        File albumF = getAlbumDir(context);
//        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

   private File getAlbumDir(Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            AbsAlbumStorageDirFactory albumStorageDirFactory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                albumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                albumStorageDirFactory = new OtherAlbumDirFactory();
            }
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);//albumStorageDirFactory.getAlbumStorageDir(BuildConfig.FANLI_CACHE_NAME);
            try {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Toast.makeText(CommUtil.getApp(), "无法创建文件", Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            } catch(SecurityException ex) {
                Toast.makeText(CommUtil.getApp(), "SD卡没有读写权限", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(CommUtil.getApp(), "检测不到SD卡", Toast.LENGTH_LONG).show();
        }

        return storageDir;
    }
}
