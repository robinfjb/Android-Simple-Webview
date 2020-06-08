package robin.scaffold.lib.function.permission;

import android.Manifest;


public class WebPermissionConstant {
    public static final String[] CAMERA;
    public static final String[] LOCATION;
    public static final String[] STORAGE;
    public static final String[] INSTALL;

    static {
        CAMERA = new String[]{
                Manifest.permission.CAMERA};

        LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        INSTALL = new String[] {
                Manifest.permission.REQUEST_INSTALL_PACKAGES
        };
    }

    public static final int REQUESTCODE_LOCATION = 1;
    public static final int REQUESTCODE_CAMERA = 1 << 1;
    public static final int REQUESTCODE_STORAGE = 1 << 2;
    public static final int REQUESTCODE_DOWNLOAD = 1 << 3;
    public static final int REQUESTCODE_INSTALL = 1 << 4;
}
