package com.workout;

import com.google.gson.Gson;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
	private String exerciseName;
	private String numberOfReps;
	private String numberOfSets;
	private String info;
    private String timed; // "y" for timed, "n" otherwise
	
	public Exercise(){
		this.info = ""; //info is optional, don't want it to be null and fail if it is not set
        timed = "n"; //exercises will not be timed by default
	}
	
	public void setName(String name){
		this.exerciseName = name;
	}
	public void setReps(String num){
		this.numberOfReps = num;
	}
	public void setSets(String num){
		this.numberOfSets = num;
	}
	public void setInfo(String info){
		this.info = info;
	}
    public void setTimed(String timed) { this.timed = timed; }
	
	public String getName(){
		return this.exerciseName;
	}
	public String getSets(){
		return this.numberOfSets;
	}
	public String getReps(){
		return this.numberOfReps;
	}
	public String getInfo(){
		return this.info;
	}
    public String getTimed() { return this.timed; }

	// Parcelling part
    public Exercise(Parcel in){
    	this.setName(in.readString());
    	this.setReps(in.readString());
    	this.setSets(in.readString());
    	this.setInfo(in.readString());
        this.setTimed(in.readString());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in); 
        }

		@Override
		public Object[] newArray(int arg0) {
			return null;
		}
    };

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(getName());
		parcel.writeString(getReps());
		parcel.writeString(getSets());
		parcel.writeString(getInfo());
        parcel.writeString(getTimed());
	}
	
	public String convertToJson() {
		Gson gson = new Gson();
		return gson.toJson(this);		
	}
	
	
	
}
