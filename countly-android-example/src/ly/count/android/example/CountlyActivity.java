package ly.count.android.example;

import android.app.Activity;
import android.os.Bundle;

import ly.count.android.api.Countly;

public class CountlyActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onStart() {
        super.onStart();
        Countly.sharedInstance().onStart();
    }

    @Override
    public void onStop() {
        Countly.sharedInstance().onStop();
        super.onStop();
    }
}
