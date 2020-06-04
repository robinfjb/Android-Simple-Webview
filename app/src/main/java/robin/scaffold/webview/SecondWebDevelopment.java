package robin.scaffold.webview;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;

import robin.scaffold.lib.develop.BaseWebDevelopment;
import robin.scaffold.lib.util.CommUrl;

public class SecondWebDevelopment extends BaseWebDevelopment {
    public SecondWebDevelopment(int priority) {
        super(priority);
    }

    @Override
    public boolean onPageStarted(WebView view, CommUrl url, Bitmap favicon) {
        Log.e("BaseWebDevelopment", "second develop onPageStarted");
        return false;
    }

    @Override
    public boolean onPageFinished(WebView view, CommUrl url) {
        Log.e("BaseWebDevelopment", "second develop onPageFinished");
        return false;
    }
}
