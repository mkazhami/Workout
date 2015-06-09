package com.workout;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class ViewHistoryListView extends Activity {

    TableLayout recordTable;
    String exerName;
    ArrayList<Pair<String, String>> sets;
    ArrayList<Pair<String, String>> sortedByDateSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_list);

        Bundle bundle = this.getIntent().getExtras();
        exerName = bundle.getString("exerName");
        setTitle(exerName);

        sets = new ArrayList<Pair<String, String>>();
        for(ExerciseRecord er : WorkoutObjects.recordList) {
            if(er.getName().equals(exerName)) {
                //store the related set records in a local arraylist
                for(Pair<String, String> pair : er.getSets()) {
                    sets.add(new Pair<String, String>(pair.getL(), pair.getR()));
                }
                break;
            }
        }

        //TODO: add all weights for each date, not just max
        //have to change record keeping for that
        sortedByDateSets = new ArrayList<Pair<String, String>>();
        for(Pair<String, String> p: sets) {
            String date = p.getL();
            String record = "Max: " + p.getR();
            sortedByDateSets.add(new Pair<String, String>(date, record));
        }

        //create table
        recordTable = (TableLayout) findViewById(R.id.recordTable);
        recordTable.removeAllViews();

        //get size of screen and determine size of table column
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        for(Pair<String, String> p: sortedByDateSets) {
            TableRow row = new TableRow(this);
            row.setBackgroundResource(R.drawable.cell_shape);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView date = new TextView(this);
            date.setWidth(width / 4);
            date.setPadding(10, 40, 30, 40);
            date.setGravity(Gravity.CENTER);
            date.setText(p.getL());
            row.addView(date);

            HorizontalScrollView sv = new HorizontalScrollView(this);
            TextView record = new TextView(this);
            record.setWidth(width - (width/4) - 80 - (width/14));
            record.setPadding(10, 40, 30, 40);
            record.setGravity(Gravity.CENTER);
            record.setText(p.getR());
            sv.addView(record);
            row.addView(sv);
            recordTable.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_history_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
