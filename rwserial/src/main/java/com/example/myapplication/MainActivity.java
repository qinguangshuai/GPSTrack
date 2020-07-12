package com.example.myapplication;

import android.app.NativeActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xrd.sunmoon.NativeInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    static {
        System.loadLibrary("native-sunmoon-lib");
    }

    public Button mB0;
    public Button mB1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mB0= findViewById(R.id.set0);
        mB1= findViewById(R.id.set1);
        mB1.setOnClickListener(this);
        mB0.setOnClickListener(this);

    }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.set0) {
                NativeInterface.setRS485ttyS2(0);
            } else if (i == R.id.set1) {
                NativeInterface.setRS485ttyS2(1);
            }

          }
}
