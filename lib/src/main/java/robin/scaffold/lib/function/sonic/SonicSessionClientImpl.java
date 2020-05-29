package robin.scaffold.lib.function.sonic;

import android.os.Bundle;

import com.tencent.sonic.sdk.SonicSessionClient;

import java.util.HashMap;

import robin.scaffold.lib.component.BaseWebView;


public class SonicSessionClientImpl extends SonicSessionClient {
        private BaseWebView webView;

        public void bindWebView(BaseWebView webView) {
            this.webView = webView;
        }

        public BaseWebView getWebView() {
            return webView;
        }

        @Override
        public void loadUrl(String url, Bundle extraData) {
            webView.loadUrl(url);
        }

        @Override
        public void loadDataWithBaseUrl(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
            webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        }


        @Override
        public void loadDataWithBaseUrlAndHeader(String baseUrl, String data, String mimeType, String encoding, String historyUrl, HashMap<String, String> headers) {
            loadDataWithBaseUrl(baseUrl, data, mimeType, encoding, historyUrl);
        }

        public void destroy() {
            if (null != webView) {
                webView.destroy();
                webView = null;
            }
        }
}
