package com.chronos.reco;

public class EventObject {
	private String location;
	private String date;
	private String time;
	private boolean roaming;
	private int brightnessLevel;
	private String audioSettings;
	private String userIdentity;
	private String appName;
	private EventType eventType;
	private String connectivity;
	
	public enum EventType{
		
		START("start"),
		END("end"),
		IDLE("idle");
		
		private String desc;
		EventType(String desc){
			this.desc = desc;
		}
		public String getDesc(){
			return this.desc;
		}
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isRoaming() {
		return roaming;
	}

	public void setRoaming(boolean roaming) {
		this.roaming = roaming;
	}

	public int getBrightnessLevel() {
		return brightnessLevel;
	}

	public void setBrightnessLevel(int brightnessLevel) {
		this.brightnessLevel = brightnessLevel;
	}

	public String getAudioSettings() {
		return audioSettings;
	}

	public void setAudioSettings(String audioSettings) {
		this.audioSettings = audioSettings;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(String connectivity) {
		this.connectivity = connectivity;
	}
}
