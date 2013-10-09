package com.example.batterymeasure;

import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

public class PeriodicalTask extends Activity implements Runnable {
	private String taskType;
	private int runningTime;
	private int runningInterval;
	private Date startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periodical_task);
		Intent intent = getIntent();
		taskType = intent.getStringExtra("TASK_TYPE");
		runningTime=intent.getIntExtra("RUNNING_TIME", 2);
		runningInterval=intent.getIntExtra("RUNNING_INTERVAL", 5);
		startTime = new Date();
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.periodical_task, menu);
		return true;
	}

	@Override
	public void run() {
		
    	ArrayList<String> addressList = new ArrayList<String>();
    	addressList.add("Zhangzhou Fujian China");
    	addressList.add("Columbia University 116th Street and Broadway New York");
    	addressList.add("Shanghai Jiaotong University");
    	
    	ArrayList<String> websiteList = new ArrayList<String>();
    	websiteList.add("http://www.google.com");
    	websiteList.add("http://www.myengineerlife.com");
    	websiteList.add("http://www.cnn.com");
    	
    	Intent visitWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    	visitWebsite.setClassName("com.android.chrome", "com.google.android.apps.chrome.Main");  
    	visitWebsite.putExtra(Browser.EXTRA_APPLICATION_ID,"BatteryLife");
    	
    	int addressPointer=0;
        Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=211W 108th Street New York"));
        while(true){
        	if((new Date().getTime()-startTime.getTime())/1000>=runningTime*60) break;
    		searchAddress.setData(Uri.parse("geo:0,0?q="+addressList.get(addressPointer)+"?z=20"));
    		visitWebsite.setData(Uri.parse(websiteList.get(addressPointer)));
    		addressPointer++;
    		if(taskType.equals("visitWebsite")){
    			startActivity(visitWebsite);  
    		}
    		if(taskType.equals("searchAddress")){
    			startActivity(searchAddress); 
    		}

			try{
				Thread.sleep(1000*runningInterval);
			}catch(Exception e)
			{
				
			}
	   		if(addressPointer==addressList.size()){
    			addressPointer=0;
    			//break;
    		}
		}
        
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
       notificationManager.notify(1,noti);
    }

}
