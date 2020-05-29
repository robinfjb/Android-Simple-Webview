package robin.scaffold.lib;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;


public interface IWebClientDelegate {
    void onLoadResource(WebView view, String url);
    void onPageFinished(WebView view, String url);
    void onPageStarted(WebView view, String url, Bitmap favicon);
    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
    void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);
    WebResourceResponse shouldInterceptRequest(WebView view, String url);
    WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request);
    boolean shouldOverrideUrlLoading(WebView view, String url);
    boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
}
