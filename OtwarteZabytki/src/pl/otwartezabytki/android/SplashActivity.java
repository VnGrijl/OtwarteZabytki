package pl.otwartezabytki.android;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

	protected int _splashTime = 1500;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		randomizeSplash();
		TimerTask task = new TimerTask(){
        	
        	@Override
        	public void run(){
        		
        		Intent mainIntent = new Intent().setClass(SplashActivity.this,HomeActivity.class);
        		startActivity(mainIntent);
        		finish();
        	}
        };
        
        Timer timer = new Timer();
        timer.schedule(task,_splashTime);
	}
	
	private void randomizeSplash(){
		RelativeLayout splashRelativeLayout = (RelativeLayout) findViewById(R.id.splashRelativeLayout);
		ImageView splashImage = (ImageView) findViewById(R.id.splashImage);
		Random generator = new Random();
		double r = generator.nextDouble();
		int rint = (int) (10 * r);
		if(rint < 3){
			splashRelativeLayout.setBackgroundColor(getResources().getColor(R.color.mill_background));
			splashImage.setImageResource(R.drawable.splash_mill);
		}
		else if(rint < 6){
			splashRelativeLayout.setBackgroundColor(getResources().getColor(R.color.church_background));
			splashImage.setImageResource(R.drawable.splash_church);
		}
		else if(rint < 8){
			splashRelativeLayout.setBackgroundColor(getResources().getColor(R.color.tower_background));
			splashImage.setImageResource(R.drawable.splash_tower);
		}
		else{
			splashRelativeLayout.setBackgroundColor(getResources().getColor(R.color.house_background));
			splashImage.setImageResource(R.drawable.splash_house);
		}
	}

}
