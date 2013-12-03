package edu.columbia.batterybenchmark;

import java.text.SimpleDateFormat;
import java.util.Date;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;


public class BatteryService extends Service {

	public static final String TAG = "BatteryServiceTAG";
	private int level;
	private int scale;
	
	private class BatteryInfoBroadcastReceiver extends BroadcastReceiver {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
	            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
	            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
	        }
	    }
	}
	
	private BatteryInfoBroadcastReceiver receiver = new BatteryInfoBroadcastReceiver(){
		public void onReceive(Context context, Intent intent){
			level = intent.getIntExtra("level", 0);
			scale = intent.getIntExtra("scale", 100);
			sendBatteryConsumption(level,scale);
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//set filter for broadcast, only receive ACTION_BATTERY_CHANGED
	@Override
	public void onStart(Intent intent, int startID){
		IntentFilter batteryReceiveFilter = new IntentFilter();
		batteryReceiveFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(receiver, batteryReceiveFilter);
	}
	
	public void onDestroy(){
		sendBatteryConsumption(level,scale);
		unregisterReceiver(receiver);
	}
	
	private void sendBatteryConsumption(int level, int scale) {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date nowTime = new Date();
		String nowTimeString = df.format(nowTime);
		Intent ittmp = new Intent();
		ittmp.setAction("android.intent.action.battery");
		ittmp.putExtra("BATTERY_COMSUMPTION_TIME", nowTimeString);
		ittmp.putExtra("BATTERY_COMSUMPTION_LEVEL", level);
		ittmp.putExtra("BATTERY_COMSUMPTION_SCALE", scale);
		sendBroadcast(ittmp);
	}
	 

}
