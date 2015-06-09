package com.workout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
    DBAdapter db;
	
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
    final ArrayList<String> textViewTimedList = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.use_workout);

        db = new DBAdapter(this);
		
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
            textViewTimedList.add(e.getTimed());

            //set width of name TextView to be reasonable size depending on screen
            name.setWidth(width / 5);
			name.setPadding(10, 40, 30, 40);
			name.setGravity(Gravity.CENTER);
			name.setText(e.getName());
			row.addView(name);
			//create the EditTexts for weight recording - one for each set
			for(int i = 0; i < Integer.parseInt(e.getSets()); i++) {
				EditText set = new EditText(this);
				set.setTextSize(14f);
				set.setWidth(150);
				set.setGravity(Gravity.CENTER);
                if(e.getTimed().equals("y")) {
                    final int count = i;
                    final int exer = exerCount;
                    set.setTextSize(15f);
                    //open timer fragment on touch
                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            android.app.FragmentManager fm = UseWorkout.this.getFragmentManager();
                            ExerciseTimer f = new ExerciseTimer();
                            Bundle b = new Bundle();
                            b.putInt("exerCode", exer*100 + count);
                            f.setArguments(b);
                            f.show(fm, "dialog");
                        }
                    });
                }
                else {
                    set.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                }
				editTextList.add(new Pair<EditText, Integer>(set, (Integer) (exerCount*100 + i)));
				row.addView(set);
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
                    bundle.putString("timed", textViewTimedList.get(pos));
					
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

    public void recordTime(int code, int time) {
        for(Pair<EditText, Integer> pair : editTextList) {
            if(pair.getR() == code) {
                pair.getL().setText(Integer.toString(time) + "s");
                return;
            }
        }
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
                            //TODO: keep track of all weights instead of just max
                            //TODO: NO MORE WRITING TO FILES - ONLY DB
                            int count = 0;
                            // get current time
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                            String date = df.format(c.getTime());
                            db.open();

                            //when recording the workout, first get all of the weights and add them to the records
                            for (Exercise e : workout.getExercises()) {
                                int max = 0;
                                int maxCode = 0;
                                //get the max weight of each exercise
                                for (int i = 0; i < Integer.parseInt(e.getSets()); i++) {
                                    Pair<EditText, Integer> pair = editTextList.get(count);
                                    final int code = pair.getR();
                                    String weight = pair.getL().getText().toString();
                                    weight = weight.replaceAll("s", "");


									if(!weight.equals("") && Integer.parseInt(weight) != 0) {
                                        db.insertRecord(e.getName(), date, Integer.parseInt(weight));
                                    }
                                    else { Log.d(WorkoutObjects.DBG, "Not adding exercise record - no/invalid value"); }

                                    if (weight.length() > 0) {
                                        int weightInt = Integer.parseInt(weight);
                                        if (weightInt > max) {
                                            max = weightInt;
                                            maxCode = code;
                                        }
                                    }
                                    count++;
                                }
                                if (max > 0) {
                                    getRecord((int) Math.floor(maxCode / 100)).recordSet(maxCode % 100, Integer.toString(max));
                                }
                            }
                            FileManagement.mergeRecordList(records);
                            Toast.makeText(context_use_workout, "Recorded Workout!", Toast.LENGTH_SHORT).show();
                            db.close();
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