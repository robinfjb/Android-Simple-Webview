package robin.scaffold.webview;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;

import robin.scaffold.lib.develop.BaseWebDevelopment;
import robin.scaffold.lib.util.CommUrl;

public class FirstWebDevelopment extends BaseWebDevelopment {
    public FirstWebDevelopment(int priority) {
        super(priority);
    }

    @Override
    public boolean onPageStarted(WebView view, CommUrl url, Bitmap favicon) {
        Log.e("BaseWebDevelopment", "first develop onPageStarted");
        return false;
    }

    @Override
    public boolean onPageFinished(WebView view, CommUrl url) {
        Log.e("BaseWebDevelopment", "first develop onPageFinished");
        return true;
    }
}