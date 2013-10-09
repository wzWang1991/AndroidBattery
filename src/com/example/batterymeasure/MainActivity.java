package com.example.batterymeasure;

import java.util.ArrayList;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ArrayList<String> batteryConsumptionTime = new ArrayList<String>();
	private ArrayList<Integer> batteryConsumptionLevel = new ArrayList<Integer>();
	private ArrayList<Integer> batteryConsumptionScale = new ArrayList<Integer>();
	TextView tv;
	EditText runningTimeSetting;
	EditText runningTimeInterval;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.textView1); 
        runningTimeSetting=(EditText)findViewById(R.id.editText1);
        runningTimeInterval=(EditText)findViewById(R.id.editText2);
        
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
    	//batteryConsumptionTime.clear();
    	//batteryConsumptionLevel.clear();
    	//batteryConsumptionScale.clear();
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
    	
    	int batteryArraySize = batteryConsumptionLevel.size();
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
    	startActivity(intent);
    }
  
    
    /** Called when the user clicks the Website button */
    public void visitWebsite(View view) {
    	getBatteryInfo();
    	Intent intent = new Intent(this, PeriodicalTask.class);
    	int runningTime = Integer.parseInt(runningTimeSetting.getText().toString());
    	int runningInterval = Integer.parseInt(runningTimeInterval.getText().toString());
    	toastShow("Test start!");
    	intent.putExtra("RUNNING_INTERVAL", runningInterval);
    	intent.putExtra("RUNNING_TIME", runningTime);
    	intent.putExtra("TASK_TYPE", "visitWebsite");
    	startActivity(intent);

    }
    
    /** Called when the user clicks the GoogleMap button */
    public void searchAddress(View view) {
    	getBatteryInfo();
    	Intent intent = new Intent(this, PeriodicalTask.class);
    	int runningTime = Integer.parseInt(runningTimeSetting.getText().toString());
    	int runningInterval = Integer.parseInt(runningTimeInterval.getText().toString());
    	toastShow("Test start!");
    	intent.putExtra("RUNNING_INTERVAL", runningInterval);
    	intent.putExtra("RUNNING_TIME", runningTime);
    	intent.putExtra("TASK_TYPE", "searchAddress");
    	startActivity(intent);

    }
    
    public void getResult(View view){
    	createNotification();
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
