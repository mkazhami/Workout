package com.example.workout;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UseWorkout extends Activity{

	private Workout workout;
	
	TableLayout workoutTable;
	
	ArrayList<ExerciseRecord> oldRecords;
	ArrayList<ExerciseRecord> records;
	public int exerCount = 0;
	
	//lists to hold the data that is to be recorded
	//also keep track of which EditText/TextView corresponds to each exercise or set inside an exercise
	final ArrayList<Pair<EditText, Integer>> editTextList = new ArrayList<Pair<EditText, Integer>>();
	final ArrayList<Pair<TextView, Integer>> textViewList = new ArrayList<Pair<TextView, Integer>>();
	final ArrayList<String> textViewNameList = new ArrayList<String>();
	final ArrayList<String> textViewSetsList = new ArrayList<String>();
	final ArrayList<String> textViewRepsList = new ArrayList<String>();
	final ArrayList<String> textViewInfoList = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.use_workout);
		
		Bundle bundle = this.getIntent().getExtras();
		workout = new Workout();
		workout.setName(bundle.getString(WorkoutObjects.WORKOUT_NAME_KEY));
		ArrayList<Exercise> list = bundle.getParcelableArrayList(WorkoutObjects.WORKOUT_EXERCISES_KEY);
		workout.setExercises(list);
		
		workoutTable = (TableLayout) findViewById(R.id.useWorkoutTable);
		workoutTable.removeAllViews();
		
		//create records for each exercise in the workout
		records = new ArrayList<ExerciseRecord>();
		for(Exercise e: workout.getExercises()) {
			ExerciseRecord record = new ExerciseRecord(e.getName(), Integer.parseInt(e.getSets()));
			records.add(record);
		}

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
		//fill the table with the exercises
		for(Exercise e: workout.getExercises()) {
			TableRow row = new TableRow(this);
			row.setBackgroundResource(R.drawable.cell_shape);
			row.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			TextView name = new TextView(this);
			textViewList.add(new Pair<TextView, Integer>(name, exerCount));
			textViewNameList.add(e.getName());
			textViewSetsList.add(e.getSets());
			textViewRepsList.add(e.getReps());
			textViewInfoList.add(e.getInfo());

            //set width of name TextView to be reasonable size depending on screen
            name.setWidth(width / 6);
			name.setPadding(10, 40, 30, 40);
			name.setGravity(Gravity.CENTER);
			name.setText(e.getName());
			row.addView(name);
			//create the EditTexts for weight recording - one for each set
			for(int i = 0; i < Integer.parseInt(e.getSets()); i++) {				
				/*TextView divider = new TextView(this);
				divider.setBackgroundColor(Color.BLACK);
				divider.setWidth(2);
                divider.setHeight(row.getHeight());
				divider.setPadding(50,40,50,40);
				row.addView(divider);*/
				
				EditText set = new EditText(this);
				set.setTextSize(14f);
				set.setWidth(150);
				set.setGravity(Gravity.CENTER);
                set.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				editTextList.add(new Pair<EditText, Integer>(set, (Integer) (exerCount*100 + i)));
				row.addView(set);
				//creating dividers between EditTexts - looks better, not necessary
				/*if(i == Integer.parseInt(e.getSets()) -1) {
					TextView divider2 = new TextView(this);
					divider2.setBackgroundColor(Color.BLACK);
					divider2.setWidth(2);
					divider2.setPadding(50,40,50,40);
					row.addView(divider2);
				}*/
			}
			HorizontalScrollView sv = new HorizontalScrollView(this);
			sv.addView(row);
			workoutTable.addView(sv, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			exerCount++;
		}
		
		//set each exercise title (TextView) to be long-clickable
		//long-clicking will bring up extra information about the exercise
		for(Pair<TextView, Integer> tvPair : textViewList) {
			final int pos = tvPair.getR();
			TextView tv = tvPair.getL();
			tv.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putString("name", textViewNameList.get(pos));
					bundle.putString("sets", textViewSetsList.get(pos));
					bundle.putString("reps", textViewRepsList.get(pos));
					bundle.putString("info", textViewInfoList.get(pos));
					
					android.app.FragmentManager fm = UseWorkout.this.getFragmentManager();
					ExerciseInfoFrag f = new ExerciseInfoFrag();
					f.setArguments(bundle);
					f.show(fm, "dialog");
					return false;
				}
			});
		}
	}
	
	public ExerciseRecord getRecord(int code) {
		return records.get(code);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    getMenuInflater().inflate(R.menu.use_workout, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Context context_use_workout = this;
	    switch (item.getItemId()) {
	        case R.id.action_save:
	        	//confirmation dialog
	        	new AlertDialog.Builder(this)
	            	.setIcon(android.R.drawable.ic_dialog_alert)
	            	.setTitle("Record Workout")
	            	.setMessage("Are you sure you want to record this workout?\nNo changes can be made after it is recorded.")
	            	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            		@Override
	            		public void onClick(DialogInterface dialog, int which) {
	            			int max = 0;
	            			int maxCode = 0;
	            			//Pair<EditText, Integer> maxPair;
	            			//when recording the workout, first get all of the weights and add them to the records
	            			for(Pair<EditText, Integer> pair: editTextList) {
	            				final int code = pair.getR();
	            				//int exerCode = (int) Math.floor(code/100);
	            				String weight = pair.getL().getText().toString();
	            				if (weight.length() > 0) {
	            					int weightInt = Integer.parseInt(weight);
		            				if (weightInt > max) {
		            					max = weightInt;
		            					maxCode = code;
		            					//maxPair = pair;
		            				}
	            				}
	            			}
	            			if (max > 0) getRecord((int) Math.floor(maxCode/100)).recordSet(maxCode%100, Integer.toString(max));
	            			FileManagement.mergeRecordList(records);
	            			Toast.makeText(context_use_workout, "Recorded Workout!", Toast.LENGTH_SHORT).show();
	            			finish();
	            		}
	            	})
	            	.setNegativeButton("No", null)
	            	.show();
	            return true;
	        case R.id.action_exit:
	        	//confirmation dialog
	        	new AlertDialog.Builder(this)
	            	.setIcon(android.R.drawable.ic_dialog_alert)
	            	.setTitle("Discard Changes")
	            	.setMessage("Are you sure you want to exit and discard all recorded sets?")
	            	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            		@Override
	            		public void onClick(DialogInterface dialog, int which) {
	            			Toast.makeText(context_use_workout, "Discarded Workout!", Toast.LENGTH_SHORT).show();
	            			finish();
	            		}
	            	})
	            	.setNegativeButton("No", null)
	            	.show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}