package edu.columbia.batterybenchmark;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadTestData extends Activity {
	private final String UPLOAD_URL="http://battery.myengineerlife.com/upload/upload.php";
	
	private String testData;
	private String testMode;
	private String testTime;
	private String testInterval;
	private String testManualAppName;
	private String autoTestSelection;
	private boolean testManualScreenSwitch;
	
	
	private EditText editTestData;
	private EditText editDescription;
	private TextView textDevice;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_test_data);
		
		Intent it=getIntent();

		testData = it.getStringExtra("UploadTestData");
		testMode = it.getStringExtra("TestMode");
		testTime = it.getStringExtra("TestTime");
		testInterval = it.getStringExtra("TestInterval");
		testManualAppName = it.getStringExtra("TestManualAppName");
		autoTestSelection = it.getStringExtra("AutoTestSelection");
		testManualScreenSwitch = it.getBooleanExtra("TestManualScreenSwitch", true);
		
		editTestData=(EditText)findViewById(R.id.editText_TestData);
		editTestData.setText(testData);
		editDescription=(EditText)findViewById(R.id.editText_Description);
		
		if(testMode.equals("manual")) editDescription.setText(testMode+" "+testManualAppName);
		else editDescription.setText(testMode+" "+autoTestSelection);
		
		textDevice = (TextView)findViewById(R.id.textViewDevice);
		textDevice.setText(android.os.Build.MODEL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_test_data, menu);
		return true;
	}
	
	public void uploadData(View view){
		try {
			String submitMessage = new SendRegistrationTask().execute().get();
			toastShow(submitMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}
	
	private class SendRegistrationTask extends AsyncTask<String, String, String> {
	    protected void onPreExecute() {
	        // prepare the request
	    }

	    protected String doInBackground(String... aurl) {
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(UPLOAD_URL);
		    String descriptionFromUser=editDescription.getText().toString();
		    String responseBody = null;
		    try {
		    	
		        
		    	String model=android.os.Build.MODEL;
		    	int brightness = getScreenBrightness(getApplicationContext());
		    	String brightnessString = Integer.toString(brightness);
		    	if(testMode.equals("manual") && !testManualScreenSwitch)
		    		brightnessString = "OFF";
		    	
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("description", descriptionFromUser));
		        nameValuePairs.add(new BasicNameValuePair("testdata", testData));
		        nameValuePairs.add(new BasicNameValuePair("testtime", testTime));
		        nameValuePairs.add(new BasicNameValuePair("model", model));
		        nameValuePairs.add(new BasicNameValuePair("brightness", Integer.toString(brightness)));
		        nameValuePairs.add(new BasicNameValuePair("interval", testInterval));	        
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        responseBody = EntityUtils.toString(response.getEntity());
		        //toastShow(responseBody);
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
			return responseBody;
	    }

	    protected void onPostExecute(String result) {
	        // Do what you want after the request has been handled
	    }
	}
	
	public static int getScreenBrightness(Context x) {
	    int value = 0;
	    ContentResolver cr = x.getContentResolver();
	    try {
	        value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
	    } catch (SettingNotFoundException e) {
	        
	    }
	    return value;
	}
	
    public void toastShow(String msg){
        Context context = this;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration); int offsetX = 0;
        int offsetY = 0;
        toast.setGravity(Gravity.BOTTOM, offsetX, offsetY);
        toast.show();
    }
	

}
