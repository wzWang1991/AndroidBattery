package edu.columbia.batterybenchmark;



import java.util.ArrayList;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ArrayList<String> batteryConsumptionTime = new ArrayList<String>();
	private ArrayList<Integer> batteryConsumptionLevel = new ArrayList<Integer>();
	private ArrayList<Integer> batteryConsumptionScale = new ArrayList<Integer>();
	
	private boolean resultViewed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		updateView();
		resultViewed = true;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent intent = new Intent(this, SettingsActivity.class);
	    		startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		updateView();
	}
	
	public void startTest(View v){
		
		//Make sure user will not restart it before view the result.
		if(!resultViewed){
			new AlertDialog.Builder(this)
			.setMessage("You haven't view the result of last test. Please view it first.")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							//
						}
					}).show();
			return;
		}
		resultViewed = false;
		
		//Get preference first.
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String testMode = sharedPreferences.getString("pref_testMode", "auto");
		String autoTestSelection = sharedPreferences.getString("pref_autoTestSelection", "chrome");
		String testFinishMode = sharedPreferences.getString("pref_testFinishMode", "time");
		String testDuration = sharedPreferences.getString("pref_testTime", "5");
		String testEndPercentage = sharedPreferences.getString("pref_testPercent", "20");
		String testInterval = sharedPreferences.getString("pref_testInterval", "5");
		
		String testManualAppName = sharedPreferences.getString("pref_manual_app_display_name", "");
		String testManualAppPackage = sharedPreferences.getString("pref_manual_app_display_package", "");
		String testManualAppClass = sharedPreferences.getString("pref_manual_app_display_class", "");
		
		String testWebsite = sharedPreferences.getString("pref_websiteSetting", "http://www.cs.columbia.edu");
		String testAddress = sharedPreferences.getString("pref_addressSetting", "Columbia University, New York");
		boolean testManualScreenSwitch = sharedPreferences.getBoolean("pref_manual_screenSwitch", true);
		
		//Create new intent to send information to period task class.
		Intent intent = new Intent(this, PeriodicalTask.class);
		
		intent.putExtra("TestMode", testMode);
		intent.putExtra("AutoTestSelection", autoTestSelection);
		intent.putExtra("TestFinishMode", testFinishMode);
		intent.putExtra("TestDuration", testDuration);
		intent.putExtra("TestEndPercentage", testEndPercentage);
		intent.putExtra("TestInterval", testInterval);
		intent.putExtra("TestManualAppName", testManualAppName);
		intent.putExtra("TestManualAppPackage", testManualAppPackage);
		intent.putExtra("TestManualAppClass", testManualAppClass);
		intent.putExtra("TestWebsite", testWebsite);
		intent.putExtra("TestAddress", testAddress);
		intent.putExtra("TestManualScreenSwitch", testManualScreenSwitch);
		
		initBatteryInfoReceiver();
		
		startActivity(intent);
		
	}
	
    public void initBatteryInfoReceiver() {
    	batteryConsumptionTime.clear();
    	batteryConsumptionLevel.clear();
    	batteryConsumptionScale.clear();
    	BatteryConsumptionReceiver receiver = new BatteryConsumptionReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.battery");
    	this.registerReceiver(receiver, filter);
    }
	
    public class BatteryConsumptionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String batteryTime = bundle.getString("BATTERY_COMSUMPTION_TIME");
			int batteryLevel = bundle.getInt("BATTERY_COMSUMPTION_LEVEL");
			int batteryScale = bundle.getInt("BATTERY_COMSUMPTION_SCALE");
			saveBatteryConsumption(batteryTime, batteryLevel, batteryScale);
		}
    	
    }

	private void saveBatteryConsumption(String time, int level, int scale){
		batteryConsumptionTime.add(time);
		batteryConsumptionLevel.add(level);
		batteryConsumptionScale.add(scale);
	}
	
	
	public void viewResult(View v){
		Intent intent = new Intent(this, ViewCurve.class);
		int batteryArraySize = batteryConsumptionLevel.size();
		
		//When size =0
		if(batteryArraySize==0){
			new AlertDialog.Builder(this)
					.setMessage("You should start test first.")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
								}
							}).show();
			return;
		}
		resultViewed = true;
		//Convert arraylist to array.
    	int[] batteryLevelArray = new int[batteryArraySize];
    	int[] batteryScaleArray = new int[batteryArraySize];
    	String[] batteryTimeArray  = new String[batteryArraySize];
    	for(int i=0;i<batteryArraySize;i++){
    		batteryLevelArray[i]=(int)batteryConsumptionLevel.get(i);
    		batteryScaleArray[i]=(int)batteryConsumptionScale.get(i);
    		batteryTimeArray[i]=(String)batteryConsumptionTime.get(i);
    	}  	
    	intent.putExtra("BATTERY_TIME", batteryTimeArray);
    	intent.putExtra("BATTERY_LEVEL", batteryLevelArray);
    	intent.putExtra("BATTERY_SCALE", batteryScaleArray);
    	
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String testMode = sharedPreferences.getString("pref_testMode", "");
		String autoTestSelection = sharedPreferences.getString("pref_autoTestSelection", "");

		String testInterval = sharedPreferences.getString("pref_testInterval", "");
		
		String testManualAppName = sharedPreferences.getString("pref_manual_app_display_name", "");
		boolean testManualScreenSwitch = sharedPreferences.getBoolean("pref_manual_screenSwitch", true);

		
		intent.putExtra("TestMode", testMode);
		intent.putExtra("AutoTestSelection", autoTestSelection);
		intent.putExtra("TestInterval", testInterval);
		intent.putExtra("TestManualAppName", testManualAppName);
		intent.putExtra("TestManualScreenSwitch", testManualScreenSwitch);
		
		startActivity(intent);
	}
	
	//Update view when start or resume.
	private void updateView(){
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String testModeValue = sharedPreferences.getString("pref_testMode", "");
		TextView testMode = (TextView)findViewById(R.id.main_testMode);
		testMode.setText(testModeValue);
		
		TextView testApp = (TextView)findViewById(R.id.main_testAPP);
		if(testModeValue.equals("auto")){
			String autoTestSelection = sharedPreferences.getString("pref_autoTestSelection", "");
			if(autoTestSelection.equals("googleMap")){
				testApp.setText("Google Map");
			}
			if(autoTestSelection.equals("chrome")){
				testApp.setText("Chrome");
			}
			
		}else{
			testApp.setText(sharedPreferences.getString("pref_manual_app_display_name", ""));
		}

		
		String testFinishMode = sharedPreferences.getString("pref_testFinishMode", "");
		TextView testEndCondition = (TextView)findViewById(R.id.main_endTest);
		testEndCondition.setText(testFinishMode);
		
		TextView testDurationTitle = (TextView)findViewById(R.id.main_testDuration_title);
		TextView testDuration = (TextView)findViewById(R.id.main_testDuration);
		if(testFinishMode.equals("time")){
			testDurationTitle.setText("Test Duration:");
			testDuration.setText(sharedPreferences.getString("pref_testTime", "")+" min");
		}else{
			testDurationTitle.setText("Test End Percent:");
			testDuration.setText(sharedPreferences.getString("pref_testPercent", "")+"%");
		}
		
		TextView testInterval = (TextView)findViewById(R.id.main_testInterval);
		TextView testIntervalTitle = (TextView)findViewById(R.id.main_testInterval_title);
		if(testModeValue.equals("manual")){
			testInterval.setEnabled(false);
			testIntervalTitle.setEnabled(false);
		}else{
			testInterval.setEnabled(true);
			testIntervalTitle.setEnabled(true);
		}
		testInterval.setText(sharedPreferences.getString("pref_testInterval", "")+" s");

		
	}

}
