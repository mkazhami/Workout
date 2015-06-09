package com.workout;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExerciseListAdapter extends BaseAdapter {

	ExerciseListFrag context;
	static ArrayList<String> list;
    DBAdapter db;
	
	public ExerciseListAdapter(ExerciseListFrag context, ArrayList<String> arrayList) {
		this.context = context;
		list = arrayList;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		if(view == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.simplerow, parent, false);
		}

        db = new DBAdapter(parent.getContext());
		
		final String name = list.get(index);
		
		TextView chooseExercise = (TextView) view.findViewById(R.id.exerciseListRow);
		chooseExercise.setText(name);
		ImageButton deleteExercise = (ImageButton) view.findViewById(R.id.deleteExercise);
		
		chooseExercise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Exercise exercise = new Exercise();
				exercise.setName(name);
				exercise.setReps("");
				exercise.setSets("");
				
				android.app.FragmentManager fm = context.getFragmentManager();
                CustomizeExercisePartial customizeExerciseDialog = new CustomizeExercisePartial();
				Bundle bundle = new Bundle();
                bundle.putParcelable(WorkoutObjects.EXER_KEY, exercise);
                bundle.putInt("position", -1);
                customizeExerciseDialog.setArguments(bundle);
                customizeExerciseDialog.show(fm, "customize_exercise_partial");
                context.dismiss();
			}
		});
		
		deleteExercise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                //TODO: NO MORE WRITING TO FILE - ONLY DB
                db.open();
                db.deleteExercise(name);
                db.close();
				ExerciseListFrag.removeFromList(name);
            }
		});
		
		return view;
	}

}
