package com.example.batterymeasure;

import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class PeriodicalTask extends Activity implements Runnable {
	private String taskType;
	private int runningTime;
	private Date startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_periodical_task);
		Intent intent = getIntent();
		taskType = intent.getStringExtra("TASK_TYPE");
		runningTime=intent.getIntExtra("RUNNING_TIME", 2);
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
    	addressList.add("Columbia University New York");
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
				Thread.sleep(5000);
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
		
	}

}
