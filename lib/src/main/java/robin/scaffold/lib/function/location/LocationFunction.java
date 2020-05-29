package robin.scaffold.lib.function.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.webkit.GeolocationPermissions;
import android.widget.TextView;

import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;

public class LocationFunction extends BaseFunction implements PermissionCallbackListener {
    private GeolocationPermissions.Callback mCallback = null;
    private String original;
    private PermissionInterceptor permissionInterceptor;
    public LocationFunction(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    public void onGeolocationPermissionsShowPrompt(Context context, String origin, GeolocationPermissions.Callback callback) {
        mCallback = callback;
        original = origin;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ((Activity)context).requestPermissions(WebPermissionConstant.LOCATION, WebPermissionConstant.REQUESTCODE_LOCATION);
            } else {
                callback.invoke(origin, true, false);
            }
        } else {
            if (permissionInterceptor != null) {
                if (permissionInterceptor.intercept(origin, WebPermissionConstant.LOCATION, WebPermissionConstant.REQUESTCODE_LOCATION,  this)){
                    return;
                }
            }
            if (context == null) {
                callback.invoke(origin, false, false);
            } else {
                callback.invoke(origin, true, false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length == 0)
            return;
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mCallback != null && original != null) {
                mCallback.invoke(original, true, false);
            }
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (mCallback != null && original != null) {
                mCallback.invoke(original, false, false);
            }
            TextView title= new TextView(context);
            title.setText("无法使用位置");
            title.setPadding(10,10,10,10);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(21);
            title.setTextColor(context.getResources().getColor(android.R.color.white));

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setCustomTitle(title)
                    .setMessage("请在对应的权限管理中，将应用“使用位置”的权限修改为允许")
                    .setPositiveButton("知道了", null).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}
