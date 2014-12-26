package com.example.workout;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Workout {
	
	private String workoutName;	
	private ArrayList<Exercise> exercises;
	
	public Workout(){
		exercises = new ArrayList<Exercise>();
	}
	
	public ArrayList<Exercise> getExercises() {
		return this.exercises;
	}
	
	public void setExercises(ArrayList<Exercise> list) {
		this.exercises = list;
	}
	
	public void setName(String name){
		this.workoutName = name;
	}
	
	public boolean hasExercise(String name) {
		for(Exercise e: exercises) {
			if(e.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void addExercise(Exercise exercise){
		this.exercises.add(exercise);
	}
	
	public Exercise getExercise(String name) {
		for (Exercise e : exercises) {
			if (e.getName().equals(name)) return e;
		}
		return null;
	}
	
	public String getName() {
		return workoutName;
	}
	
	public String convertToJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static Workout convertFromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Workout.class);
	}
}
