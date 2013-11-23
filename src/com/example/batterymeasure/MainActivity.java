package com.example.batterymeasure;

import java.util.ArrayList;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ArrayList<String> batteryConsumptionTime = new ArrayList<String>();
	private ArrayList<Integer> batteryConsumptionLevel = new ArrayList<Integer>();
	private ArrayList<Integer> batteryConsumptionScale = new ArrayList<Integer>();
	RadioGroup testMode;
	RadioGroup testSelect;
	EditText runningTimeSetting;
	EditText runningTimeInterval;
	EditText runningStopPercentage;
	
	private String manualPackageName;
	private String manualClassName;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Prevent system gets into sleep mode.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        testMode=(RadioGroup)findViewById(R.id.radioGroup);
        testSelect=(RadioGroup)findViewById(R.id.radioGroupTestSelect);
        runningTimeSetting=(EditText)findViewById(R.id.editRunningTime);
        runningTimeInterval=(EditText)findViewById(R.id.editRunningTimeInterval);
        runningStopPercentage=(EditText)findViewById(R.id.editStopBatteryPercentage);
        Context context = getApplicationContext();
        //keepScreenOn(context,true);
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @SuppressLint("NewApi")
	public void createNotification(){
        String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)getSystemService(svcName);
        
        Context context = getApplicationContext();
        //notification;
        Intent newIntent =new Intent(this, MainActivity.class);
      
        PendingIntent newPendingIntent=PendingIntent.getActivity(MainActivity.this, 0, newIntent, 0);
        CharSequence charseq = "Test is over";
        Notification noti = new Notification.Builder(context)
        .setContentTitle("Title" )
        .setContentText("Test")
        .setSmallIcon(R.drawable.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(newPendingIntent)
        .setTicker(charseq)
        .build(); 
       notificationManager.notify(1,noti);
    }
    
    public void toastShow(String msg){
        Context context = this;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration); int offsetX = 0;
        int offsetY = 0;
        toast.setGravity(Gravity.BOTTOM, offsetX, offsetY);
        toast.show();
    }
    
    
    public void getBatteryInfo() {
    	batteryConsumptionTime.clear();
    	batteryConsumptionLevel.clear();
    	batteryConsumptionScale.clear();
    	BatteryConsumptionReceiver receiver = new BatteryConsumptionReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.battery");
    	this.registerReceiver(receiver, filter);
    	//tv.setText("abc");

    }
    
    public void viewCurve(View v) {
    	Intent intent = new Intent(this, BatteryCurve.class);
    	
    	int batteryArraySize = batteryConsumptionLevel.size();
    	if(batteryArraySize==0){	
    		new AlertDialog.Builder(this)
            .setMessage("You should start test first.")
            .setPositiveButton("OK",
                            new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialoginterface, int i){
                                        //
                                    }
                            })
            .show();
    		return;
    	}
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
    	intent.putExtra("TASK_INTERVAL", runningTimeInterval.getText().toString());
    	intent.putExtra("TASK_TYPE", taskType);
    	intent.putExtra("TASK_MODE", taskMode);
    	startActivity(intent);
    }
  
    
    // Called when the user clicks start test button.
    String taskType="";
    String taskMode="";
    public void startTest(View view){
    	
    	if(testSelect.getCheckedRadioButtonId()==R.id.radioButtonTestSelectWebsite){
    		taskType="visitWebsite";
    	}else if(testSelect.getCheckedRadioButtonId()==R.id.radioButtonTestSelectMap){
    		taskType="searchAddress";
    	}else{
    		taskType="manual";
    	}
    	
    	if(testMode.getCheckedRadioButtonId()==R.id.radioButtonTestForTime){
    		taskMode="StopByTime";
    	}else{
    		taskMode="StopByPercentage";
    	}
    	
    	getBatteryInfo();
    	Intent intent = new Intent(this, PeriodicalTask.class);
    	int runningTime = Integer.parseInt(runningTimeSetting.getText().toString());
    	int runningInterval = Integer.parseInt(runningTimeInterval.getText().toString());
    	int runningPercentage = Integer.parseInt(runningStopPercentage.getText().toString());

    	toastShow("Test start!");
    	intent.putExtra("RUNNING_INTERVAL", runningInterval);
    	intent.putExtra("RUNNING_TIME", runningTime);
    	intent.putExtra("RUNNING_PERCENTAGE", runningPercentage);
    	intent.putExtra("TASK_TYPE", taskType);
    	intent.putExtra("TASK_MODE", taskMode);
    	intent.putExtra("Manual_package_name", manualPackageName);
    	intent.putExtra("Manual_class_name", manualClassName);
    	
    	ToggleButton screenSwitch = (ToggleButton)findViewById(R.id.toggleButtonScreenSwitch);
    	intent.putExtra("ScreenSwitch", screenSwitch.isChecked());
    	
    	startActivity(intent);
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
		}
    	
    }
    
	private void saveBatteryConsumption(String time, int level, int scale){
		batteryConsumptionTime.add(time);
		batteryConsumptionLevel.add(level);
		batteryConsumptionScale.add(scale);
	}
	
	public void selectApp(View v){
		Intent intent = new Intent(this, AppShowActivity.class);
		//startActivity(intent);
		startActivityForResult(intent, 1);  
	}
	
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        switch (resultCode) {  
        case RESULT_OK:  
            Bundle bundle = data.getExtras();  
            String appName = bundle.getString("appName");  
            RadioButton userSelectApp = (RadioButton)findViewById(R.id.radioButtonTestManualSelect);
            userSelectApp.setText(appName);
            manualPackageName = bundle.getString("PackageName");
            manualClassName = bundle.getString("ClassName");

            testSelect.check(R.id.radioButtonTestManualSelect);
            userSelectApp.setEnabled(true);
           
            break;  
  
        default:  
            break;  
        }  
    }


    
}
