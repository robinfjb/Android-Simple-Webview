package robin.scaffold.lib.function.download;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import robin.scaffold.lib.function.download.api.DownloadProgressInterceptor;
import robin.scaffold.lib.function.download.api.IDownloadService;
import robin.scaffold.lib.util.Constants;
import robin.scaffold.lib.util.Logger;


public class Downloader {
    private Map<String, DownloadTask> queue;
    protected ExecutorService executorService;

    /***
     * 初始化单线程下载队列
     * */
    public Downloader() {
        executorService = Executors.newCachedThreadPool();
        queue = Collections.synchronizedMap(new ConcurrentHashMap<String, DownloadTask>());
    }

    /**
     * resultSave和saveFileName可为空
     */
    public void downloadFile(DownLoadTaskData downLoadTaskData, DownloadProgressListener listener) {
        DownloadTask task = new DownloadTask(downLoadTaskData, listener);
        queue.put(downLoadTaskData.getUrl(), task);
        executorService.submit(task);
        if (listener != null)
            listener.onDownloadStart();
    }

   public boolean cancel(String url) {
        if(url == null)
            return false;
        DownloadTask task = queue.get(url);
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }

   /* public boolean pause(String url) {
        DownloadTask task = queue.get(url);
        if (task != null) {
            task.pause();
            return true;
        }
        return false;
    }*/

  /*  public boolean resume(String url) {
        DownloadTask task = queue.get(url);
        if (task != null) {
            task.resume();
            return true;
        }
        return false;
    }*/

    private class DownloadTask implements Runnable {
        private DownLoadTaskData downLoadTaskData;
        private DownloadProgressListener listener;
        private AtomicBoolean pause = new AtomicBoolean(false);
        private AtomicBoolean cancel = new AtomicBoolean(false);
        private Retrofit retrofit;

        public DownloadTask(DownLoadTaskData downLoadTaskData, DownloadProgressListener listener) {
            this.downLoadTaskData = downLoadTaskData;
            this.listener = listener;

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            final Request.Builder builder = chain.request().newBuilder();
                            builder.addHeader("Accept", "application/*");
                            return chain.proceed(builder.build());
                        }
                    })
                    .addInterceptor(new DownloadProgressInterceptor(listener, downLoadTaskData))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.github.com")
                    .client(okHttpClient)
                    .build();
        }


        @Override
        public void run() {

            long startPos = downLoadTaskData.getInitalSize();
            retrofit2.Call call = retrofit.create(IDownloadService.class).download("bytes=" + startPos + "-", downLoadTaskData.getUrl());
            try {
                retrofit2.Response<ResponseBody> response = call.execute();
                int code = response.code();
                if (code == Constants.Net.REPONSE_CODE_NOT_FOUND || code >= Constants.Net.REPONSE_CODE_BAD_SERVER_ERR) {
                    listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), "network error");
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), "response body is null");
                    return;
                }
                downLoadTaskData.setLength(body.contentLength());
                if (startPos > downLoadTaskData.getLength()) {
                    listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), "breakpoint file has expired!");
                    return;
                }
                if (startPos == downLoadTaskData.getLength() && startPos > 0) {
                    if (downLoadTaskData.getFile().exists() && startPos == downLoadTaskData.getFile().length()) {
                        listener.onDownloadFinish(downLoadTaskData);
                        return;
                    } else {
                        listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), "breakpoint file has expired!");
                        return;
                    }
                }
                //start downloading
                RandomAccessFile randomAccessFile;
                try {
                    randomAccessFile = new RandomAccessFile(downLoadTaskData.getFile(), "rw");
                    randomAccessFile.seek(startPos);
                    final int BUFFER_SIZE = 1024 * 8;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    InputStream input = body.byteStream();
                    BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
                    int len;
                    try {
                        while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1
                                && !pause.get()
                                && !cancel.get()) {
                            randomAccessFile.write(buffer, 0, len);
                        }
                    } finally {
                        try { in.close(); } catch (Exception e){}
                        try { randomAccessFile.close(); } catch (Exception e){}
                        try { input.close(); } catch (Exception e){}
                    }
                    if(!pause.get() && !cancel.get()) {
                        listener.onDownloadFinish(downLoadTaskData);
                    } else if(cancel.get()) {
                        listener.onDownloadCancel();
                    }
                } catch (Exception e) {
                    listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), Logger.getStackTraceString(e));
                }
            } catch (IOException e) {
                listener.onDownloadError(downLoadTaskData.getFilePath(), downLoadTaskData.getUrl(), Logger.getStackTraceString(e));
            }

        }

        public void resume() {

        }

        public void pause() {
            pause.set(true);
            if(listener != null)
                listener.onDownloadPause();
        }

        public void cancel() {
            cancel.set(true);
            if(listener != null)
                listener.onDownloadCancel();
        }
    }

}
