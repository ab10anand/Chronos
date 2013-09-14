package com.chronos.reco;


import java.util.Date;
import java.util.List;

import com.chronos.reco.EventObject.EventType;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String currProcessName = "";
		while(true){
			String processName = getRunningProcess();
			if(processName != currProcessName){
				//publishEndEvent;
				publishEvent(currProcessName, EventType.END);
				currProcessName = processName;
				publishEvent(currProcessName, EventType.START);
			}
		}
		//return super.onStartCommand(intent, flags, startId);
	}

	private void publishEvent(String currProcessName, EventType evtType) {
		EventObject evtObj = getEventObject();
		//push to server
		Log.i("Chronos PublishEvent:", currProcessName + " " + evtType.getDesc());
	}

	private String getRunningProcess(){
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
		//Foreground app
		ActivityManager.RunningAppProcessInfo appInfo = runningAppProcessInfo.get(1);
		String processName = appInfo.processName;
		return processName;
	}
	
	private EventObject getEventObject(){
		EventObject evtObj = new EventObject();
		evtObj.setAppName("testApp");
		evtObj.setAudioSettings("High");
		evtObj.setBrightnessLevel(20);
		evtObj.setConnectivity("Wifi: SSID");
		evtObj.setDate(new Date().toString());
		evtObj.setEventType(EventType.START);
		evtObj.setLocation("56' 79' Indian Express");
		evtObj.setRoaming(true);
		evtObj.setTime("Current time");
		evtObj.setUserIdentity("{Name: x, email:y}");
		return evtObj;
	}
	
	

}
