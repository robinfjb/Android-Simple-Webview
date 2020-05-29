package robin.scaffold.lib.function.download.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface IDownloadService {
    @Streaming
    @GET
        //downParam下载参数，传下载区间使用
        //url 下载链接
    retrofit2.Call<ResponseBody> download(@Header("Range") String downParam, @Url String url);
}
