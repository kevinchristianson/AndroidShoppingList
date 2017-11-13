package com.example.kevinchristianson.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
        animate(ivLogo);
    }

    // referenced https://stackoverflow.com/questions/11476144/android-translateanimation-to-center
    // to get screen center for translation animation, but added other code to increase functionality
    private void animate(final View view) {
        final RelativeLayout root = (RelativeLayout) findViewById(R.id.layoutSplash);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        int yDest = 2*dm.heightPixels/3 - view.getMeasuredHeight()/2 - statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, yDest - originalPos[1]);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // extract duration into xml so that animation durations always are in sync
        anim.setDuration(getResources().getInteger(R.integer.duration));
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(anim);
        animationSet.addAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in_anim));
        animationSet.setFillAfter(true);
        animationSet.setFillEnabled(true);
        view.startAnimation(animationSet);
    }
}
