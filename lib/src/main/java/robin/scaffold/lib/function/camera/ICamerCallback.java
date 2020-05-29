package robin.scaffold.lib.function.camera;

import android.net.Uri;

import androidx.annotation.Nullable;


public interface ICamerCallback {
    void onResult(@Nullable Uri[] paths);
}
