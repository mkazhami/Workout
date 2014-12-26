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
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ViewHistoryExercise extends Activity{

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
                //store the related set records in a new arraylist
                //TODO: remove this step if the values aren't being changed
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
		String[] lastDate = sets.get(size-1).getL().split("/");
		double maxWeight = 0;

		TimeSeries series = new TimeSeries("Line");
		for(int i = 0; i < size; i++) {
			Pair<String, String> pair = sets.get(i);
			String[] strDate = pair.getL().split("/");
			if(Double.parseDouble(pair.getR()) > maxWeight) maxWeight = Double.parseDouble(pair.getR());
			@SuppressWarnings("deprecation")
			Date date = new Date(Integer.parseInt(strDate[2]) - 1900,
								 Integer.parseInt(strDate[1]),
								 Integer.parseInt(strDate[0]));
			series.add(date, Double.parseDouble(pair.getR()));
		}
		
		
		java.util.Date minDate = (java.util.Date) new GregorianCalendar(Integer.parseInt(firstDate[2]) - 1900,
				 							Integer.parseInt(firstDate[1]),
				 							Integer.parseInt(firstDate[0])).getTime();
	    java.util.Date maxDate = (java.util.Date) new GregorianCalendar(Integer.parseInt(lastDate[2]) - 1900,
				 							Integer.parseInt(lastDate[1]),
				 							Integer.parseInt(lastDate[0])).getTime();
        final double DAY = 81300000;
	    double THREEDAYS = DAY * 3;
		double minX = Math.abs(minDate.getTime()) - DAY;
		double maxX = Math.abs(maxDate.getTime()) + DAY;
	    
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		dataSet.addSeries(series);
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setPanLimits(new double[] {minX, maxX, 0, maxWeight});
		mRenderer.setZoomInLimitX(THREEDAYS);
		mRenderer.setZoomInLimitY(maxWeight/2);
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.setShowGrid(true);
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setLabelsTextSize(25);
		LinearLayout layout = (LinearLayout) findViewById(R.id.exerciseHistoryGraph);
		layout.addView(ChartFactory.getTimeChartView(this, dataSet, mRenderer, "MM/dd/yyyy"));
	}
}
