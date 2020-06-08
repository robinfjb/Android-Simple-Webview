package robin.scaffold.webview;

import android.util.Log;
import android.webkit.ValueCallback;

import org.json.JSONObject;

import robin.scaffold.lib.component.BaseFragmentWebview;
import robin.scaffold.lib.function.download.DownLoadTaskData;
import robin.scaffold.lib.function.download.DownloadProgressListener;

public class MyFragmentWebview extends BaseFragmentWebview {
    @Override
    protected void init() {
        super.init();
        getController().registerToSchemeWhiteList("taobao");
        setDownloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done, DownLoadTaskData downLoadTaskData) {
                long initSize = downLoadTaskData.getInitalSize();
                long currentSize = initSize + progress;
                long totalSize = total + initSize;
                Log.e("WebController", "currentSize=" + currentSize + "||totalSize=" + totalSize);
            }

            @Override
            public void onDownloadFinish(DownLoadTaskData downLoadTaskData) {
                Log.e("WebController", "onDownloadFinish");
            }

            @Override
            public void onDownloadPause() {
            }

            @Override
            public void onDownloadStart() {
            }

            @Override
            public void onDownloadCancel() {
                Log.e("WebController", "onDownloadCancel");
            }

            @Override
            public void onDownloadError(String filePath, String url, String message) {
                Log.e("WebController", "onDownloadError:filePath=" + filePath + "||url=" + url + "||message=" + message);
            }
        });
    }

    @Override
    public void onAddJavaObjects() {
        super.onAddJavaObjects();
        webView.addJavascriptInterface(new JsInterfaceCompat(this), "MyName");
    }

    @Override
    public void onDevelopmentRegister() {
        getController().registerDevelopment(new FirstWebDevelopment(0));
        getController().registerDevelopment(new SecondWebDevelopment(1));
    }

    @Override
    public void showStartDownloadMessage(String fileName) {

    }

    @Override
    public void uiOnPageFinish() {
        getController().getJsCall().callJs("callByAndroid");
        getController().getJsCall().callJs("callByAndroidParam","Hello !");
        getController().getJsCall().callJs("callByAndroidInteraction","你好Js");
        getController().getJsCall().callJs("callByAndroidMoreParams", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d("BaseFragmentWebview", "js方法callByAndroidMoreParams的返回值："+ value);
            }
        }, getJson(), " Hello!", " Hello2!");
    }

    private String getJson(){

        String result="";
        try {

            JSONObject mJSONObject=new JSONObject();
            mJSONObject.put("id",1);
            mJSONObject.put("name","Robin");
            mJSONObject.put("age",18);
            result= mJSONObject.toString();
        }catch (Exception e){

        }

        return result;
    }
}
