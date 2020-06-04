package robin.scaffold.webview;

import robin.scaffold.lib.component.BaseBrowserActivity;
import robin.scaffold.lib.component.BaseFragmentWebview;

public class MyBrowserActivity extends BaseBrowserActivity {
    @Override
    protected BaseFragmentWebview createBaseFragmentWebview() {
        return new MyFragmentWebview();
    }
}
