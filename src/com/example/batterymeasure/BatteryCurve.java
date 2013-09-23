package com.example.batterymeasure;

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
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;

public class BatteryCurve extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		// get battery data
		String[] batteryRecordTime = it.getStringArrayExtra("BATTERY_TIME");
		int[] batteryLevel = it.getIntArrayExtra("BATTERY_LEVEL");
		int[] batteryScale = it.getIntArrayExtra("BATTERY_SCALE");

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date startTime = new Date();
		Date stopTime = new Date();
		try {
			startTime = (Date) df.parse(batteryRecordTime[0]);
			stopTime = (Date) df
					.parse(batteryRecordTime[batteryRecordTime.length - 1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int totalTestTime = (int) ((stopTime.getTime() - startTime.getTime()) / 1000);
		int[] xLabel = new int[batteryRecordTime.length];
		double[] yLabel = new double[batteryRecordTime.length];

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

		setContentView(chart);
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
		renderer.setAxisTitleTextSize(20);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battery_curve, menu);
		return true;
	}

}
