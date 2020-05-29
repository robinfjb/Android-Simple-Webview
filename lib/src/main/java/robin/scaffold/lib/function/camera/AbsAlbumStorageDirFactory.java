package robin.scaffold.lib.function.camera;

import java.io.File;

public abstract class AbsAlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
