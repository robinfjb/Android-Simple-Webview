package robin.scaffold.lib.develop;

import android.content.Intent;
import android.os.Bundle;

public interface IActivityDevelopCallBack {
    void onCreate(Bundle arg0);
    void onDestroy();
    void onPause();
    void onResume();
    void onStart();
    void onStop();
    void onRestart();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onSaveInstanceState(Bundle outState);
    boolean onNewIntent(Intent intent);
}
