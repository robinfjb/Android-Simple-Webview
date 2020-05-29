package robin.scaffold.lib.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import robin.scaffold.lib.function.js.JsCallJava;


public class BaseWebView extends WebView {
    private Map<String, JsCallJava> mJsCallJavas;
    private Map<String, String> mInjectJavaScripts;

    public BaseWebView(Context context) {
        super(context);
        init(context);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        dealJavascriptLeak();
    }


    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可，setWebViewClient和setWebChromeClient要在addJavascriptInterface之前执行）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     *
     *
     * @deprecated Android4.2.2及以上版本的addJavascriptInterface方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；
     */
    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object interfaceObj, String interfaceName) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1){
            super.addJavascriptInterface(interfaceObj,interfaceName);
            return;
        }

        if (mJsCallJavas == null) {
            mJsCallJavas = new HashMap<String, JsCallJava>();
        }
        mJsCallJavas.put(interfaceName, new JsCallJava(interfaceObj, interfaceName));
    }


    @Override
    public void onResume() {
        super.onResume();
        resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseTimers();
    }

    public void onCreate() {

    }

    public void onDestory() {
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        loadUrl("about:blank");
        stopLoading();
        if (getHandler() != null)
            getHandler().removeCallbacksAndMessages(null);
        removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) getParent())) != null)
            mViewGroup.removeView(this);
        setWebChromeClient(null);
        setWebViewClient(null);
        setTag(null);
        destroy();
    }

    @Override
    public void destroy() {
        setVisibility(View.GONE);
        if (mJsCallJavas != null) {
            mJsCallJavas.clear();
        }
        if (mInjectJavaScripts != null) {
            mInjectJavaScripts.clear();
        }
        removeAllViewsInLayout();
        fixedStillAttached();
        releaseConfigCallback();
        super.destroy();
    }


    private void dealJavascriptLeak() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
            return;
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
    }

    // Activity在onDestory时调用webView的destroy，可以停止播放页面中的音频
    private void fixedStillAttached() {
        // java.lang.Throwable: Error: WebView.destroy() called while still attached!
        // at android.webkit.WebViewClassic.destroy(WebViewClassic.java:4142)
        // at android.webkit.WebView.destroy(WebView.java:707)
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) { // 由于自定义webView构建时传入了该Activity的context对象，因此需要先从父容器中移除webView，然后再销毁webView；
            ViewGroup mWebViewContainer = (ViewGroup) getParent();
            mWebViewContainer.removeAllViewsInLayout();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) { // JELLY_BEAN
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)  { // KITKAT
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
            } catch (ClassNotFoundException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

}
