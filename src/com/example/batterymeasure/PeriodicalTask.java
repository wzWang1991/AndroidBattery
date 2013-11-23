package com.example.batterymeasure;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.example.batterymeasure.MainActivity.BatteryConsumptionReceiver;

import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Browser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;

public class PeriodicalTask extends Activity implements Runnable {
	private String taskType;
	private String taskMode;
	private int runningTime;
	private int runningInterval;
	private int runningPercentage;
	private Date startTime;
	private int batteryLevel;
	private int batteryScale;
	private boolean stopFlag;
	
	private String manualPackageName;
	private String manualClassName;
	
	private boolean screenSwitch;
	
	BatteryConsumptionReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periodical_task);
		Intent intent = getIntent();
		taskType = intent.getStringExtra("TASK_TYPE");
		taskMode = intent.getStringExtra("TASK_MODE");
		runningTime=intent.getIntExtra("RUNNING_TIME", 2);
		runningInterval=intent.getIntExtra("RUNNING_INTERVAL", 5);
		runningPercentage=intent.getIntExtra("RUNNING_PERCENTAGE", 2);
		
		
		manualPackageName = intent.getStringExtra("Manual_package_name");
		manualClassName = intent.getStringExtra("Manual_class_name");
		
		screenSwitch = intent.getBooleanExtra("ScreenSwitch", true);
		System.out.println(screenSwitch);
		
    	receiver = new BatteryConsumptionReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.battery");
    	this.registerReceiver(receiver, filter);
    	
    	batteryLevel=100;
    	batteryScale=100;
    	
    	
    	
		Thread thread = new Thread(this);
    	Intent itService=new Intent(this, BatteryService.class);
    	itService.addCategory("BatteryServiceTAG");
    	
    	startService(itService);
		
		thread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.periodical_task, menu);
		return true;
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		stopFlag = true;
	}

	@Override
	public void run() {
		
		startTime = new Date();
		
    	ArrayList<String> addressList = new ArrayList<String>();
    	addressList.add("Zhangzhou Fujian China");
    	addressList.add("Columbia University 116th Street and Broadway New York");
    	addressList.add("Shanghai Jiaotong University, Dongchuan Road 800");
    	
    	ArrayList<String> websiteList = new ArrayList<String>();
    	websiteList.add("http://www.google.com");
    	websiteList.add("http://www.myengineerlife.com");
    	websiteList.add("http://www.cnn.com");
    	
    	Intent visitWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    	visitWebsite.setClassName("com.android.chrome", "com.google.android.apps.chrome.Main");  
    	visitWebsite.putExtra(Browser.EXTRA_APPLICATION_ID,"BatteryLife");
    	
    	int addressPointer=0;
        Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=211W 108th Street New York"));
        Context context = getApplicationContext();
        keepScreenOn(context,screenSwitch);
        
        stopFlag = false;
        createStopNotification();
        
        boolean manualTestStartFlag = false;
        
        while(!stopFlag){
			if (taskMode.equals("StopByTime")) {
				if ((new Date().getTime() - startTime.getTime()) / 1000 >= runningTime * 60)
					break;
			} else if (taskMode.equals("StopByPercentage")) {
				if (batteryLevel * 100 / batteryScale <= runningPercentage)
					break;
			}
			if (batteryLevel * 100 / batteryScale <= 5)
				break;
        	
        	
        	//if type is manual
			if (taskType.equals("manual")) {
				//Prevent run several times.
				if(!manualTestStartFlag){
					manualTestStartFlag = true;
					Intent it = new Intent();
					ComponentName comp = new ComponentName(manualPackageName, manualClassName);
					it.setComponent(comp); 
					it.setAction(Intent.ACTION_VIEW); 
					startActivity(it);
				}
				

			}
			else{

				searchAddress.setData(Uri.parse("geo:0,0?q="
						+ addressList.get(addressPointer) + "?z=20"));
				visitWebsite
						.setData(Uri.parse(websiteList.get(addressPointer)));
				addressPointer++;

				if (taskType.equals("visitWebsite")) {
					startActivity(visitWebsite);
				}

				if (taskType.equals("searchAddress")) {
					startActivity(searchAddress);
				}

				try {
					Thread.sleep(1000 * runningInterval);
				} catch (Exception e) {

				}
				if (addressPointer == addressList.size()) {
					addressPointer = 0;
					// break;
				}
			}
		}
        keepScreenOn(context,false);
        unregisterReceiver(receiver);
		// TODO Auto-generated method stub
        Intent itService = new Intent(getApplicationContext(), BatteryService.class);
        itService.addCategory("BatteryServiceTAG");
    	stopService(itService);
    	createNotification();
    	
		
	}
	
    @SuppressLint("NewApi")
	public void createNotification(){
        String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)getSystemService(svcName);
        
        Context context = getApplicationContext();
        //notification;
        Intent newIntent =new Intent(this, MainActivity.class);
      
        PendingIntent newPendingIntent=PendingIntent.getActivity(context, 0, newIntent, 0);
        CharSequence charseq = "Test is finished!";
        Notification noti = new Notification.Builder(context)
        .setContentTitle("Test Finished!" )
        .setContentText("Now you can click here to go back to main interface.")
        .setSmallIcon(R.drawable.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(newPendingIntent)
        .setTicker(charseq)
        .build(); 
       noti.defaults=Notification.DEFAULT_SOUND;
       notificationManager.notify(1,noti);
    }
    
    @SuppressLint("NewApi")
	public void createStopNotification(){
        String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)getSystemService(svcName);
        
        Context context = getApplicationContext();
        //notification;
        Intent newIntent =new Intent(this, PeriodicalTask.class);
        newIntent.putExtra("STOP_FLAG", true);
        PendingIntent newPendingIntent=PendingIntent.getActivity(context, 0, newIntent, 0);
        CharSequence charseq = "Test is running!";
        Notification noti = new Notification.Builder(context)
        .setContentTitle("Test Running!" )
        .setContentText("Click Here to stop it now.")
        .setSmallIcon(R.drawable.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(newPendingIntent)
        .setTicker(charseq)
        .build(); 
       noti.defaults=Notification.DEFAULT_SOUND;
       notificationManager.notify(1,noti);
    }
    
    
    public class BatteryConsumptionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			batteryLevel = bundle.getInt("BATTERY_COMSUMPTION_LEVEL");
			batteryScale = bundle.getInt("BATTERY_COMSUMPTION_SCALE");
		}
    	
    }
    
    public void goback(View v){
    	finish();
    }
	
	/**
     *Keep screen on
     *
     * @param on
     *            
     */
    private static WakeLock wl;
    @SuppressWarnings("deprecation")
	public static void keepScreenOn(Context context, boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wl.acquire();
        } else {
            if (wl != null) {
                wl.release();
                wl = null;
            }
        }
    }
    

}
