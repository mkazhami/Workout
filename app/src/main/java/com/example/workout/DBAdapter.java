package com.example.workout;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_RESULT = "result"; // weight or time depending on exercise

    private static final String DATABASE_NAME = "WorkoutDB";
    public static final String TABLE_EXERCISE = "exercises";
    public static final String TABLE_EXERCISE_CREATE = "create table " + TABLE_EXERCISE + " ("
                                        + KEY_ID + " integer primary key autoincrement, "
                                        + KEY_NAME + " VARCHAR not null)";
    public static final String TABLE_EXERCISE_RECORDS = "exercise_records";
    public static final String TABLE_EXERCISE_RECORDS_CREATE = "create table " + TABLE_EXERCISE_RECORDS + " ("
                                        + KEY_ID + " integer not null, "
                                        + KEY_NAME + " VARCHAR not null, "
                                        + KEY_DATE + " date, "
                                        + KEY_RESULT + " integer not null)";
    public static final int DATABASE_VERSION = 2;

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context c) {
        this.context = c;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_EXERCISE_CREATE);
                db.execSQL(TABLE_EXERCISE_RECORDS_CREATE);
            } catch(Exception e){
                Log.d(WorkoutObjects.DBG, "CREATING TABLES FAILED");
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO: implement onUpgrade
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    // insert a new exercise
    public long insertExercise(String name) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        return db.insert(TABLE_EXERCISE, null, cv);
    }

    // insert a new exercise record
    public long insertRecord(String name, String date, int result) {
        // get id of exercise
        Cursor c = db.query(TABLE_EXERCISE, new String[] {KEY_ID}, KEY_NAME + "='" + name + "'", null, null, null, null);
        c.moveToFirst();
        String id = c.getString(0);

        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, Integer.parseInt(id));
        cv.put(KEY_NAME, name);
        cv.put(KEY_DATE, date);
        cv.put(KEY_RESULT, result);
        Log.d(WorkoutObjects.DBG, "putting record for " + name + " into table with id " + id);
        return db.insert(TABLE_EXERCISE_RECORDS, null, cv);
    }

    // delete an exercise
    public boolean deleteExercise(String name) {
        try {
            // first delete all records
            db.delete(TABLE_EXERCISE_RECORDS, KEY_NAME + "='" + name + "'", null);
            // then delete exercise
        } catch(Exception e) {
            Log.i(WorkoutObjects.DBG, "No records to delete for " + name);
            e.printStackTrace();
        }
        return db.delete(TABLE_EXERCISE, KEY_NAME + "='" + name + "'", null) > 0;
    }

    // gets all exercises
    public Cursor getAllExercises() {
        return db.query(TABLE_EXERCISE, new String[]{KEY_NAME}, null, null, null, null, KEY_NAME);
    }

    // gets all exercise records of exercise with name "name"
    public Cursor getAllRecords(String name) {
        return db.query(TABLE_EXERCISE_RECORDS, new String[] {KEY_DATE, KEY_RESULT}, KEY_NAME + "='" + name + "'", null, null, null, null);
    }

    // update exercise
    public boolean updateExercise(String oldName, String newName) {
        //TODO: implement exercise name updating
        return true;
    }

    //update exercise record with new value
    public boolean updateRecord(String name, String date, int value) {
        //TODO: implement exercise record upating - not really important
        return true;
    }

}
