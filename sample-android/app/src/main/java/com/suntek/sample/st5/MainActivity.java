package com.suntek.sample.st5;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.sdkapi.api.SdkApi;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SdkApi.newInstance(getApplicationContext());
        TextView view = new TextView(this);
        String fw = SdkApi.getInstance().DeviceInfo().getFirmwareVersion();
        view.setText("SUNTEK ST5 SDK Ready\nFirmware: " + fw);
        setContentView(view);
    }
}
