package robin.scaffold.lib.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CommUrl {
    private String mUrl;

    private Boolean mParsed;
    private Boolean mUpdated;

    private String mScheme;
    private String mSchemeSpecificPart;
    private String mAuthority;
    private String mUserInfo;
    private String mHost;
    private int mPort;
    private String mPath;
    private Map<String, String> mQueries;
    private String mFragment;

    public CommUrl(String url){
        mParsed = false;
        mUpdated = false;
        // mUri = null;
        mQueries = null;

        mUrl = url;
    }

    public CommUrl(Uri uri){

        mParsed = false;
        mUpdated = false;
        // mUri = null;
        mQueries = null;
        mUrl = uri.toString();

        parseUri(uri);
    }

    private void parse() {
        if(TextUtils.isEmpty(mUrl)){
            return;
        }

        Uri uri = null;
        try{
            uri = Uri.parse(mUrl);
        }catch(Exception e){
            return;
        }

        parseUri(uri);
    }

    private void parseUri(Uri uri) {
        if(uri == null){
            return;
        }
        mScheme = uri.getScheme();
        mSchemeSpecificPart = uri.getSchemeSpecificPart();
        mAuthority = uri.getAuthority();
        mUserInfo = uri.getUserInfo();
        mHost = uri.getHost();
        mPort = uri.getPort();
        mPath = uri.getPath();

        mFragment = uri.getFragment();

        mQueries = new LinkedHashMap<String, String>();
        mParsed = true;

        // String query = mUri.getQuery();
        try {
            Set<String> keys = uri.getQueryParameterNames();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {//Iterating over the set will return the names in order of their first occurrence
                String key = it.next();
                mQueries.put(key, uri.getQueryParameter(key));
            }
        }catch (Exception e){
            StackTraceElement[] stacks = e.getStackTrace();
            for (StackTraceElement stack : stacks) {
                Log.e("CommUrl", stack.getClassName() + ":" + stack.getMethodName() + "(" + stack.getLineNumber() + ")");
            }
            Log.e("CommUrl","parseUri,mScheme="+mScheme+",mSchemeSpecificPart="+mSchemeSpecificPart+",mAuthority="+mAuthority+",mHost="+mHost+",mPath="+mPath);
        }

    }

    /**
     * 返回原始url
     * @return
     */
    public String getOrgUrl(){
        return mUrl;
    }

    public void setFragment(String fragment){
        if (!mParsed) parse();

        this.mFragment = fragment;
        mUpdated = true;
    }

    /**
     * 获取此 URL 的授权部分
     * @return
     */
    public String getAuthority(){
        if (!mParsed) parse();

        return mAuthority;
    }

    /**
     * 返回URL的主机
     * @return
     */
    public String getHost(){
        if (!mParsed) parse();

        return mHost;
    }

    /**
     * 返回URL路径部分
     * @return
     */
    public String getPath(){
        if (!mParsed) parse();

        return mPath;
    }

    /**
     * 返回URL查询部分
     * @return
     */
    /*
    public String getQuery(){
        if (!mParsed) parse();

        return mQuery;
    }
    */


    /**
     * 获取此 URL 的锚点（也称为"引用"或者fragment）
     * @return
     */
    public String getFragment(){
        if (!mParsed) parse();

        return mFragment;
    }

    /**
     * 返回URL的协议
     * @return
     */
    public String getProtocol(){
        if (!mParsed) parse();

        return mScheme;
    }

    /**
     * 返回URL的协议
     * @return
     */
    public String getScheme(){
        if (!mParsed) parse();

        return mScheme;
    }

    /**
     * 返回URL的端口
     * @return
     */
    public int getPort(){
        if (!mParsed) parse();

        return mPort;
    }

    /**
     * 增加一个query，已有则保留原值不处理
     * @param key
     * @param value
     */
    public void addOrIgnoreQuery(String key, String value){
        if(key == null){
            return;
        }

        if (!mParsed) parse();

        if(mQueries == null){
            return;
        }
        if(!mQueries.containsKey(key)){
            mQueries.put(key, value);
            mUpdated = true;
        }
    }

    /**
     * 增加多个query参数,已有则保留原值不处理
     * 若要保留按插入顺序则需传入LinkedHashMap
     * @param params
     */
    public void addOrIgnoreQueries(Map<String, String> params){
        if(params == null || params.isEmpty()){
            return;
        }

        if (!mParsed) parse();
        if(mQueries == null){
            return;
        }
        for(String key : params.keySet()) {
            if (key != null) {
                if(!mQueries.containsKey(key)) {
                    mQueries.put(key, params.get(key));
                }
            }
        }

        mUpdated = true;
    }

    /**
     * 增加多个query参数,已有则替换value
     * @param params
     */
    public void addOrReplacequeries(Map<String, String> params){
        if(params == null || params.isEmpty()){
            return;
        }

        if (!mParsed) parse();

        for(String key : params.keySet()){
            addOrReplaceQuery(key, params.get(key));
        }

        mUpdated = true;
    }

    public void addOrReplacequeries(Parameters params){
        if(params == null){
            return;
        }

        if (!mParsed) parse();

        Set<String> set = params.getParameterNames();
        for(String key : set){
            addOrReplaceQuery(key, params.getParameter(key));
        }

        mUpdated = true;
    }

    /**
     * 增加一个query，已有则替换value
     * @param key
     * @param value
     */
    public void addOrReplaceQuery(String key, String value){
        if(key == null){
            return;
        }

        if (!mParsed) parse();
        if(mQueries == null){
            return;
        }
        mQueries.put(key, value);
        mUpdated = true;
    }

    /**
     * 移除一个query参数
     * @param key
     */
    public void removeQuery(String key){
        if (!mParsed) parse();
        if(mQueries == null){
            return;
        }
        if(mQueries.containsKey(key)){
            mQueries.remove(key);
            mUpdated = true;
        }
    }

    /**
     * 获取url中某个query的值
     * @param key
     * @return
     */
    public String getQueryParameter(String key ) {
        if (!mParsed) parse();
        if(mQueries == null){
            return null;
        }
        return mQueries.get(key);
    }

    /**
     * url中是否包含某个query
     * @param key
     * @return
     */
    public boolean containsKey(String key){
        if (!mParsed) parse();
        if(mQueries == null){
            return false;
        }
        return mQueries.containsKey(key);
    }

    public String getUrl() {
        return buildUrl();
    }

    /**
     * 返回处理后的url
     * @return
     * @deprecated
     */
    public String build(){
        return buildUrl();
    }

    /**
     * 返回处理后的url
     * @return
     */
    private String buildUrl(){
        if (!mUpdated) return mUrl;

        StringBuilder str = new StringBuilder();
        str.append(mScheme + "://");
        if (mUserInfo != null) {
            str.append(mUserInfo + "@");
        }
        str.append(mHost);
        if(mPort > 0) {
            str.append(":").append(mPort);
        }
        str.append(mPath);

        boolean first = true;
        if(mQueries != null){
            for (String key : mQueries.keySet()) {
                if (first) {
                    str.append("?");
                    first = false;
                } else {
                    str.append("&");
                }
                str.append(urlEncode(key) + "=" + urlEncode(mQueries.get(key)));
            }
        }

        if (mFragment != null && mFragment != ""){
            str.append("#" + mFragment);
        }

        return str.toString();
    }

    private String urlEncode(String in) {
        if (in == null) {
            return "";
        }

        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return in;
        }
    }
}
