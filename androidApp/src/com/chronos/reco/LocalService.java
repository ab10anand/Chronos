package com.chronos.reco;


import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
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
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {

	
	Timer myTimer;
	
	String currProcessName = "";
	long startTime = System.currentTimeMillis();
	
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
		
	    
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (!timerRunning) {
	        myTimer = new Timer();
	        myTimer.scheduleAtFixedRate(new Task(), START_TIME, RETRY_TIME);
	        timerRunning = true;
	    }
			
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	/**
	 * 
	 * @author a
	 *
	 */
	private class Task extends TimerTask {

		@Override
	    public void run() {

	        // Get the current Active Process Package Name
	    	
	    	String processName = getRunningProcess();
	    	Log.i("manas", "processname: " + processName);
	    	Log.i("manas", "last process: " + currProcessName);
	    	
	    	//Check for change in last process and current process 
			if(!processName.equals(currProcessName)){
				//publishEndEvent;
				try{
				
				long endTime = System.currentTimeMillis();
				publishEvent(currProcessName, EventType.END, endTime-startTime);
				currProcessName = processName;
				
				startTime = endTime;
				publishEvent(currProcessName, EventType.START, 0);
				}catch(Exception e){
					
				}
			}
	    	
	    }
		
		/**
		 * 
		 * @param currProcessName
		 * @param evtType
		 * @param duration
		 * @throws JSONException
		 * @throws UnsupportedEncodingException
		 */
	    private void publishEvent(String currProcessName, EventType evtType, long duration) throws JSONException, UnsupportedEncodingException {
			
			//push to server
			//http://ec2-54-227-236-224.compute-1.amazonaws.com:3000/event/save
			Log.i("Chronos", "publishing event");
			String url = Constants.baseUrl + "event/save";
			RequestParams req = getRequestParams(currProcessName, evtType.getDesc(), duration);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, req, new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
			        Log.i("Chronos Publish Event", "Post done" + response);
			    }
			    @Override 
			    public void onFailure(Throwable e, String resp){
			    	Log.e("Chronos Publish Event ", "Failed in post", e);
			    }
			});
	    	
	    }
	    
	    /**
	     * 
	     * @param processName
	     * @param evtType
	     * @param duration
	     * @return
	     */
	    private RequestParams getRequestParams(String processName, String evtType, long duration){
	    	
	    	Date date = new Date();
	    	Log.i("Chronos" , "process" + processName + ", " + evtType);
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.ENGLISH);
	    	//df.setTimeZone(TimeZone.getTimeZone("UTC"));
	    	String strDate = df.format(date);
	    	Log.i("date", "dateChrono"+strDate);
	    	RequestParams req = new RequestParams();
	    	req.put("id", "android");
	    	req.put("app", processName);
	    	req.put("email", "user@and.com");
	    	req.put("time",strDate);
	    	req.put("battery", String.valueOf(getBatteryLevel()));
	    	req.put("eventType", evtType);
	    	req.put("duration", String.valueOf(duration));
	    	return req;
	    }

	}
	
	/**
	 * 
	 * @return
	 */
	public float getBatteryLevel() {
	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
    }

	    return ((float)level / (float)scale) * 100.0f; 
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

	

	/**
	 * 
	 * @return
	 */
	private String getRunningProcess(){
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		//list of Active activity
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		//Foreground app, top in stack
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		String processName = componentInfo.getPackageName();
		return processName;
		
	}
	
	/**
	 * 
	 * @return
	 */
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
