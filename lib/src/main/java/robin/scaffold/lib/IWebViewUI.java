package robin.scaffold.lib;

import android.webkit.WebBackForwardList;

import java.util.Map;

import robin.scaffold.lib.component.BaseWebView;


public interface IWebViewUI {
    void loadUrl(String url);

    void loadUrl(String url, Map<String, String> exHeaders);

    void onTitleCallback(String title);

    void refresh();

    boolean goBack();

    void goForward();

    WebBackForwardList getWebBackForwardList();

    BaseWebView getWebView();

    /**
     * onPageFinish时的回调
     */
    void uiOnPageFinish();

    /**
     * OnPageStart时的回调
     */
    void uiOnPageStart();
}
