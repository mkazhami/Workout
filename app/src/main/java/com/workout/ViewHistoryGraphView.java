package com.workout;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ViewHistoryGraphView extends Activity{

	String exerName;
	ArrayList<Pair<String, String>> sets;

    DBAdapter db;
    String[] dates;
    String[] values;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_history_exercise);

        Bundle bundle = this.getIntent().getExtras();
        exerName = bundle.getString("exerName");

        db = new DBAdapter(this);
        db.open();
        // get all dates and weights for given exercise - store in arraylist to be added to timeseries
        Cursor c = db.getAllRecords(exerName);
        final int rowCount = c.getCount();
        dates = new String[rowCount];
        values = new String[rowCount];
        if(c.moveToFirst()) {
            int counter = 0;
            do {
                // query returns date in first column, value in second
                dates[counter] = c.getString(0);
                values[counter] = c.getString(1);
                counter++;
            } while(c.moveToNext());
        } else {
            Log.d(WorkoutObjects.DBG, "No records were found for exercise " + exerName);
        }
        db.close();
		
		createGraph();
	}
	
	private void createGraph() {
        String[] firstDate = dates[0].split("-");
        String[] lastDate = dates[dates.length - 1].split("-");
        double maxWeight = 0;

        TimeSeries series = new TimeSeries("Line");

        //add weight/date pairs to the graph series
        final int rowCount = dates.length;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        for(int i = 0; i < rowCount; i++) {
            String[] splitDate = dates[i].split("-");
            Date date = new Date(Integer.parseInt(splitDate[0]) - 1900,
                    Integer.parseInt(splitDate[1]) - 1,
                    Integer.parseInt(splitDate[2]));
            Double val = Double.parseDouble(values[i]);
            if(val > maxWeight) { maxWeight = val; }
            series.add(date, val);
        }

        //lowest and highest dates, used to scale the graph
        Date minDate = new Date(Integer.parseInt(firstDate[0]) - 1900,
                Integer.parseInt(firstDate[1]) - 1,
                Integer.parseInt(firstDate[2]));
        Date maxDate = new Date(Integer.parseInt(lastDate[0]) - 1900,
                Integer.parseInt(lastDate[1]) - 1,
                Integer.parseInt(lastDate[2]));

        final double DAY = 81300000;
        double THREEDAYS = DAY * 3;
        double minX = Math.abs(minDate.getTime()) - DAY;
        double maxX = Math.abs(maxDate.getTime()) + DAY;

        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
        dataSet.addSeries(series);
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        //this is to ensure that the starting zoom position is never smaller than what is allowed by the user
        if(maxX - minX > DAY * 5) {
            mRenderer.setXAxisMin(minX);
            mRenderer.setXAxisMax(maxX);
        }
        else {
            mRenderer.setXAxisMin(minX - DAY*2);
            mRenderer.setXAxisMax(maxX + DAY*2);
        }
        mRenderer.setYAxisMin(0);
        if(maxWeight > 1) mRenderer.setYAxisMax(maxWeight + maxWeight/5);
        else mRenderer.setYAxisMax(5); // value slightly greater than 0 that won't mess up the y-axis scaling
        mRenderer.setYLabelsPadding(15f);
		mRenderer.setYLabelsColor(0, Color.GREEN);
        mRenderer.setPanLimits(new double[] {minX, maxX, 0, maxWeight});
		mRenderer.setZoomInLimitX(DAY*5);
		mRenderer.setZoomInLimitY(maxWeight/2);
        mRenderer.setZoomEnabled(true, false);
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.setShowGrid(true);
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setLabelsTextSize(25);
		LinearLayout layout = (LinearLayout) findViewById(R.id.exerciseHistoryGraph);
		layout.addView(ChartFactory.getTimeChartView(this, dataSet, mRenderer, "MM/dd/yyyy"));
	}
}
