package robin.scaffold.lib.function.scheme;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.util.CommUrl;
import robin.scaffold.lib.util.Constants;


public class SchemeFunc extends BaseFunction implements ISchemeHandler{
    private static final String SCHEME_SMS = "sms:";
    private static final String INTENT_SCHEME = "intent://";
    private List<String> whiteList = new ArrayList<String>();

    @Override
    public void registerToWhiteList(String scheme) {
        whiteList.add(scheme);
    }

    @Override
    public void unRegisterToWhiteList(String scheme) {
        whiteList.remove(scheme);
    }

    private boolean isInWhiteList(String scheme) {
        return whiteList.contains(scheme);
    }

    @Override
    public boolean handleScheme(Context context, String url) {
        if(TextUtils.isEmpty(url))
            return false;
        if (url.toLowerCase().startsWith(Constants.Net.HTTP) || url.toLowerCase().startsWith(Constants.Net.HTTPS)) {
            return false;
        }
        boolean isHandleCommScheme = handleCommScheme(context, url);
        if(isHandleCommScheme)
            return true;
        boolean isHandleIntentUrl = handleIntentUrl(context, url);
        if(isHandleIntentUrl)
            return true;
        boolean isHandleUnknowScheme = queryActivies(context, url) > 0 && handleUnknowScheme(context, url);
        if(isHandleUnknowScheme)
            return true;
        return true;
    }
    /**
     * 处理通用的scheme
     * @param context
     * @param url
     * @return
     */
    private boolean handleCommScheme(Context context, String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                if (context == null)
                    return false;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        return false;
    }

    /**
     * 处理intent://形式的url
     * @param context
     * @param intentUrl
     * @return
     */
    private boolean handleIntentUrl(Context context, String intentUrl) {
        try {
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME))
                return false;

            if (openOtherPage(context, intentUrl)) {
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 打开非常规scheme的通用函数
     * @param context
     * @param intentUrl
     * @return
     */
    private boolean openOtherPage(Context context, String intentUrl) {
        try {
            Intent intent;
            if (context == null)
                return true;
            PackageManager packageManager = context.getPackageManager();
            intent = new Intent().parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (info != null) {  //跳到该应用
                context.startActivity(intent);
                return true;
            }
        } catch (Throwable ignore) {
        }

        return false;
    }

    /**
     * 是否有对应的Activity存在
     * @param context
     * @param url
     * @return
     */
    private int queryActivies(Context context, String url) {
        try {
            if (context == null) {
                return 0;
            }
            Intent intent = new Intent().parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager mPackageManager = context.getPackageManager();
            List<ResolveInfo> mResolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return mResolveInfos == null ? 0 : mResolveInfos.size();
        } catch (URISyntaxException ignore) {
            return 0;
        }
    }

    /**
     * 处理未知的scheme，只有白名单的scheme才会跳转
     * @param context
     * @param url
     * @return
     */
    private boolean handleUnknowScheme(Context context, String url) {
        if(url == null)
            return false;
        CommUrl commUrl = new CommUrl(url);
        String scheme = commUrl.getScheme();
        if(isInWhiteList(scheme)) {
            return openOtherPage(context, url);
        }
        return false;
    }
}
