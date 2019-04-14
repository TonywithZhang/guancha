package com.tec.zhang.guancha;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends BaseActivity {
    private static final String TAG = "观察者网app";

    //private LottieAnimationView w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11,w12;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        ActionBar action = getSupportActionBar();
        if (action != null){
            action.hide();
        }
        LitePal.getDatabase();
        /*w1 = (LottieAnimationView) findViewById(R.id.w1);
        w2 = (LottieAnimationView) findViewById(R.id.w2);
        w3 = (LottieAnimationView) findViewById(R.id.w3);
        w4 = (LottieAnimationView) findViewById(R.id.w4);
        w5 = (LottieAnimationView) findViewById(R.id.w5);
        w6 = (LottieAnimationView) findViewById(R.id.w6);
        w7 = (LottieAnimationView) findViewById(R.id.w7);
        w8 = (LottieAnimationView) findViewById(R.id.w8);
        w9 = (LottieAnimationView) findViewById(R.id.w9);
        w10 = (LottieAnimationView) findViewById(R.id.w10);
        w11 = (LottieAnimationView) findViewById(R.id.w11);
        w12 = (LottieAnimationView) findViewById(R.id.w12);*/

        /*final List<LottieAnimationView> animalList = new ArrayList<>();
        animalList.add(w1);
        animalList.add(w2);
        animalList.add(w3);*/

        new CountDownTimer(3500,370){
            @Override
            public void onTick(long millisUntilFinished) {
                count ++;
               /* for (LottieAnimationView ani : animalList){
                    switch (count){
                        case 1:
                            w1.playAnimation();
                            break;

                        case 2:
                            w2.playAnimation();
                            break;

                            case 3:
                             w3.playAnimation();
                             break;

                        case 4:
                            w4.playAnimation();
                            break;
                        case 5:
                            w5.playAnimation();
                            break;

                        case 6:
                            w6.playAnimation();
                            break;
                        case 7:
                            w7.playAnimation();
                            break;

                        case 8:
                            w8.playAnimation();
                            break;

                        case 9:
                            w9.playAnimation();
                            break;

                        case 10:
                            w10.playAnimation();
                            break;
                        case 11:
                            w11.playAnimation();
                            break;
                        case 12:
                            w12.playAnimation();
                            break;
                             default:
                    }
                }*/
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(WelcomeActivity.this,SurfPage.class));
                //Toast.makeText(WelcomeActivity.this,"动画加载完毕！！！",Toast.LENGTH_LONG).show();
            }
        }.start();
        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
/*        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
