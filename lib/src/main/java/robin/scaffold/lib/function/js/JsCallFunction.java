package robin.scaffold.lib.function.js;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.ValueCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import robin.scaffold.lib.function.BaseFunction;


public class JsCallFunction extends BaseFunction implements IJsCall{
    private IJsUi jsUi;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public JsCallFunction(IJsUi jsUi) {
        this.jsUi = jsUi;
    }

    @Override
    public void callJs(String method, ValueCallback<String> callback, String... params) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            callSafeCallJs(method, callback, params);
            return;
        }
        callJsInMainThread(method, callback, params);
    }

    @Override
    public void callJs(String method, String... params) {
        this.callJs(method,null,params);
    }

    @Override
    public void callJs(String method) {
        this.callJs(method,(String[])null);
    }

    private void callSafeCallJs(final String method, final ValueCallback valueCallback, final String... params) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callJsInMainThread(method, valueCallback, params);;
            }
        });
    }

    private void callJsInMainThread(String method, ValueCallback<String> valueCallback, String... params) {
        StringBuilder sb=new StringBuilder();
        sb.append("javascript:"+method);
        if(params==null||params.length==0){
            sb.append("()");
        }else{
            sb.append("(").append(concat(params)).append(")");
        }
        jsUi.loadJs(sb.toString(),valueCallback);
    }

    private String concat(String...params){

        StringBuilder mStringBuilder=new StringBuilder();

        for(int i=0;i<params.length;i++){
            String param=params[i];
            if(!isJson(param)){
                mStringBuilder.append("\"").append(param).append("\"");
            }else{
                mStringBuilder.append(param);
            }
            if(i!=params.length-1){
                mStringBuilder.append(" , ");
            }
        }
        return mStringBuilder.toString();
    }

    private static boolean isJson(String target) {
        if (TextUtils.isEmpty(target))
            return false;
        boolean tag = false;
        try {
            if (target.startsWith("["))
                new JSONArray(target);
            else
                new JSONObject(target);

            tag = true;
        } catch (JSONException ignore) {
//            ignore.printStackTrace();
            tag = false;
        }
        return tag;

    }
}
