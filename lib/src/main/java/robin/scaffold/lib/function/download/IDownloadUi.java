package robin.scaffold.lib.function.download;

import java.io.File;


public interface IDownloadUi {
    void showNetWarnMessage(final String url, final long contentLength, final File file, DownloadFunc.ForceDownloadCallback callback);
    void showTaskRunningWarnMessage();
    void showStartDownloadMessage(String fileName);
}
