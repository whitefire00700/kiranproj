package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hibs.GPSRoute.R;

public class Activity_Splash extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.splash_id);
        l.clearAnimation();
        l.startAnimation(anim);
 
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.clearAnimation();
        iv.startAnimation(anim);
        
        Thread splashTread = new Thread()
		 {
			@Override
			public void run()
			{
				try {
					Thread.sleep(5000);
					Intent in = new Intent(Activity_Splash.this, Activity_Main.class);
					startActivity(in);
					finish();
					
				}
				catch (InterruptedException e)
				{
				}
			}
			};
			splashTread.start();			
   }  
}



