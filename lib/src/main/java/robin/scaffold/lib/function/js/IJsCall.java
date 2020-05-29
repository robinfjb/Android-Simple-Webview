package robin.scaffold.lib.function.js;

import android.webkit.ValueCallback;


public interface IJsCall {
    void callJs(String method, ValueCallback<String> callback, String... params);
    void callJs(String method, String... params);
    void callJs(String method);
}
