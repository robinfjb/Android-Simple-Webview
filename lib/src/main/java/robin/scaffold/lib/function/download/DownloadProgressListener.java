package robin.scaffold.lib.function.download;

public interface DownloadProgressListener {
    void onProgress(long progress, long total, boolean done, DownLoadTaskData downLoadTaskData);

    void onDownloadFinish(DownLoadTaskData downLoadTaskData);

    void onDownloadPause();

    void onDownloadStart();

    void onDownloadCancel();

    void onDownloadError(String filePath, String url, String message);
}
