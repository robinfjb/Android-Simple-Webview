package robin.scaffold.lib.function.sonic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.TextView;

import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

import java.lang.ref.WeakReference;

import robin.scaffold.lib.component.BaseWebView;
import robin.scaffold.lib.function.BaseFunction;
import robin.scaffold.lib.function.permission.PermissionCallbackListener;
import robin.scaffold.lib.function.permission.PermissionInterceptor;
import robin.scaffold.lib.function.permission.WebPermissionConstant;


public class SonicFunc extends BaseFunction implements PermissionCallbackListener {
    private Context context;
    private PermissionInterceptor permissionInterceptor;
    private SonicSession sonicSession;
    private WeakReference<BaseWebView> webViewWeakReference;
    private String url;

    public SonicFunc(Context context, PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
        this.context = context;
    }

    public void init() {
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            boolean hasPermission= context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
            if(!hasPermission){
                ((Activity)context).requestPermissions(WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_STORAGE);
                return;
            }
        } else if(permissionInterceptor != null){
            if(permissionInterceptor.intercept(null, WebPermissionConstant.STORAGE, WebPermissionConstant.REQUESTCODE_STORAGE, this)) {
                return;
            }
        }*/
        initSonic();
    }

    public void ready(BaseWebView webView, String url) {
        initSonicSession(webView, url);
    }

    private void initSonic() {
        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
        sessionConfigBuilder.setSupportLocalServer(true);
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(context.getApplicationContext()), new SonicConfig.Builder().build());
        }
    }

    private void initSonicSession(BaseWebView webView, String url) {
        webViewWeakReference = new WeakReference<BaseWebView>(webView);
        this.url = url;
        if (!SonicEngine.isGetInstanceAllowed()) {
            return;
        }
        SonicSessionClientImpl sonicSessionClient = null;
        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(url,  new SonicSessionConfig.Builder().build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
//            throw new UnknownError("create session fail!");
        }
        if(sonicSessionClient != null) {
            Intent intent = ((Activity)context).getIntent();
            intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
            webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        }
    }

    public void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
    }

    public void onPageFinished(WebView view, String url){
        if (sonicSession != null) {
            sonicSession.getSessionClient().pageFinish(url);
        }
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url){
        if (sonicSession != null) {
            //step 6: Call sessionClient.requestResource when host allow the application
            // to return the local data .
            return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
        }
        //RESERVED
        //为了减少CommUrl对象的创建，此方法暂时不实现
        return null;
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if(WebPermissionConstant.REQUESTCODE_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSonic();
                BaseWebView webView = webViewWeakReference.get();
                if(webView != null && url != null) {
                    initSonicSession(webView, url);
                }
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                TextView title= new TextView(context);
                title.setText("无法使用SD卡");
                title.setPadding(10,10,10,10);
                title.setGravity(Gravity.CENTER);
                title.setTextSize(21);
                title.setTextColor(context.getResources().getColor(android.R.color.white));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setCustomTitle(title)
                        .setMessage("请在对应的权限管理中，将应用“使用SD卡”的权限修改为允许")
                        .setPositiveButton("知道了", null).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }
}
