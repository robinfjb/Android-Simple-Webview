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

import androidx.annotation.NonNull;

import robin.scaffold.lib.util.CommUrl;
import robin.scaffold.lib.IWebClientDevelopCallback;


public class BaseWebDevelopment implements IActivityDevelopCallBack,
        IWebClientDevelopCallback,
        IChromeClientDevelopCallback,
        Comparable<BaseWebDevelopment> {
    private int priority;
    public BaseWebDevelopment(int priority) {
       this.priority = priority;
    }


    @Override
    public void onCreate(Bundle arg0) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public boolean onNewIntent(Intent intent) {
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, CommUrl url) {
        return false;
    }

    @Override
    public boolean onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        return false;
    }

    @Override
    public boolean onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        return false;
    }

    @Override
    public boolean onPageFinished(WebView view, CommUrl url) {
        return false;
    }

    @Override
    public boolean onPageStarted(WebView view, CommUrl url, Bitmap favicon) {
        return false;
    }

    @Override
    public boolean onLoadResource(WebView view, CommUrl url) {
        return false;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return false;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return false;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return false;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }

    @Override
    public void openFileChooserBelowLollipop(ValueCallback<Uri> uploadFile, String acceptType, String capture) {

    }

    @Override
    public int compareTo(@NonNull BaseWebDevelopment o) {
        if(priority > o.priority)
            return 1;
        if(priority < o.priority)
            return -1;
        return 0;
    }
}
