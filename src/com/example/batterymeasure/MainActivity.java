package com.example.batterymeasure;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ArrayList<String> batteryConsumptionTime = new ArrayList<String>();
	private ArrayList<Integer> batteryConsumptionLevel = new ArrayList<Integer>();
	private ArrayList<Integer> batteryConsumptionScale = new ArrayList<Integer>();
	TextView tv;
	EditText runningTimeSetting;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.textView1); 
        runningTimeSetting=(EditText)findViewById(R.id.editText1);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void getBatteryInfo(View v) {
    	BatteryConsumptionReceiver receiver = new BatteryConsumptionReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.battery");
    	this.registerReceiver(receiver, filter);
    	//tv.setText("abc");
    	Intent itService=new Intent(this, BatteryService.class);
    	itService.addCategory("BatteryServiceTAG");
    	startService(itService);
    }
    public void viewCurve(View v) {
    	Intent intent = new Intent(this, BatteryCurve.class);
    	startActivity(intent);
     
    }
  
    
    /** Called when the user clicks the Website button */
    public void visitWebsite(View view) {
    	Intent intent = new Intent(this, PeriodicalTask.class);
    	int runningTime = Integer.parseInt(runningTimeSetting.getText().toString());
    	intent.putExtra("RUNNING_TIME", runningTime);
    	intent.putExtra("TASK_TYPE", "visitWebsite");
    	startActivity(intent);

    }
    
    /** Called when the user clicks the GoogleMap button */
    public void searchAddress(View view) {
    	Intent intent = new Intent(this, PeriodicalTask.class);
    	int runningTime = Integer.parseInt(runningTimeSetting.getText().toString());
    	intent.putExtra("RUNNING_TIME", runningTime);
    	intent.putExtra("TASK_TYPE", "searchAddress");
    	startActivity(intent);

    }
    
    public void getResult(View view){
    	String tmp;
    	tmp="";
    	for(int i=0;i<batteryConsumptionTime.size();i++){
    		tmp=tmp+batteryConsumptionTime.get(i)+"|"+batteryConsumptionLevel.get(i)+"%\n";
    	}
    	tv.setText(tmp);
			
	 }
    
    public class BatteryConsumptionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String batteryTime = bundle.getString("BATTERY_COMSUMPTION_TIME");
			int batteryLevel = bundle.getInt("BATTERY_COMSUMPTION_LEVEL");
			int batteryScale = bundle.getInt("BATTERY_COMSUMPTION_SCALE");
			saveBatteryConsumption(batteryTime, batteryLevel, batteryScale);
			//tv.setText(batteryTime);
			tv.setText(String.valueOf(batteryLevel));
		}
    	
    }
    
	private void saveBatteryConsumption(String time, int level, int scale){
		batteryConsumptionTime.add(time);
		batteryConsumptionLevel.add(level);
		batteryConsumptionScale.add(scale);
	}
    
}
