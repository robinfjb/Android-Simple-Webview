package robin.scaffold.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_webview);
    }

    public void onClickBt1(View v) {
        openWebView("https://m.baidu.com");
    }

    public void onClickBt2(View v) {
        openWebView("http://mc.vip.qq.com/demo/indexv");
    }

    public void onClickBt3(View v) {
        openWebView("file:///android_asset/sms/sms.html");
    }

    public void onClickBt4(View v) {
        openWebView("file:///android_asset/jsbridge/demo.html");
    }

    public void onClickBt5(View v) {
        String url = "https://m.baidu.com";
        Intent intent = new Intent(this, MyBrowserActivity .class);
        intent.putExtra("title", "这是我的title");
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void openWebView(String url) {
        Intent intent = new Intent(this, MyBrowserActivity .class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
