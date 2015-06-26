package com.workout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewHistoryMenu extends Activity{

	ListView exerciseList;
    DBAdapter db;
    String[] names;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_history_menu);
		
		exerciseList = (ListView) findViewById(R.id.exerciseHistoryListView);
		//int size = WorkoutObjects.recordList.size();
        db = new DBAdapter(this);
        db.open();
        Cursor c = db.getAllExercises();
        int size = c.getCount();
        names = new String[size];
        boolean atLeastOneExercise = false;
        int i = 0;
        while(c.moveToNext()) {
            if (db.getRecordCount(c.getString(0)) > 0) {
                atLeastOneExercise = true;
                names[i] = c.getString(0);
                i++;
            }
        }
        db.close();

        // exit activity if there are no records
        if (!atLeastOneExercise) {
            Toast.makeText(this, "No exercise records were found!", Toast.LENGTH_SHORT).show();
            finish();
        }

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, names);
		exerciseList.setAdapter(modeAdapter);
		exerciseList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                CharSequence items[] = new CharSequence[]{"List", "Graph"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewHistoryMenu.this);
                builder.setTitle("Record Representation");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { //bring up list view
                            Intent intent = new Intent(ViewHistoryMenu.this, ViewHistoryListView.class);
                            Bundle extras = new Bundle();
                            //extras.putString("exerName", WorkoutObjects.recordList.get(pos).getName());
                            extras.putString("exerName", names[pos]);
                            intent.putExtras(extras);
                            startActivity(intent);
                        } else if (which == 1) { //bring up graph view
                            Intent intent = new Intent(ViewHistoryMenu.this, ViewHistoryGraphView.class);
                            Bundle extras = new Bundle();
                            //extras.putString("exerName", WorkoutObjects.recordList.get(pos).getName());
                            extras.putString("exerName", names[pos]);
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
