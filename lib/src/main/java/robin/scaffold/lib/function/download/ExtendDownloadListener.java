package robin.scaffold.lib.function.download;

import android.webkit.DownloadListener;


public interface ExtendDownloadListener extends DownloadListener {
    boolean cancelDownload();
}
