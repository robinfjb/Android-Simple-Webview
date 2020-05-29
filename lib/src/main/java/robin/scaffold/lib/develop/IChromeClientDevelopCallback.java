package robin.scaffold.lib.develop;

import android.net.Uri;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public interface IChromeClientDevelopCallback {
    boolean onConsoleMessage(ConsoleMessage consoleMessage);
    void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback);
    boolean onJsAlert(WebView view, String url, String message, JsResult result);
    boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result);
    void onProgressChanged(WebView view, int newProgress);
    void onReceivedTitle(WebView view, String title);
    boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
    void openFileChooserBelowLollipop(ValueCallback<Uri> uploadFile, String acceptType, String capture);
}
