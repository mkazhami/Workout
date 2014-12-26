package com.example.workout;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
	private WorkoutList workouts;
	Button addWorkout;
	
	
	private DragSortListView dslv;
	
	private static MyAdapter adapter;
	private static MatrixCursor cursor;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(WorkoutObjects.workoutList == null) WorkoutObjects.workoutList = WorkoutList.getInstance();
		WorkoutObjects.workoutNamesList = new ArrayList<String>();
		WorkoutObjects.exerciseNamesList = new ArrayList<String>();
		WorkoutObjects.recordList = new ArrayList<ExerciseRecord>();

		WorkoutObjects.FOLDER_NAME = getBaseContext().getFilesDir().toString() + "/";

		FileManagement.fillWorkoutList();
		FileManagement.fillExerciseList();
		FileManagement.fillRecordList();
		
		dslv = (DragSortListView) findViewById(R.id.workoutList);
		String[] cols = {"name"};
        int[] ids = {R.id.text};
        adapter = new MyAdapter(this, R.layout.list_item_click_remove, null, cols, ids, 0);
        dslv.setAdapter(adapter);
        if (WorkoutObjects.workoutList != null) {
		    // populate the list of exercises
		    cursor = new MatrixCursor(new String[] {"_id", "name"});
		    int size = WorkoutObjects.workoutList.getSize();
			for (int i = 0; i < size; i++) {
				cursor.newRow()
					  .add(i)
		              .add(WorkoutObjects.workoutList.getWorkout(i).getName());
			}
		    adapter.changeCursor(cursor);
        }
		
		addWorkout = (Button) findViewById(R.id.addWorkout);
		setUpListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_create_exercise:
				android.app.FragmentManager fm = MainActivity.this.getFragmentManager();
				CreateExercise f = new CreateExercise();
				Bundle bundle = new Bundle();
				bundle.putInt("caller", WorkoutObjects.MAIN_ACTIVITY);
				f.setArguments(bundle);
				f.show(fm, "dialog");
				return true;
			case R.id.action_history:
				Intent intent = new Intent(MainActivity.this, ViewHistoryMenu.class);
				startActivity(intent);
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	private void setUpListeners(){
		addWorkout.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				Intent intent = new Intent(MainActivity.this, CustomizeWorkout.class);
				Bundle extras = new Bundle();
				extras.putInt("position", -1);
				intent.putExtras(extras);
				startActivityForResult(intent, WorkoutObjects.CUSTOMIZE_WORKOUT);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case WorkoutObjects.CUSTOMIZE_WORKOUT: 
				if(resultCode == WorkoutObjects.BAD_RESULT) {
					Toast.makeText(this, "Workout not saved!" , Toast.LENGTH_SHORT).show();
				}
				else if (resultCode == WorkoutObjects.OK_RESULT) {
					Toast.makeText(this, "Workout saved!" , Toast.LENGTH_SHORT).show();
				}
		}
	}
	
	
	//checks for items deleted from the list and updates the global workout list
	public static void checkRemoved() {
		ArrayList<Integer> positions = adapter.getCursorPositions();
		ArrayList<String> newWorkoutList = new ArrayList<String>();
		if (cursor != null) {
			cursor.moveToFirst();
	        for (int i = 0; i < positions.size(); i++) {
	        	int cursorPosition = positions.get(i);
	        	cursor.moveToPosition(cursorPosition);
	        	String c = cursor.getString(1);
	        	newWorkoutList.add(c);
	        }
	        ArrayList<String> toRemove = new ArrayList<String>();
	        for(int i = 0; i < WorkoutObjects.workoutList.getSize(); i++) {
	        	if(!newWorkoutList.contains(WorkoutObjects.workoutList.getWorkout(i).getName())) {
	        		toRemove.add(WorkoutObjects.workoutList.getWorkout(i).getName());
	        	}
	        }
	        for(int i = 0; i < toRemove.size(); i++) {
	        	WorkoutObjects.workoutList.removeWorkout(toRemove.get(i));
	        	WorkoutObjects.workoutNamesList.remove(toRemove.get(i));
	        }
		}
		FileManagement.writeWorkoutFile();
	}
	
	
	
	public static void onEdit(Workout workout, int position) {
		ArrayList<Integer> positions = adapter.getCursorPositions();
		MatrixCursor newcursor = new MatrixCursor(new String[] {"_id", "name"});
		//int lastCursorPosition = cursor.getPosition();
		// copy over old cursor items to the new one.
		if (cursor != null) {
			cursor.moveToFirst();
	        for (int i = 0; i < positions.size(); i++) {
	        	int cursorPosition = positions.get(i);
	        	int listPosition = adapter.getListPosition(positions.get(i));
	        	// dont add to new cursor if item has been removed.
	        	if (listPosition == DragSortCursorAdapter.REMOVED) continue;
	        	cursor.moveToPosition(cursorPosition);
	        	String c = cursor.getString(1);
	        	// if its not a new exercise, check to see if the current cursor list position mapping
	        	// matches the one we are working with. If yes, set c to be the new exercise.
	        	if (position >= 0) {
	        		if (position == listPosition ) {
	        			c = workout.getName();
	        		}
	        	}
	        	newcursor.newRow()
	                    .add(listPosition)
	                    .add(c);
	        }
		}
        // add the new row
        if (position < 0 && workout != null) {
        	newcursor.newRow().add(newcursor.getCount()).add(workout.getName());
        }
        
        
        ArrayList<Workout> newWorkouts = new ArrayList<Workout>();
        newcursor.moveToFirst();
        for (int i = 0; i < newcursor.getCount(); i++) {
        	String wname = newcursor.getString(1);
        	Workout w = WorkoutObjects.workoutList.getWorkout(wname);
        	if (w != null) newWorkouts.add(w);
        	else if (i == newcursor.getCount() - 1) newWorkouts.add(workout);
        	if (!newcursor.moveToNext()) break;
        }
         
        adapter.changeCursor(newcursor); 
        cursor = newcursor;
		
        // modify workout to reflect the change
        WorkoutObjects.workoutList.setWorkouts(newWorkouts);
        
	}
	
	
	
	// custom adapter, used to intercept list item touch events.
    private class MyAdapter extends SimpleDragSortCursorAdapter {
        private Context mContext;

        public MyAdapter(Context ctxt, int rmid, Cursor c, String[] cols, int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            View tv = v.findViewById(R.id.text);
            tv.setTag(position);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	int pos = (Integer) v.getTag();
                	Intent intent = new Intent(MainActivity.this, UseWorkout.class);
    				Bundle extras = new Bundle();
    				Workout w = WorkoutObjects.workoutList.getWorkout(pos);
    				extras.putString(WorkoutObjects.WORKOUT_NAME_KEY, w.getName());
    				extras.putParcelableArrayList(WorkoutObjects.WORKOUT_EXERCISES_KEY, w.getExercises());
    				intent.putExtras(extras);
    				startActivityForResult(intent, WorkoutObjects.USE_WORKOUT);
                }
            });
            
            tv.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int pos = (Integer) v.getTag();
                	Intent intent = new Intent(MainActivity.this, CustomizeWorkout.class);
    				Bundle extras = new Bundle();
    				extras.putInt("position", pos);
    				intent.putExtras(extras);
    				startActivityForResult(intent, WorkoutObjects.CUSTOMIZE_WORKOUT);
					return true;
				}
            });
            return v;
        }
    }

}
