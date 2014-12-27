package com.example.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.text.DecimalFormat;

public class ExerciseTimer extends DialogFragment {

    private long lastPause = 0;
    Chronometer mChronometer;
    private int code;

    private Button start;
    private Button stop;
    private Button reset;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder exerciseTimer = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.exercise_timer, null);
        exerciseTimer.setView(view);

        Bundle bundle = getArguments();
        code = bundle.getInt("exerCode");

        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);

        start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(mStartListener);
        stop = (Button) view.findViewById(R.id.stop);
        stop.setOnClickListener(mStopListener);
        reset = (Button) view.findViewById(R.id.reset);
        reset.setOnClickListener(mResetListener);

        exerciseTimer.setPositiveButton("Record", new PositiveButtonClickListener());
        exerciseTimer.setNegativeButton("Cancel", new NegativeButtonClickListener());

        return exerciseTimer.create();
    }

    class PositiveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            UseWorkout activity = (UseWorkout) getActivity();
            //get time on timer and record the number of seconds
            String[] parts = mChronometer.getText().toString().split(":");
            int seconds = 0, minutes = 0, hours = 0;
            if(parts.length == 2){
                seconds = Integer.parseInt(parts[1]);
                minutes = Integer.parseInt(parts[0]);
            }
            else if(parts.length == 3){
                seconds = Integer.parseInt(parts[2]);
                minutes = Integer.parseInt(parts[1]);
                hours = Integer.parseInt(parts[1]);
            }
            activity.recordTime(code, seconds + minutes*60 + hours*3600);
            dialog.dismiss();
        }
    }

    class NegativeButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    View.OnClickListener mStartListener = new View.OnClickListener() {
        public void onClick(View v) {
        mChronometer.setBase(SystemClock.elapsedRealtime() + lastPause);
        mChronometer.start();
        }
    };
    View.OnClickListener mStopListener = new View.OnClickListener() {
        public void onClick(View v) {
        lastPause = mChronometer.getBase() - SystemClock.elapsedRealtime();
        mChronometer.stop();
        }
    };
    View.OnClickListener mResetListener = new View.OnClickListener() {
        public void onClick(View v) {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        lastPause = 0;
        }
    };
}
