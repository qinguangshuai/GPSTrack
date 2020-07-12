package com.lxd.gpstrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lxd.gpstrack.util.HexUtil;

import java.math.BigInteger;

import android_serialport_api.SerialPortUtil;

public class TestActivity extends SerialPortActivity {

    private Button btn;
    private TextView texxt1;
    private String mHexStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn = findViewById(R.id.btn);
        texxt1 = findViewById(R.id.texxt1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dat = "AB   CA    DB";
                //sendHexString(dat.replaceAll("\\s*", ""), "232");
                SerialPortUtil.open("/dev/ttyS3",19200,0);
                //SerialPortUtil.sendString(dat);
                SerialPortUtil.receive();
            }
        });
    }

    byte formData[] = new byte[1024];
    int len485 = 0;
    int len232 = 0;//接收到的数据长度
    int len = 0;//数据总长度
    boolean isStart232 = false;

    @Override
    protected void onDataReceived(final byte[] buffer, final int size, final int type) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (type == 232) {
                    Log.i("testData", "ssssssss");
                    Log.i("testDataSize", String.valueOf(size));
                    mHexStr = HexUtil.encodeHexStr(buffer, false, size);
                    texxt1.setText(mHexStr);
                    if (size == 32 && buffer[0] == -35 && buffer[1] == -103) {
                        Log.i("testData", "hhhhhhhhh");
                        for (int i = 0; i < size; i++) {
                            formData[len232 + i] = buffer[i];
                        }
                        len232 = len232 + size;
                        //获取调车单数据中所描述的数据长度
                        len = toInt2(new byte[]{buffer[2], buffer[3]}, 2) + 4;
                        Log.i("TAG_len", String.valueOf(len));
                    } else if (!isStart232) {
                        for (int i = 0; i < size; i++) {
                            formData[len232 + i] = buffer[i];
                        }
                        len232 += size;
                        Log.i("TAG_len232", String.valueOf(len232));
                        //如果接收到的数据长度等于调车单数据所描述的数据长度
                        if (len232 == len) {
                            Log.i("TAG_len232=len", "len232=len");
                        }
                    }
                }
            }
        });
    }

    /**
     * 转化为int类型
     *
     * @param bytes
     * @param size
     * @return
     */
    private int toInt2(byte[] bytes, int size) {
        return Integer.parseInt(new BigInteger((HexUtil.encodeHexStr(bytes, size).replace(
                " ", ""
        )), 16).toString(10));
    }
}
