package com.example.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ExerciseInfoFrag extends DialogFragment {
	
	private Button findVideo;
	
	private EditText name;
	private EditText sets;
	private EditText reps;
	private EditText info;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder exerciseInfo = new AlertDialog.Builder(getActivity());
    	final View view = getActivity().getLayoutInflater().inflate(R.layout.exercise_info_frag, null);
    	exerciseInfo.setView(view);
    	
    	name = (EditText) view.findViewById(R.id.exerciseInfoNameField);
    	sets = (EditText) view.findViewById(R.id.exerciseInfoSetsField);
    	reps = (EditText) view.findViewById(R.id.exerciseInfoRepsField);
    	info = (EditText) view.findViewById(R.id.exerciseInfoInfoField);
    	
    	//set values of EditTexts
    	Bundle bundle = getArguments();
    	name.setText(bundle.getString("name"));
    	sets.setText(bundle.getString("sets"));
    	reps.setText(bundle.getString("reps"));
    	info.setText(bundle.getString("info"));
    	
    	//don't want EditTexts to be editable
    	name.setEnabled(false);
    	sets.setEnabled(false);
    	reps.setEnabled(false);
    	info.setEnabled(false);

    	//google search for "[exercise name] exercise example video"
    	findVideo = (Button) view.findViewById(R.id.find_video);
    	findVideo.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View view){
    			String urlName =  name.getText().toString().replaceAll(" ", "%20");
    			Uri uri = Uri.parse("http://google.com/#q=" + urlName + "%20exercise%20example%20video");
    			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    			startActivity(intent);
    		}
    	});
    	
    	
    	return exerciseInfo.create();
	}

}
