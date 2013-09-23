package com.example.batterymeasure;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;

public class BatteryCurve extends Activity {

	@Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);

        String[] titles = new String[] { "First", "Second"};

        List x = new ArrayList(); 
        List y = new ArrayList();

        x.add(new double[] { 1, 3, 5, 7, 9, 11} ); 
        x.add(new double[] { 0, 2, 4, 6, 8, 10} );

        y.add(new double[] { 3, 14, 5, 30, 20, 25}); 
        y.add(new double[] { 18, 9, 21, 15, 10, 6});

        XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);

        int[] colors = new int[] { Color.BLUE, Color.GREEN}; 
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND}; 
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "Line Chart Demo", "X", "Y", -1, 12, 0, 35 , Color.WHITE, Color.WHITE);
        View chart = ChartFactory.getLineChartView(this, dataset, renderer);

        setContentView(chart); 
    }

	
	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; 
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i]); 
			double[] xV = (double[]) xValues.get(i); 
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length;

			for (int k = 0; k < seriesLength; k++) 
			{
				series.add(xV[k], yV[k]);
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(fill);
			renderer.addSeriesRenderer(r);
		}
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battery_curve, menu);
		return true;
	}

}
