package com.lxd.gpstrack.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.lxd.gpstrack.R;
import com.lxd.gpstrack.SerialPortActivity;
import com.lxd.gpstrack.util.HexUtil;

/**
 * @author BJXT-LXD
 * @version 1.0
 * @date 2020/3/10 21:37
 * @description TODO
 */
public class SendDemo extends SerialPortActivity {
    private Button senddemo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senddemo);

        senddemo = findViewById(R.id.button);

        senddemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dat="AB   CA    DB";
                sendHexString(dat.replaceAll("\\s*",""),"485");
            }
        });
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("wocao","dddddd");
                if(type==485) {

                    Log.i("testData", "485555数据: " + HexUtil.encodeHexStr(buffer, false, size));
                }
            }
        });
    }
}
