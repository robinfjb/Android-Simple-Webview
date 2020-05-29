package robin.scaffold.lib.develop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robin.scaffold.lib.util.CommUrl;
import robin.scaffold.lib.IWebViewUI;


public class BaseWebDevelopEngine {
    protected IWebViewUI mIWebViewUI;
    protected List<BaseWebDevelopment> moduleQueue = new ArrayList<BaseWebDevelopment>();

    public BaseWebDevelopEngine(IWebViewUI webViewUI) {
        this.mIWebViewUI = webViewUI;
    }

    public void registerDevelopment(BaseWebDevelopment development) {
        moduleQueue.add(development);
        Collections.sort(moduleQueue);
    }

    public void unRegisterDevelopment(BaseWebDevelopment development) {
        boolean b = moduleQueue.remove(development);
        if(b) {
            Collections.sort(moduleQueue);
        }
    }

    public void onCreate(Bundle arg0) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onCreate(arg0);
        }
    }

    public void onDestroy() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onDestroy();
        }
    }

    public void onStart() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onStart();
        }
    }

    public void onStop() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onStop();
        }
    }

    public void onPause() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onPause();
        }
    }

    public void onResume() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onResume();
        }
    }

    public void onRestart() {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onRestart();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onSaveInstanceState(outState);
        }
    }

    public void onNewIntent(Intent intent) {
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onNewIntent(intent)) {
                break;
            }
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, CommUrl url) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.shouldOverrideUrlLoading(view, url)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onReceivedSslError(view, handler, error)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public boolean onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onReceivedError(view, errorCode, description, failingUrl)) {
                consumed = true;
                break;
            }
        }
        //TODO
        //send error message and clear view
        return consumed;
    }

    public boolean onPageFinished(WebView view, CommUrl url) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onPageFinished(view, url)) {
                consumed = true;
                break;
            }
        }
        mIWebViewUI.uiOnPageFinish();
        return consumed;
    }

    public boolean onPageStarted(WebView view, CommUrl url, Bitmap favicon) {
        mIWebViewUI.uiOnPageStart();
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onPageStarted(view, url, favicon)) {
                consumed = true;
                break;
            }
        }
        return consumed;

    }

    public boolean onLoadResource(WebView view, CommUrl url) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onLoadResource(view, url)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onConsoleMessage(consoleMessage)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onJsAlert(view, url, message, result)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onJsPrompt(view, url, message, defaultValue, result)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public void onProgressChanged(WebView view, int newProgress) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onProgressChanged(view, newProgress);
        }
    }

    public void onReceivedTitle(WebView view, String title) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.onReceivedTitle(view, title);
        }
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        boolean consumed = false;
        for (BaseWebDevelopment module : moduleQueue) {
            if(module.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
                consumed = true;
                break;
            }
        }
        return consumed;
    }

    public void openFileChooserBelowLollipop(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        for (BaseWebDevelopment module : moduleQueue) {
            module.openFileChooserBelowLollipop(uploadFile, acceptType, capture);
        }
    }
}
