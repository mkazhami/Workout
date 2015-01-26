package com.example.workout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
                final int pos = position;
                //TODO: bring up an option to view a graph or a list of records
                CharSequence items[] = new CharSequence[] {"List", "Graph"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewHistoryMenu.this);
                builder.setTitle("Record Representation");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if(which == 0) { //bring up list view
                            Intent intent = new Intent(ViewHistoryMenu.this, ViewHistoryListView.class);
                            Bundle extras = new Bundle();
                            extras.putString("exerName", WorkoutObjects.recordList.get(pos).getName());
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                        else if (which == 1) { //bring up graph view
                            Intent intent = new Intent(ViewHistoryMenu.this, ViewHistoryGraphView.class);
                            Bundle extras = new Bundle();
                            extras.putString("exerName", WorkoutObjects.recordList.get(pos).getName());
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
			}
		});

	}
}
