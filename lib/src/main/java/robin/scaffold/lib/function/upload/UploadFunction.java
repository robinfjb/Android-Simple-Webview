package robin.scaffold.lib.function.upload;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.webkit.ValueCallback;
import android.widget.TextView;

import androidx.annotation.Nullable;

import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.function.camera.CameraFunction;
import robin.scaffold.lib.function.camera.ICamerCallback;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;
import robin.scaffold.lib.util.Constants;


public class UploadFunction extends BaseFunction implements PermissionCallbackListener {
    private Context context;
    private PermissionInterceptor permissionInterceptor;
    private ValueCallback valueCallback;
    private IUploadUi iUploadUi;
    private CameraFunction cameraFunc;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    public UploadFunction(Context context, PermissionInterceptor permissionInterceptor, IUploadUi iUploadUi) {
        this.permissionInterceptor = permissionInterceptor;
        this.iUploadUi = iUploadUi;
        this.context = context;
    }

    public void openFileChooser(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSafeOpenFileChooser();
            return;
        }
        callOpenFileChooserInMainThread();
    }

    private void callSafeOpenFileChooser() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callOpenFileChooserInMainThread();
            }
        });
    }

    private void callOpenFileChooserInMainThread() {
        iUploadUi.showChooser(new String[]{"相机", "文件选择器"}, new IChooserResult(){
            @Override
            public void onChoose(int which) {
                if(which == 0) {
                    openCamera();
                } else {
                    openFile();
                }
            }

            @Override
            public void onCancel() {
                if(valueCallback != null) {
                    valueCallback.onReceiveValue(null);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if(WebPermissionConstant.REQUESTCODE_CAMERA == requestCode) {
            if(cameraFunc != null) {
                cameraFunc.onRequestPermissionsResult(context,  requestCode, permissions, grantResults);
            }
        } else if(WebPermissionConstant.REQUESTCODE_STORAGE == requestCode) {
            if(grantResults.length == 0) {
                if(valueCallback != null) {
                    valueCallback.onReceiveValue(null);
                }
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callSafeOpenFile();
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
                if(valueCallback != null) {
                    valueCallback.onReceiveValue(null);
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RequestCode.ACTIVITY_REQUEST_CODE_OPEN_CAMREA && cameraFunc != null) {
            cameraFunc.onActivityResult(requestCode, resultCode, data);
        } else if(requestCode == Constants.RequestCode.ACTIVITY_REQUEST_CODE_CHOOSE_FILE) {
            Uri[] datas = null;
            if(data != null) {
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
            if(valueCallback != null) {
                valueCallback.onReceiveValue(datas == null ? new Uri[]{} : datas);
            }
        }
    }

    private void openCamera() {
        cameraFunc = new CameraFunction(permissionInterceptor);
        cameraFunc.goCamera(context, new ICamerCallback() {
            @Override
            public void onResult(@Nullable Uri[] paths) {
                if(valueCallback != null) {
                    valueCallback.onReceiveValue(paths);
                }
            }
        });
    }

    private void openFile() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            boolean hasPermission=(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                    && (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
            if(!hasPermission){
                ((Activity)context).requestPermissions(WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_STORAGE);
                return;
            }
        } else if(permissionInterceptor != null){
            if(permissionInterceptor.intercept(null, WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_STORAGE, this)) {
                return;
            }
        }
        callSafeOpenFile();
    }

    private void callSafeOpenFile() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    openFileInMainThread();
                }
            });
            return;
        }
        openFileInMainThread();
    }

    private void openFileInMainThread() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(context instanceof Activity) {
            ((Activity)context).startActivityForResult(Intent.createChooser(i, ""), Constants.RequestCode.ACTIVITY_REQUEST_CODE_CHOOSE_FILE);
        } else {
            context.startActivity(Intent.createChooser(i, ""));
        }
    }
}
