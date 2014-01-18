package de.uniol.yourclubmusic.handler;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerLocationChanged extends Handler{
	
	public static final String LATITUDE="lat";
	public static final String LONGITUDE="lon";
	
	public void sendNewLocation(Location location){
		Message msg = new Message();
 	   Bundle bundle = new Bundle();
 	   bundle.putDouble(LATITUDE, location.getLatitude());
 	   bundle.putDouble(LONGITUDE, location.getLongitude());
 	   msg.setData(bundle);
 	   sendMessage(msg);
	}
}
