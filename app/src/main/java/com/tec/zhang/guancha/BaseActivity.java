package com.tec.zhang.guancha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//
public class BaseActivity extends AppCompatActivity {
    public final String FINISH_ZHIS = "com.tec.zhang.FINISH_ZHIS";
    private final String FINISH_ALL = "com.tec.zhang.FINISH_ALL";


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if (intent.getAction().equals(FINISH_ZHIS))
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,new IntentFilter(FINISH_ZHIS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    long timeNow = 0L;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() - timeNow < 2000){
            sendBroadcast(new Intent(FINISH_ZHIS));
            finish();
        }else {
            timeNow = System.currentTimeMillis();
            Toast.makeText(this,"再次点击退出！",Toast.LENGTH_LONG).show();
        }
    }
}
