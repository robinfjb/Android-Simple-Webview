package robin.scaffold.lib.function.js;

import android.webkit.ValueCallback;


public interface IJsUi {
    void loadJs(String script, ValueCallback<String> callback);
    void onAddJavaObjects();
}
