package robin.scaffold.lib.function.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.WebPermissionConstant;

public class CameraHelper {
    public static void showPermissionAlert(final Activity activity, String url, String[] permissions, final PermissionCallbackListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("权限申请");
        builder.setMessage(url + "允许打开您的相机吗？").setCancelable(true).setPositiveButton("允许",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onRequestPermissionsResult(activity, WebPermissionConstant.REQUESTCODE_CAMERA, WebPermissionConstant.CAMERA, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }).setNegativeButton("不允许",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onRequestPermissionsResult(activity, WebPermissionConstant.REQUESTCODE_CAMERA, WebPermissionConstant.CAMERA, new int[]{PackageManager.PERMISSION_DENIED});
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
