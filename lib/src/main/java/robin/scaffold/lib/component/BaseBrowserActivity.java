package robin.scaffold.lib.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import robin.scaffold.lib.R;

public class BaseBrowserActivity extends AppCompatActivity {
    protected BaseFragmentWebview mWebviewFragment;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_base_web);
        Intent intent = getIntent();
        if(!checkIntent(intent)) {
            Log.e("BaseBrowserActivity", "非法的url");
            finish();
        }

        //初始化标题栏
        initTitleLayout(intent);

        if (bundle == null) {
            mWebviewFragment = createBaseFragmentWebview();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getSupportFragmentManager().beginTransaction().add(R.id.root_container, mWebviewFragment, "web_pane").commit();
            mWebviewFragment.setUserVisibleHint(true);
        } else {
            mWebviewFragment = (BaseFragmentWebview) getSupportFragmentManager().findFragmentByTag("web_pane");
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void initTitleLayout(Intent intent) {
        View title_layout = findViewById(R.id.title_layout);
        TextView layout_title = (TextView) findViewById(R.id.layout_title);
        View layout_back = findViewById(R.id.layout_back);
        TextView layout_righttext = (TextView) findViewById(R.id.layout_righttext);
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (intent.hasExtra("title")) {
            title_layout.setVisibility(View.VISIBLE);
            layout_title.setText(getIntent().getStringExtra("title"));
        } else {
            title_layout.setVisibility(View.GONE);
        }
    }

    protected BaseFragmentWebview createBaseFragmentWebview() {
        return new BaseFragmentWebview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(!checkIntent(intent)) {
            Log.e("BaseBrowserActivity", "非法的url");
            return;
        }
        if (mWebviewFragment != null) {
            mWebviewFragment.onNewIntent(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (mWebviewFragment != null) {
            mWebviewFragment.onActivityResult(requestCode, resultCode, resultIntent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideKeyboard();
        if (mWebviewFragment != null) {
            mWebviewFragment.onRestart();
        }
    }

    private void hideKeyboard() {
        if(this == null) return;
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View view = this.getCurrentFocus();
            if (view != null && view.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private boolean checkIntent(Intent intent) {
        if(intent != null) {
            Uri uri = intent.getData();
            if(uri != null && !TextUtils.isEmpty(uri.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mWebviewFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if(mWebviewFragment.goBack()) {
        } else {
            super.onBackPressed();
        }
    }
}
