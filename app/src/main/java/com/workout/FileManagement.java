package com.workout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

public class FileManagement {
	
	/******************** Write to/read from workout/exercise text files ********************/

	public static void writeWorkoutFile() {
		try {
    		File wFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.WORKOUT_FILE_NAME);
			OutputStream out = new FileOutputStream(wFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			if(!wFile.exists()) {
        		wFile.createNewFile();
        	}
			int size = WorkoutObjects.workoutList.getSize();
			for(int i = 0; i < size; i++){ 
				Workout w = WorkoutObjects.workoutList.getWorkout(i);
				bw.write(w.convertToJson());
				bw.newLine();
			}
			bw.close();
		}
    	catch (FileNotFoundException e) { e.printStackTrace(); }
    	catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void fillWorkoutList() {
		if(WorkoutObjects.workoutList.getSize() != 0) return;
		File wFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.WORKOUT_FILE_NAME);
		if (!(wFile.exists())) {
			try { wFile.createNewFile(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
		else {
			try {
				InputStream in = new FileInputStream(wFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = reader.readLine()) != null) {
					Workout workout = Workout.convertFromJson(line);
					WorkoutObjects.workoutList.addWorkout(workout);
					WorkoutObjects.workoutNamesList.add(workout.getName());
				}
				reader.close();
				in.close();
			} 
			catch (FileNotFoundException e) {	e.printStackTrace(); } 
			catch (IOException e) {	e.printStackTrace(); }
		}
	}
	
	public static void writeExerciseFile() {
		try {
			File eFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.EXERCISE_FILE_NAME);
			OutputStream out = new FileOutputStream(eFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			if(!eFile.exists()) {
				eFile.createNewFile();
			}
			for(String name : WorkoutObjects.exerciseNamesList){ 
				bw.write(name);
				bw.newLine();
			}
			bw.close();
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
    	catch (IOException e) { e.printStackTrace(); }
	}

	public static void fillExerciseList() {
		File eFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.EXERCISE_FILE_NAME);
		if (!(eFile.exists())) {
			try { eFile.createNewFile(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
		else {
			try {
				InputStream in = new FileInputStream(eFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = reader.readLine()) != null) {
					WorkoutObjects.exerciseNamesList.add(line);
				}
				reader.close();
				in.close();
			} 
			catch (FileNotFoundException e) {	e.printStackTrace(); } 
			catch (IOException e) {	e.printStackTrace(); }
		}
	}
	
	/***************************** ADD TO GLOBAL LIST FUNCTIONS ******************************/

	//adds a new workout in lexicographical order to the global workout names list
	public static void addGlobalWorkout(Workout workout) {
		String name = workout.getName();
		int count = 0;
		//place the new workout name in the proper (alphabetical) position
		for(String workoutName : WorkoutObjects.workoutNamesList) {
			if(name.compareTo(workoutName) < 0) {
				WorkoutObjects.workoutNamesList.add(count, name);
				break;
			}
			count++;
		}
		//if the string is greater than all the strings in the list, place it at the end
		if(count >= WorkoutObjects.workoutNamesList.size()) {
			WorkoutObjects.workoutNamesList.add(name);
		}
	}
	
	public static boolean addGlobalExercise(String name) {
		//update the list with the given exercise
		int count = 0;
        name.toLowerCase();
		//place the new exercise name in the proper (alphabetical) position
		for(String exerciseName : WorkoutObjects.exerciseNamesList) {
			if(name.compareTo(exerciseName) == 0) {
				return false;
			}
			else if(name.compareTo(exerciseName) < 0) {
				WorkoutObjects.exerciseNamesList.add(count, name);
				return true;
			}
            count++;
		}
		WorkoutObjects.exerciseNamesList.add(name);
		return true;
	}
	
	public static void removeGlobalExercise(String name) {
		WorkoutObjects.exerciseNamesList.remove(name);
	}
	
	

	/******************************** RECORDS FUNCTIONS *********************************/
	public static void mergeRecordList(ArrayList<ExerciseRecord> erList) {
		for(ExerciseRecord er : erList) {
			String name = er.getName();
			boolean wrote = false;
			for(ExerciseRecord record : WorkoutObjects.recordList) {
				//if an exercise record for this exercise already exists, just add to the existing record
				if(record.getName().equals(name)) {
					for(Pair<String, String> info : er.getSets()) {
						record.recordSet(info);
					}
					wrote = true;
					break;
				}
			}
			//if existing record wasn't found, create a new one in the global record list
			if(!wrote) {
				WorkoutObjects.recordList.add(er);
			}
		}
		FileManagement.writeRecordFile();
	}
	
	public static void writeRecordFile() {
		try {
    		File rFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.RECORD_FILE_NAME);
			OutputStream out = new FileOutputStream(rFile, false);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			if(!rFile.exists()) {
        		rFile.createNewFile();
        	}
			for(ExerciseRecord er : WorkoutObjects.recordList) {
				bw.write(er.convertToJson());
				bw.newLine();
			}
			bw.close();
		}
    	catch (FileNotFoundException e) { e.printStackTrace(); }
    	catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void fillRecordList() {
		File rFile = new File(WorkoutObjects.FOLDER_NAME + WorkoutObjects.RECORD_FILE_NAME);
		if (!(rFile.exists())) {
			try { rFile.createNewFile(); } 
			catch (IOException e) { e.printStackTrace(); }
		}
		else {
			try {
				InputStream in = new FileInputStream(rFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = reader.readLine()) != null) {
					ExerciseRecord er = ExerciseRecord.convertFromJson(line);
					WorkoutObjects.recordList.add(er);
				}
				reader.close();
				in.close();
			} 
			catch (FileNotFoundException e) {	e.printStackTrace(); } 
			catch (IOException e) {	e.printStackTrace(); }
		}
	}
	 
}
