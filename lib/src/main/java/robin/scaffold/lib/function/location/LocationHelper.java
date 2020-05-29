package robin.scaffold.lib.function.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.WebPermissionConstant;


public class LocationHelper {
    public static void showPermissionAlert(final Activity activity, String url, String[] permissions, final PermissionCallbackListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("权限申请");
        builder.setMessage(url + "允许获取您的地理位置信息吗？").setCancelable(true).setPositiveButton("允许",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onRequestPermissionsResult(activity, WebPermissionConstant.REQUESTCODE_LOCATION, WebPermissionConstant.LOCATION, new int[]{PackageManager.PERMISSION_GRANTED});
                    }
                }).setNegativeButton("不允许",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onRequestPermissionsResult(activity, WebPermissionConstant.REQUESTCODE_LOCATION, WebPermissionConstant.LOCATION, new int[]{PackageManager.PERMISSION_DENIED});
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
