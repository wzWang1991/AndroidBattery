package com.example.batterymeasure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BatteryCurve extends Activity {

	String[] batteryRecordTime;
	int[] batteryLevel;
	int[] batteryScale;
	Date startTime;
	Date stopTime;
	int totalTestTime;
	
	//xLable is the test time. yLable is Level/Scale at that time.
	int[] xLabel;
	double[] yLabel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		// get battery data
		batteryRecordTime = it.getStringArrayExtra("BATTERY_TIME");
		batteryLevel = it.getIntArrayExtra("BATTERY_LEVEL");
		batteryScale = it.getIntArrayExtra("BATTERY_SCALE");

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		startTime = new Date();
		stopTime = new Date();
		try {
			startTime = (Date) df.parse(batteryRecordTime[0]);
			stopTime = (Date) df
					.parse(batteryRecordTime[batteryRecordTime.length - 1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		totalTestTime = (int) ((stopTime.getTime() - startTime.getTime()) / 1000);
		xLabel = new int[batteryRecordTime.length];
		yLabel = new double[batteryRecordTime.length];

		for (int i = 0; i < batteryRecordTime.length; i++) {
			Date nowTime = new Date();
			try {
				nowTime = (Date) df.parse(batteryRecordTime[i]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int timeLength = (int) ((nowTime.getTime() - startTime.getTime()) / 1000);
			xLabel[i] = timeLength;
			yLabel[i] = (double) batteryLevel[i] / (double) batteryScale[i] * 100;
		}

		String title = "Battery Consumption";

		XYMultipleSeriesDataset dataset = buildDataset(title, xLabel, yLabel);

		int color = Color.GREEN;
		PointStyle style = PointStyle.CIRCLE;
		XYMultipleSeriesRenderer renderer = buildRenderer(color, style, true);

		setChartSettings(renderer, "Battery Consumtpion", "Time(s)", "Battery(%)", 0,
				totalTestTime, 0, 100, Color.WHITE, Color.WHITE);
		View chart = ChartFactory.getLineChartView(this, dataset, renderer);

		//setContentView(chart);
		setContentView(R.layout.activity_battery_curve);
		LinearLayout chartView = (LinearLayout)findViewById(R.id.curveView);
		chartView.addView(chart);
		
	   	//Calculate projected battery life.
		int totalBatteryConsumption = batteryLevel[0]-batteryLevel[batteryLevel.length-1];
		String batteryReport = "";
		batteryReport += "Start time: "+batteryRecordTime[0]+"\n";
		batteryReport += "Stop time: "+batteryRecordTime[batteryRecordTime.length - 1]+"\n";
		batteryReport += "Total test time: "+ totalTestTime + " s\n";
		if(totalBatteryConsumption!=0){
		int projectedTimeInSeconds = totalTestTime/totalBatteryConsumption * 100;
		
		int projectedTimePartMinute = projectedTimeInSeconds/60;
		int projectedTimePartSecond = projectedTimeInSeconds%60;
		
		batteryReport = batteryReport + "\nProjected Battery Life: "+projectedTimePartMinute+" minutes and "+projectedTimePartSecond+" seconds.\n";
		}else{
			batteryReport+="Can't determine the projected battery life.";
		}
		TextView tv = (TextView)findViewById(R.id.textViewCurve);
		tv.setText(batteryReport);
		
		
	}

	protected XYMultipleSeriesDataset buildDataset(String title, int[] xValues,
			double[] yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		XYSeries series = new XYSeries(title);
		int length = xValues.length;
		for (int i = 0; i < length; i++) {
			series.add(xValues[i], yValues[i]);
		}
		dataset.addSeries(series);
		return dataset;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		renderer.addSeriesRenderer(r);

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		// add by wzwang
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(25);
		renderer.setChartTitleTextSize(30);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setAxisTitleTextSize(25);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battery_curve, menu);
		return true;
	}
	
	//Export button click. Export the report to txt file.
	public void exportToFile(View v){
		String report=reportPlainText();
		File file = Environment.getExternalStorageDirectory();
		FileOutputStream fos;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
		String filename=df.format(startTime);
		try {
			fos = new FileOutputStream(file.getAbsolutePath()+"/BatteryReport/"+filename+".txt");
			byte[] buf=report.getBytes();
			fos.write(buf);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Upload button click. Upload the report data to website.
	public void uploadToWebsite(View v){
		
	}
	
	//Generate a report version of plain text. For export or upload.
	public String reportPlainText(){
		String report="";
		report += "#Battery Test Report\n\n";
		report += "Start time:" + batteryRecordTime[0] + "\n";
		report += "End time:" + batteryRecordTime[batteryRecordTime.length-1] +"\n";
		report += "Time duration: " + totalTestTime + "s\n\n";
		for(int i=0;i<batteryRecordTime.length;i++){
			report += "["+batteryRecordTime[i]+"]"+yLabel[i]+"%\n";
		}
		return report;
	}
}
