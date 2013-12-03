package edu.columbia.batterybenchmark;

import java.util.ArrayList;
import java.util.Date;


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

	
	private String testMode;
	private String autoTestSelection;
	private String testFinishMode;
	private String testDurationString;
	private String testEndPercentageString;
	private String testIntervalString;
	private String testManualAppName;
	private String testManualAppPackage;
	private String testManualAppClass;
	ArrayList<String> websiteList = new ArrayList<String>();
	ArrayList<String> addressList = new ArrayList<String>();
	
	
	private int testDuration;
	private int testEndPercentage;
	private int testInterval;
	
	private boolean testManualScreenSwitch;
	
	private Date startTime;
	
	//User can stop the task themselves.
	private boolean stopFlag;
	
	//Battery level now.
	private int batteryLevel;
	private int batteryScale;
	
	private BatteryConsumptionReceiver receiver;
	
	private boolean pausedFlag;
	private boolean manualTestStartFlag;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periodical_task);
		
		//Read information from intent.
		Intent intent = getIntent();
		testMode = intent.getStringExtra("TestMode");
		autoTestSelection = intent.getStringExtra("AutoTestSelection");
		testFinishMode = intent.getStringExtra("TestFinishMode");
		testDurationString = intent.getStringExtra("TestDuration");
		testEndPercentageString = intent.getStringExtra("TestEndPercentage");
		testIntervalString = intent.getStringExtra("TestInterval");
		testManualAppName = intent.getStringExtra("TestManualAppName");
		testManualAppPackage = intent.getStringExtra("TestManualAppPackage");
		testManualAppClass = intent.getStringExtra("TestManualAppClass");
		testManualScreenSwitch = intent.getBooleanExtra("TestManualScreenSwitch", true);
		
		testDuration = Integer.parseInt(testDurationString);
		testEndPercentage = Integer.parseInt(testEndPercentageString);
		testInterval = Integer.parseInt(testIntervalString);
		
		//Save websites and addresses to arraylist.
		String testWebsite = intent.getStringExtra("TestWebsite");
		String testAddress = intent.getStringExtra("TestAddress");
		String[] testWebsiteSplit = testWebsite.split("\n");
		String[] testAddressSplit = testAddress.split("\n");
		
		for(int i=0;i<testWebsiteSplit.length;i++) 
			websiteList.add(testWebsiteSplit[i]);
		for(int i=0;i<testAddressSplit.length;i++)
			addressList.add(testAddressSplit[i]);
		
    	receiver = new BatteryConsumptionReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.battery");
    	this.registerReceiver(receiver, filter);
		
		batteryLevel=100;
    	batteryScale=100;
	
    	
		Thread thread = new Thread(this);
    	Intent itService=new Intent(this, BatteryService.class);
    	itService.addCategory("BatteryServiceTAG");
    	System.out.println("abc");
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
	protected void onResume(){
		super.onResume();
		pausedFlag = true;
		
	}

	@Override
	public void run() {
		startTime = new Date();
		Context context = getApplicationContext();
		Intent testIntent = null;
		
		//If test mode is auto.
		if(testMode.equals("auto")){
			testIntent = new Intent(Intent.ACTION_VIEW);
			if(autoTestSelection.equals("chrome")){
				testIntent.setClassName("com.android.chrome", "com.google.android.apps.chrome.Main");  
				testIntent.putExtra(Browser.EXTRA_APPLICATION_ID,"BatteryBenchmark");
			}
	        keepScreenOn(context,true);
		}else{
			//Manual test.
			if(testManualScreenSwitch) keepScreenOn(context,true);
		}
		
		stopFlag = false;
		pausedFlag = false;
		
		manualTestStartFlag = false;
		int arrayPointer = 0;
		
		//Create notification.
		Intent itPeriodicalTask = new Intent(this, PeriodicalTask.class);
		createNotification("Test start!","Test is running, click here to pause.",itPeriodicalTask);
		
		while(!stopFlag){
			//Stop condition.
			if (testFinishMode.equals("time")) {
				if ((new Date().getTime() - startTime.getTime()) / 1000 >= testDuration * 60) break;
			} else if (testFinishMode.equals("percent")) {
				if (batteryLevel * 100 / batteryScale <= testEndPercentage) break;
			}
			//If battery level is less then 5%, stop.
			if (batteryLevel * 100 / batteryScale <= 5) break;
			
			
			//If pasued, then don't do anything.
			if(pausedFlag) continue;
			
			
        	//if test is manual test
			if (testMode.equals("manual")) {
				//Prevent run several times.
				if(!manualTestStartFlag){
					manualTestStartFlag = true;
					Intent it = new Intent();
					ComponentName comp = new ComponentName(testManualAppPackage, testManualAppClass);
					it.setComponent(comp); 
					it.setAction(Intent.ACTION_VIEW); 
					startActivity(it);
				}
			}
			else{
				//Auto test here.
				if (autoTestSelection.equals("chrome")){
					if (arrayPointer == websiteList.size()) {
						arrayPointer = 0;
					}
					testIntent.setData(Uri.parse(websiteList.get(arrayPointer)));
					startActivity(testIntent);

				}

				if (autoTestSelection.equals("googleMap")) {
					if (arrayPointer == addressList.size()) {
						arrayPointer = 0;
					}
					testIntent.setData(Uri.parse("geo:0,0?q=" + addressList.get(arrayPointer) + "?z=20"));
					startActivity(testIntent);

				}
				arrayPointer++;

				try {
					Thread.sleep(1000 * testInterval);
				} catch (Exception e) {

				}
			}
		}

		keepScreenOn(context,false);
        unregisterReceiver(receiver);
        
        Intent itService = new Intent(getApplicationContext(), BatteryService.class);
        itService.addCategory("BatteryServiceTAG");
    	stopService(itService);
    	
    	//If stop by user, then finish this activity.
		if(stopFlag){
			finish();
			stopFlag = true;
			return;
			
		}
		
		//TODO when stopFlag==true, it means user use go back button after test finishing.
		
		//Create finish notification.
		Intent itMainActivity = new Intent(this, MainActivity.class);
		createNotification("Test finished!","Test is finished, click here to go back.",itMainActivity);

	}
	
	
	//Receive the battery level which can detemine stop running.
    public class BatteryConsumptionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			batteryLevel = bundle.getInt("BATTERY_COMSUMPTION_LEVEL");
			batteryScale = bundle.getInt("BATTERY_COMSUMPTION_SCALE");
		}
    	
    }
	
    public void continueTask(View v){
    	if (testMode.equals("manual")){
    		manualTestStartFlag = false;
    	}
    	pausedFlag = false;
		//Create notification.
		Intent itPeriodicalTask = new Intent(this, PeriodicalTask.class);
		createNotification("Test running!","Test is running, click here to pause.",itPeriodicalTask);

    }
    
    public void stopTask(View v){
    	stopFlag = true;
    	
    }
    
	@SuppressLint("NewApi")
	public void createNotification(String title, String content, Intent it){
        String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)getSystemService(svcName);
        
        Context context = getApplicationContext();
        Intent newIntent = it;
        PendingIntent newPendingIntent=PendingIntent.getActivity(context, 0, newIntent, 0);
        CharSequence charseq = title;
        Notification noti = new Notification.Builder(context)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(newPendingIntent)
        .setTicker(charseq)
        .build(); 
       noti.defaults=Notification.DEFAULT_SOUND;
       notificationManager.notify(1,noti);
    }
    
    
	//Function to keep screen on. If parameter is true then keep it on.
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
