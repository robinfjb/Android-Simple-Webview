package robin.scaffold.lib.function.scheme;

import android.content.Context;


public interface ISchemeHandler {
    void registerToWhiteList(String scheme);
    void unRegisterToWhiteList(String scheme);
    boolean handleScheme(Context context, String url);
}
