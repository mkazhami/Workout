package com.workout;

import java.util.ArrayList;

public class WorkoutObjects {

    public static final String DBG = "Workout";

	public static final int OK_RESULT = 1;
	public static final int BAD_RESULT = 2;
	
	public static final int MAIN_ACTIVITY = 1;
	public static final int CUSTOMIZE_WORKOUT = 2;
	public static final int USE_WORKOUT = 3;
	public static final int EXERCISE_LIST = 4;
	public static final int CHOOSE_EXERCISE = 5;
	public static final int DELETE_EXERCISE = 6;
	public static final int VIEW_HISTORY_MENU = 7;
	public static final int VIEW_HISTORY_EXERCISE = 8;
	public static final String EXER_KEY = "exercise";
	public static final String WORKOUT_EXERCISES_KEY = "exerciseList";
	public static final String WORKOUT_NAME_KEY = "workoutName";

	public static String FOLDER_NAME;
	public final static String FILE_NAME_PREFIX = "workout";
	public final static String EXERCISE_FILE_NAME = "exercises.txt";
	public final static String WORKOUT_FILE_NAME = "workouts.txt";
	public final static String RECORD_FILE_NAME = "records.txt";
    public final static String FULL_RECORD_FILE_NAME = "full_records.txt";
	
	public static WorkoutList workoutList;
	public static ArrayList<String> workoutNamesList;
	public static ArrayList<Exercise> exerciseList;
	public static ArrayList<String> exerciseNamesList; //in alphabetical order
	public static ArrayList<ExerciseRecord> recordList;
    public static ArrayList<Pair<String, ArrayList<Pair<String, String>>>> fullRecordList;

	
	
}
