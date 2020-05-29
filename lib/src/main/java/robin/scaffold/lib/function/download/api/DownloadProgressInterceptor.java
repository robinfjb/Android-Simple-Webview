package robin.scaffold.lib.function.download.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import robin.scaffold.lib.function.download.DownLoadTaskData;
import robin.scaffold.lib.function.download.DownloadProgressListener;


public class DownloadProgressInterceptor implements Interceptor {
    private DownloadProgressListener progressListener;
    private DownLoadTaskData downLoadTaskData;
    public DownloadProgressInterceptor(DownloadProgressListener progressListener, DownLoadTaskData downLoadTaskData) {
        this.progressListener = progressListener;
        this.downLoadTaskData = downLoadTaskData;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener))
                .build();
    }

    public class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private final DownloadProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, DownloadProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }


        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1, downLoadTaskData);
                    return bytesRead;
                }
            };
        }
    }
}
