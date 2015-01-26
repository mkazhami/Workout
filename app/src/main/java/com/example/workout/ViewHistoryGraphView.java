package com.example.workout;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ViewHistoryGraphView extends Activity{

	String exerName;
	ArrayList<Pair<String, String>> sets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_history_exercise);
		
		sets = new ArrayList<Pair<String, String>>();
		
		Bundle bundle = this.getIntent().getExtras();
		exerName = bundle.getString("exerName");
		for(ExerciseRecord er : WorkoutObjects.recordList) {
			if(er.getName().equals(exerName)) {
                //store the related set records in a local arraylist
				for(Pair<String, String> pair : er.getSets()) {
					sets.add(new Pair<String, String>(pair.getL(), pair.getR()));
				}
				break;
			}
		}
		
		createGraph();
	}
	
	private void createGraph() {
        int size = sets.size();
        String[] firstDate = sets.get(0).getL().split("/");
        String[] lastDate = sets.get(size - 1).getL().split("/");
        double maxWeight = 0;

        TimeSeries series = new TimeSeries("Line");
        //add weight/date pairs to the graph series
        for (int i = 0; i < size; i++) {
            Pair<String, String> pair = sets.get(i);
            String[] strDate = pair.getL().split("/");
            if (Double.parseDouble(pair.getR()) > maxWeight)
                maxWeight = Double.parseDouble(pair.getR());
            @SuppressWarnings("deprecation")
            Date date = new Date(Integer.parseInt(strDate[2]) - 1900,
                    Integer.parseInt(strDate[1]) - 1,
                    Integer.parseInt(strDate[0]));
            series.add(date, Double.parseDouble(pair.getR()));
        }

        //lowest and highest dates, used to scale the graph
        Date minDate = new Date(Integer.parseInt(firstDate[2]) - 1900,
                Integer.parseInt(firstDate[1]) - 1,
                Integer.parseInt(firstDate[0]));
        Date maxDate = new Date(Integer.parseInt(lastDate[2]) - 1900,
                Integer.parseInt(lastDate[1]) - 1,
                Integer.parseInt(lastDate[0]));

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
