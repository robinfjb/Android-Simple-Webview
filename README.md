# Android-Simple-Webview
Android 功能强大的webview

1.scheme白名单机制
2.腾讯sonic
3.权限管理
4.Native和JS
5.文件上传与下载


## 打开一个普通网页：
```
		Intent intent = new Intent(this, MyBrowserActivity.class);
        intent.setData(Uri.parse("https://www.github.com"));
        startActivity(intent);
```
![普通网页](images/Screenshot_2020_0608_121550.png)

## 打开自定义title的网页：
```
		Intent intent = new Intent(this, MyBrowserActivity.class);
        intent.putExtra("title", "这是我的title");
        intent.setData(Uri.parse("https://www.github.com"));
        startActivity(intent);
```

## 自带sonic功能，可打开此页面测试：
```
		Intent intent = new Intent(this, MyBrowserActivity.class);
        intent.putExtra("title", title);
        intent.setData(Uri.parse("https://mc.vip.qq.com/demo/indexv3"));
        startActivity(intent);
```
## 自定义scheme：
sms,tel,mailto,geo等功能已集成，如果想加自定义scheme，可参考sample中列子：
```
public class MyFragmentWebview extends BaseFragmentWebview {
	 @Override
    protected void init() {
        super.init();
        getController().registerToSchemeWhiteList("taobao");
    }
}

```
效果如下