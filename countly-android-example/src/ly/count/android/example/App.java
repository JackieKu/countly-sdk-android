package ly.count.android.example;

import android.app.Application;

import org.OpenUDID.OpenUDID_manager;

import ly.count.android.api.Countly;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The unique device ID, this example use OpenUDID.
        final String deviceID = OpenUDID_manager.getOpenUDID(getApplicationContext());
        // You should use cloud.count.ly instead of YOUR_SERVER for the line below if you are using Countly Cloud service
        //Countly.sharedInstance().init(this, "https://YOUR_SERVER", "YOUR_APP_KEY", deviceID);
    }
}
