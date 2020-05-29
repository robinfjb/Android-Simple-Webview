package robin.scaffold.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Map;

import robin.scaffold.lib.develop.BaseWebDevelopEngine;
import robin.scaffold.lib.develop.BaseWebDevelopment;
import robin.scaffold.lib.develop.IDevelopUi;
import robin.scaffold.lib.function.download.DownLoadTaskData;
import robin.scaffold.lib.function.download.DownloadFunc;
import robin.scaffold.lib.function.download.DownloadProgressListener;
import robin.scaffold.lib.function.download.ExtendDownloadListener;
import robin.scaffold.lib.function.download.IDownloadUi;
import robin.scaffold.lib.function.js.IJsCall;
import robin.scaffold.lib.function.js.IJsUi;
import robin.scaffold.lib.function.js.JsCallFunction;
import robin.scaffold.lib.function.location.LocationFunction;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;
import robin.scaffold.lib.function.scheme.ISchemeHandler;
import robin.scaffold.lib.function.scheme.SchemeFunc;
import robin.scaffold.lib.function.sonic.SonicFunc;
import robin.scaffold.lib.function.upload.IUploadUi;
import robin.scaffold.lib.function.upload.UploadFunction;
import robin.scaffold.lib.util.CommUrl;


public class WebController {
    private BaseWebDevelopEngine developEngine;
    private Context context;
    private IWebViewUI webViewUI;
    private IJsUi jsUi;
    private IUploadUi iUploadUi;
    private IDownloadUi iDownloadUi;
    private IDevelopUi developUi;

