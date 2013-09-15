package com.chronos.reco;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent service = new Intent(this, LocalService.class);
        startService(service);
        
        //Load recommendation
        getAppRecommendation();
        
    }


    /**
     * 
     */
    private void getAppRecommendation() {
		// TODO Auto-generated method stub
    	
    	String url = Constants.baseUrl + "event/recommend";
    	
    	Date date = new Date();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.ENGLISH);
    	String strDate = df.format(date);
    	
    	RequestParams req = new RequestParams();
    	req.put("id", "android");
    	req.put("app", "");
    	req.put("email", "user@and.com");
    	req.put("time",strDate);
    	req.put("battery", "");
    	req.put("eventType", "");
    	req.put("duration", "");
    	if(get3gData()){
    		req.put("mobileData", "3g");
    	}
    	
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, req, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        Log.i("Chronos request sent :", "Post recommendation" + response);
		        
				try {
					
					JSONArray respArray = new JSONArray(response);
					TextView loadingText = (TextView) findViewById(R.id.loadingText);
					loadingText.setVisibility(View.INVISIBLE);
					displayAppList(respArray);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		    }
		    @Override 
		    public void onFailure(Throwable e, String resp){
		    	Log.e("Chronos Publish Event ", "Failed in post", e);
		    	TextView loadingText = (TextView) findViewById(R.id.loadingText);
		    	loadingText.setText("Oops !! Connection Lost. Please try again ");
		    }
		});
    	
		
	}

    /**
     * 
     * @return
     */
    private boolean get3gData(){
    	ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	State mobile = conMan.getNetworkInfo(0).getState();
    	if(mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 
     * @param v
     */
    public void refreshList(View v){
    	ListView appList = (ListView) findViewById(R.id.appList);
    	appList.setVisibility(View.INVISIBLE);
    	getAppRecommendation();
    	appList.clearTextFilter();
    	TextView loadingText = (TextView) findViewById(R.id.loadingText);
    	loadingText.setVisibility(View.VISIBLE);
    }

    /**
     * 
     * @param respArray
     * @throws JSONException
     */
	private void displayAppList(JSONArray respArray) throws JSONException {
		// TODO Auto-generated method stub
		ListView appList = (ListView) findViewById(R.id.appList);
		appList.setVisibility(View.VISIBLE);
		
		String[] stringarray = new String[respArray.length()];
        for (int i = 0; i < respArray.length(); i++) {
            stringarray[i] = respArray.getString(i);
        }
        //set Array Adapter
        appList.setAdapter(new MobileArrayAdapter(this,stringarray));
	}

	/**
	 * 
	 * @author a
	 * Array Adapter to display List
	 */
	public class MobileArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
	 
		public MobileArrayAdapter(Context context, String[] values) {
			super(context, R.layout.list_item, values);
			this.context = context;
			this.values = values;
		}
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = inflater.inflate(R.layout.list_item, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.appName);
			
			textView.setText(values[position]);
	 
			return rowView;
		}
	}


	/*
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
    
}
