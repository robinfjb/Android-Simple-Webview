package robin.scaffold.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_webview);
    }

    public void onClickBt1(View v) {
        openWebView("https://www.github.com");
    }

    public void onClickBt2(View v) {
        openWebView("https://mc.vip.qq.com/demo/indexv3");
    }

    public void onClickBt3(View v) {
        openWebView("file:///android_asset/sms/sms.html");
    }

    public void onClickBt4(View v) {
        openWebView("file:///android_asset/js_interaction/hello.html");
    }

    public void onClickBt5(View v) {
        openWebView("https://www.github.com", "这是我的title");
    }

    public void onClickBt6(View v) {
        openWebView("https://m.baidu.com");
    }

    public void onClickBt7(View v) {
        openWebView("https://download.alicdn.com/wireless/tmallandroid/latest/tmallandroid_10002119.apk");
//        openWebView("http://wppkg.baidupcs.com/issue/netdisk/yunguanjia/BaiduNetdisk_6.9.7.4.exe");
    }

    private void openWebView(String url) {
        openWebView(url, null);
    }

    private void openWebView(String url, String title) {
        Intent intent = new Intent(this, MyBrowserActivity .class);
        if(!TextUtils.isEmpty(title)) {
            intent.putExtra("title", title);
        }
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