    private IJsCall jsCall;
    private ISchemeHandler schemeHandler;
    private PermissionInterceptor permissionInterceptor;
    private ExtendDownloadListener downloadListener;
    public void setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }
    private PermissionCallbackListener locationFunc;
    private PermissionCallbackListener uploadFunc;
    private SonicFunc sonicFunc;

    public WebController(Context context, IWebViewUI webViewUI) {
        this.context = context;
        this.webViewUI = webViewUI;
        developEngine = new BaseWebDevelopEngine(webViewUI);
    }

    public WebController setIJsUi(IJsUi jsUi) {
        this.jsUi = jsUi;
        return this;
    }

    public WebController setIDownloadUi(IDownloadUi iDownloadUi) {
        this.iDownloadUi = iDownloadUi;
        return this;
    }

    public WebController setIUploadUi(IUploadUi iUploadUi) {
        this.iUploadUi = iUploadUi;
        return this;
    }

    public WebController setIDevelopUi(IDevelopUi developUi) {
        this.developUi = developUi;
        return this;
    }

    public void init() {
        sonicFunc = new SonicFunc(context, permissionInterceptor);
        sonicFunc.init();
        developUi.onDevelopmentRegister();
    }

    public void go(String url) {
        ready(url);
        webViewUI.loadUrl(url);
    }

    public void go(String url, Map map) {
        ready(url);
        webViewUI.loadUrl(url,map);
    }

    private void ready(String url) {
        //sonicFunc.ready(webViewUI.getWebView(), url);
        jsUi.onAddJavaObjects();
    }

    /**
     * 获取运行js的对象
     * @return
     */
    public IJsCall getJsCall() {
        if (jsCall == null) {
            this.jsCall = new JsCallFunction(jsUi);
        }
        return jsCall;
    }

    public void registerToSchemeWhiteList(String scheme) {
        if (schemeHandler == null) {
            schemeHandler = new SchemeFunc();
        }
        schemeHandler.registerToWhiteList(scheme);
    }

    public void unRegisterToSchemeWhiteList(String scheme) {
        if (schemeHandler == null) {
            schemeHandler = new SchemeFunc();
        }
        schemeHandler.unRegisterToWhiteList(scheme);
    }

    public ExtendDownloadListener getDownloadListener() {
        if(downloadListener == null) {
            //NOTE: 所有回调均为子线程，需要做线程转换
            downloadListener = new DownloadFunc(context, iDownloadUi, permissionInterceptor, new DownloadProgressListener() {
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
        return downloadListener;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (WebPermissionConstant.REQUESTCODE_LOCATION == requestCode) {
            if(locationFunc == null) {
                locationFunc = new LocationFunction(permissionInterceptor);
            }
            locationFunc.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        } else if(WebPermissionConstant.REQUESTCODE_CAMERA == requestCode
                || WebPermissionConstant.REQUESTCODE_STORAGE == requestCode) {
            if(uploadFunc == null) {
                uploadFunc = new UploadFunction(context, permissionInterceptor, iUploadUi);
            }
            uploadFunc.onRequestPermissionsResult(context,  requestCode, permissions, grantResults);
            if(WebPermissionConstant.REQUESTCODE_STORAGE == requestCode) {
                sonicFunc.onRequestPermissionsResult(context,  requestCode, permissions, grantResults);
            }
        }
    }

    public void registerDevelopment(BaseWebDevelopment development) {
        developEngine.registerDevelopment(development);
    }

    public void unRegisterDevelopment(BaseWebDevelopment development) {
        developEngine.unRegisterDevelopment(development);
    }

    //==================================================Actiivty lifecycle=============================================================//
    public void onCreate(Bundle savedInstanceState) {
        developEngine.onCreate(savedInstanceState);
    }

    public void onDestroy() {
        developEngine.onDestroy();
        sonicFunc.onDestroy();
        if(webViewUI.getWebView() != null) {
            webViewUI.getWebView().onDestory();
        }
    }

    public void onCreateView(@Nullable Bundle savedInstanceState) {
        //RESERVED
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //RESERVED
    }

    public void onStart() {
        developEngine.onStart();
    }

    public void onStop() {
        developEngine.onStop();
    }

    public void onResume() {
        developEngine.onResume();
        if(webViewUI.getWebView() != null) {
            webViewUI.getWebView().onResume();
        }
    }

    public void onPause() {
        developEngine.onPause();
        if(webViewUI.getWebView() != null) {
            webViewUI.getWebView().onPause();
        }
    }

    public void onNewIntent(Intent intent) {
        developEngine.onNewIntent(intent);
    }

    public void onRestart() {
        developEngine.onRestart();
    }

    public void onHiddenChanged(boolean hidden) {
        //RESERVED
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        developEngine.onActivityResult(requestCode, resultCode, data);
        if(uploadFunc == null) {
            uploadFunc = new UploadFunction(context, permissionInterceptor, iUploadUi);
        }
        ((UploadFunction)uploadFunc).onActivityResult(requestCode, resultCode, data);
    }

    public void onSaveInstanceState(Bundle outState) {
        developEngine.onSaveInstanceState(outState);
    }
    //==================================================Activty lifecycle=============================================================//
    //
    //
    //------------------------------------------------------分割线---------------------------------------------------------------------//
    //
    //
    //==================================================webview lifecycle=============================================================//
    public void onLoadResource(WebView view, String url){
        //RESERVED
        //为了减少CommUrl对象的创建，此方法暂时不实现
    }

    public void onPageFinished(WebView view, String url){
        developEngine.onPageFinished(view, new CommUrl(url));
        sonicFunc.onPageFinished(view, url);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon){
        developEngine.onPageStarted(view, new CommUrl(url), favicon);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
        developEngine.onReceivedError(view, errorCode, description, failingUrl);
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
        handler.proceed();
        developEngine.onReceivedSslError(view, handler, error);
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url){
        //RESERVED
        //为了减少CommUrl对象的创建，此方法暂时不实现
        return sonicFunc.shouldInterceptRequest(view, url);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url){
        if (schemeHandler == null) {
            schemeHandler = new SchemeFunc();
        }
        if(schemeHandler.handleScheme(context, url))
            return true;
        return developEngine.shouldOverrideUrlLoading(view, new CommUrl(url));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
        return shouldOverrideUrlLoading(view, request.getUrl() != null ? request.getUrl().toString() : "");
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return developEngine.onConsoleMessage(consoleMessage);
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        developEngine.onGeolocationPermissionsShowPrompt(origin, callback);
        if(locationFunc == null) {
            locationFunc = new LocationFunction(permissionInterceptor);
        }
        ((LocationFunction)locationFunc).onGeolocationPermissionsShowPrompt(context, origin, callback);
    }

    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return developEngine.onJsAlert(view, url, message, result);
    }

    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return developEngine.onJsPrompt(view, url, message, defaultValue, result);
    }

    public void onProgressChanged(WebView view, int newProgress) {
        developEngine.onProgressChanged(view, newProgress);
    }

    public void onReceivedTitle(WebView view, String title) {
        developEngine.onReceivedTitle(view, title);
        webViewUI.onTitleCallback(title);
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if(!developEngine.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
            if(uploadFunc == null) {
                uploadFunc = new UploadFunction(context, permissionInterceptor, iUploadUi);
            }
            ((UploadFunction)uploadFunc).openFileChooser(filePathCallback);
        }
        return true;
    }

    public void openFileChooserBelowLollipop(final ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        developEngine.openFileChooserBelowLollipop(uploadFile, acceptType, capture);

        if(uploadFunc == null) {
            uploadFunc = new UploadFunction(context, permissionInterceptor, iUploadUi);
        }
        ValueCallback<Uri[]> filePathCallback = new ValueCallback<Uri[]>() {
            @Override
            public void onReceiveValue(Uri[] value) {
                if(value != null && value.length > 0) {
                    uploadFile.onReceiveValue(value[0]);
                } else {
                    uploadFile.onReceiveValue(null);
                }
            }
        };
        ((UploadFunction)uploadFunc).openFileChooser(filePathCallback);
    }
    //==================================================webview lifecycle=============================================================//
}
