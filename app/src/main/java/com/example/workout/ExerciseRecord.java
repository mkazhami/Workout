package com.example.workout;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.gson.Gson;

public class ExerciseRecord {
	
	private String name;
	private ArrayList<Pair<String, String>> sets; //date and weight values - date will be in day/month/year format
	
	public ExerciseRecord(String name, int setsAmount) {
		this.name = name;
		this.sets = new ArrayList<Pair<String, String>>();
	}
	
	public ExerciseRecord(String name, ArrayList<Pair<String, String>> list) {
		this.name = name;
		this.sets = list;
	}

	public void recordSet(String weight) {
		Calendar c = Calendar.getInstance();
		String date = Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/" +
					  Integer.toString(c.get(Calendar.MONTH) + 1) + "/" +
					  Integer.toString(c.get(Calendar.YEAR));
		sets.add(new Pair<String, String>(date, weight));
	}
	
	public void recordSet(int set, String weight) {
		Calendar c = Calendar.getInstance();
		String date = Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/" +
					  Integer.toString(c.get(Calendar.MONTH) + 1) + "/" + //need MONTH + 1 because January = 0
					  Integer.toString(c.get(Calendar.YEAR));
		if(set >= sets.size()) {
			sets.add(new Pair<String, String>(date, weight));
		}
		else{
			sets.remove(set);
			sets.add(set, new Pair<String, String>(date, weight));
		}
	}
	
	public void recordSet(Pair<String, String> info) {
		sets.add(info);
	}
	
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Pair<String, String>> getSets() {
		return this.sets;
	}
	
	public String convertToJson() {
		Gson gson = new Gson();
		return gson.toJson(this, ExerciseRecord.class);
	}
	
	public static ExerciseRecord convertFromJson(String s) {
		Gson gson = new Gson();
		return gson.fromJson(s, ExerciseRecord.class);
	}
	
}
