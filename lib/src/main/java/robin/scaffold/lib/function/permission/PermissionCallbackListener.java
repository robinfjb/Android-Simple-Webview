package robin.scaffold.lib.function.permission;

import android.content.Context;

public interface PermissionCallbackListener {
    void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);
}
