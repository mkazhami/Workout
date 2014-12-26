package com.example.workout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewHistoryMenu extends Activity{

	ListView exerciseList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_history_menu);
		
		exerciseList = (ListView) findViewById(R.id.exerciseHistoryListView);
		int size = WorkoutObjects.recordList.size();
		String[] names = new String[size];
		for(int i = 0; i < size; i++) {
			names[i] = WorkoutObjects.recordList.get(i).getName();
		}
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, names);
		exerciseList.setAdapter(modeAdapter);
		exerciseList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ViewHistoryMenu.this, ViewHistoryExercise.class);
				Bundle extras = new Bundle();
				extras.putString("exerName", WorkoutObjects.recordList.get(position).getName());
				intent.putExtras(extras);
				startActivity(intent);
			}
		});

	}
}
