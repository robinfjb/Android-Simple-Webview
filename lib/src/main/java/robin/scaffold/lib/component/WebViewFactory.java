package robin.scaffold.lib.component;

import android.content.Context;

public class WebViewFactory {
    //TODO
    //保留方法：为将来准备不同的webview
    public static BaseWebView createWebView(Context context){
        return new BaseWebView(context);
    }
}
