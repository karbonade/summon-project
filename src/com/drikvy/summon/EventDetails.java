package com.drikvy.summon;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class EventDetails extends Dialog {

	Context context;
	String eventDescription;
	
	EditText edEvent;
	Button btnEvent;
	
	LatLng geoPos;
	MainActivity mainActivity;
	
	public EventDetails(MainActivity main, String description, LatLng pos) {
		super(main);
		this.geoPos = pos;
		this.mainActivity = main;
		this.eventDescription = description;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
	
        edEvent = (EditText) findViewById(R.id.edDescription);
        edEvent.setText(eventDescription);
        
        btnEvent = (Button) findViewById(R.id.btnPublish);
        btnEvent.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				publishEvent();
			}
		});
        
	}

	protected void publishEvent() {
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("lat",geoPos.latitude);
			obj.put("lng",geoPos.longitude);
			obj.put("title","Event");
			obj.put("desc",edEvent.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  		  
		mainActivity.listener.publish(obj.toString());
		this.dismiss();
	}

}
