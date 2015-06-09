package com.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateExercise extends DialogFragment{

	private EditText editName;
	private String name;
	
	private int caller;


	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		caller = args.getInt("caller");
		AlertDialog.Builder createExercise = new AlertDialog.Builder(getActivity());
    	final View view = getActivity().getLayoutInflater().inflate(R.layout.create_exercise, null);
    	createExercise.setView(view);


    	editName = (EditText) view.findViewById(R.id.CreateExerciseNameField);
    	editName.addTextChangedListener(new TextWatcher() {
    		public void afterTextChanged(Editable s) {
    			name = s.toString();
    		}
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
    	});
    	
    	
    	createExercise.setTitle("Create Exercise");
    	createExercise.setPositiveButton("Done", new PositiveButtonClickListener());
        createExercise.setNegativeButton("Cancel", new NegativeButtonClickListener());
    	return createExercise.create();
	}
	
	class PositiveButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            //TODO: NO MORE WRITING TO FILE - ONLY DB
        	if(name == null || name.equals("")) {
        		Toast.makeText(getActivity(), "Invalid name!", Toast.LENGTH_SHORT).show();
        	}
        	else if(!FileManagement.addGlobalExercise(name)) {
        		Toast.makeText(getActivity(), "Exercise name already exists!", Toast.LENGTH_SHORT).show();
        	}
        	else{
                DBAdapter db = new DBAdapter(getActivity());
                db.open();
                long result = db.insertExercise(name);
                if(result < 1) {
                    Log.d("WORKOUT", "FAILED TO INSERT");
                }
                db.close();
        		FileManagement.writeExerciseFile();
        		if(caller == WorkoutObjects.EXERCISE_LIST) ExerciseListFrag.notifyChange();
            	dialog.dismiss();
        	}
        }
    }
    
    class NegativeButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.dismiss();
        }
        
    }
}
