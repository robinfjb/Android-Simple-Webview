package robin.scaffold.webview;

import android.util.Log;
import android.webkit.ValueCallback;

import org.json.JSONObject;

import robin.scaffold.lib.component.BaseFragmentWebview;

public class MyFragmentWebview extends BaseFragmentWebview {
    @Override
    protected void init() {
        super.init();
        getController().registerToSchemeWhiteList("taobao");
    }

    @Override
    public void onAddJavaObjects() {
        super.onAddJavaObjects();
        webView.addJavascriptInterface(new JsInterfaceCompat(this), "crfchina");
    }

    @Override
    public void onDevelopmentRegister() {
        getController().registerDevelopment(new FirstWebDevelopment(0));
        getController().registerDevelopment(new SecondWebDevelopment(1));
    }

    @Override
    public void uiOnPageFinish() {
        getController().getJsCall().callJs("callByAndroid");
        getController().getJsCall().callJs("callByAndroidParam","Hello !");
        getController().getJsCall().callJs("callByAndroidInteraction","你好Js");
        getController().getJsCall().callJs("callByAndroidMoreParams", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("fjb","value:"+value);
            }
        }, getJson(), " Hello!");
    }

    private String getJson(){

        String result="";
        try {

            JSONObject mJSONObject=new JSONObject();
            mJSONObject.put("id",1);
            mJSONObject.put("name","Agentweb");
            mJSONObject.put("age",18);
            result= mJSONObject.toString();
        }catch (Exception e){

        }

        return result;
    }
}
