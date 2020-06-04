package robin.scaffold.webview;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

import robin.scaffold.lib.component.BaseFragmentWebview;

public class JsInterfaceCompat {
    private String TAG = this.getClass().getSimpleName();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private WeakReference<BaseFragmentWebview> weakReference = null;

    public JsInterfaceCompat(BaseFragmentWebview fragmentWebview) {
        weakReference = new WeakReference<BaseFragmentWebview>(fragmentWebview);
    }

    @JavascriptInterface
    public void uploadFile() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    uploadFile("*/*");
                }
            });
            return;
        }
        uploadFile("*/*");
    }

    public void uploadFile(String acceptType) {

    }
}
