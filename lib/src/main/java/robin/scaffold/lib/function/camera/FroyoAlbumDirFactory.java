package robin.scaffold.lib.function.camera;

import android.os.Environment;

import java.io.File;


public class FroyoAlbumDirFactory extends AbsAlbumStorageDirFactory {
    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
