package com.chronos.reco;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.chronos.reco.EventObject.EventType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {

	//private TimerTask findCurrentProcess;
	Timer myTimer;
	
	static String currProcessName = "";
	
	private boolean timerRunning = false;
	private long RETRY_TIME = 2000;
	private long START_TIME = 1000;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		myTimer = new Timer();
	    myTimer.scheduleAtFixedRate(new Task(), START_TIME, RETRY_TIME);
	    timerRunning = true;
		
	    //Intent  myIntent = new Intent(LocalService.this, )
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		//}
		
		if (!timerRunning) {
	        myTimer = new Timer();
	        myTimer.scheduleAtFixedRate(new Task(), START_TIME, RETRY_TIME);
	        timerRunning = true;
	    }
			
		return super.onStartCommand(intent, flags, startId);
		//return 1;
	}
	
	private class Task extends TimerTask {

		
		
	    @Override
	    public void run() {

	        // DO WHAT YOU NEED TO DO HERE
	    	
	    	String processName = getRunningProcess();
	    	Log.i("manas", "processname: " + processName);
	    	Log.i("manas", "last process: " + currProcessName);
			if(!processName.equals(currProcessName)){
				//publishEndEvent;
				try{
				publishEvent(currProcessName, EventType.END);
				currProcessName = processName;
				publishEvent(currProcessName, EventType.START);
				}catch(Exception e){
					
				}
			}
	    	
	    }


	}
		
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (myTimer != null) {
	        myTimer.cancel();
        }

	    timerRunning = false;
		Log.i("service Destroyed ", "Chronos");
		
	}

	private void publishEvent(String currProcessName, EventType evtType) throws JSONException, UnsupportedEncodingException {
		//EventObject evtObj = getEventObject();
		//push to server
		//http://ec2-54-227-236-224.compute-1.amazonaws.com:3000/event/save
		JSONObject params = new JSONObject();
		params.put("app", currProcessName);
		StringEntity entity = new StringEntity(params.toString());
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		AsyncHttpClient client = new AsyncHttpClient();
		Log.i("manas", "postContent" + entity.toString());
		client.post(this,"http://ec2-54-227-236-224.compute-1.amazonaws.com:3000/event/save",
				entity, "application/json", new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        Log.i("manas", "post done" + response);
		    }
		});
		//params.put("more", "data");
		/*
		 id   (userId decide by our app, send some number for now)
        name
        email        
        time (epoch time: in seconds)
        battery (percentage)
        roaming (yes or no)
        roaming location  (if possible)
        screenLevel (the brightness level)
        volume (volume level)
        vibrate (vibrate level)
        wifi: (yes or no)
        mobileData (no, 2g or 3g)
        longitude
        latitude
        app (name of the app started)
        runningAppList  (list of apps running)
		 */
		Log.i("Chronos PublishEvent:", currProcessName + " " + evtType.getDesc());
	}

	private String getRunningProcess(){
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		//Foreground app
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		String processName = componentInfo.getPackageName();
		return processName;
		/*
		String appActive = "";
		ActivityManager activityManager = (ActivityManager) getSystemService( Context.ACTIVITY_SERVICE );
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for(RunningAppProcessInfo appProcess : appProcesses){
		    if(appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
		        Log.i("Foreground App", appProcess.processName);
		        appActive = appProcess.processName;
		    }
		}
		
		//Log.i("Running Package ", processName);
		return appActive;
		*/
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
