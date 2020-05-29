package robin.scaffold.lib.function.camera;

import android.os.Environment;

import java.io.File;


public class OtherAlbumDirFactory extends AbsAlbumStorageDirFactory {
    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
