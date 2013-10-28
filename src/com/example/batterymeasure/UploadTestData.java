package com.example.batterymeasure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class UploadTestData extends Activity {
	String testData;
	String testType;
	String testTime;
	EditText editTestData;
	EditText editDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_test_data);
		Intent it=getIntent();
		testData=it.getStringExtra("TEST_DATA");
		testType=it.getStringExtra("TEST_TYPE");
		testTime=it.getStringExtra("TEST_TIME");
		editTestData=(EditText)findViewById(R.id.editText_TestData);
		editTestData.setText(testData);
		editDescription=(EditText)findViewById(R.id.editText_Description);
		editDescription.setText(testType);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_test_data, menu);
		return true;
	}
	
	public void uploadData(View view){
		new SendRegistrationTask().execute();
	}
	
	public void postData() {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://battery.myengineerlife.com/welcome.php");
	    String descriptionFromUser=editDescription.getText().toString();
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("description", descriptionFromUser));
	        nameValuePairs.add(new BasicNameValuePair("testdata", testData));
	        nameValuePairs.add(new BasicNameValuePair("testtime", testTime));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	} 
	
	private class SendRegistrationTask extends AsyncTask<String, String, String> {

	    protected void onPreExecute() {
	        // prepare the request
	    }

	    protected String doInBackground(String... aurl) {
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://www.myengineerlife.com/test/welcome.php");

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("description", testType));
		        nameValuePairs.add(new BasicNameValuePair("testdata", testData));
		        nameValuePairs.add(new BasicNameValuePair("testtime", testTime));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
			return null;
	    }

	    protected void onPostExecute(String result) {
	        // Do what you want after the request has been handled
	    }
	}
	

}
