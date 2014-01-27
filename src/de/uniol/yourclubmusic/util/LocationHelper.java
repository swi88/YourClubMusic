package de.uniol.yourclubmusic.util;

import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class LocationHelper {
	Context context;
	private String currentProvider="";
	
	public LocationHelper(Context context,HandlerLocationChanged handlerLocationChanged) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	LocationListener listener= new LocationListener(handlerLocationChanged);
    	//get updates after 10 meters
    	if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, listener);
        	currentProvider=LocationManager.NETWORK_PROVIDER;
    	}
    	else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, listener);
    		currentProvider=LocationManager.GPS_PROVIDER;
    	}else  Toast.makeText(context, "Sie konnten nicht lokalisiert werden", 50).show();
    	if(!currentProvider.isEmpty()){
    		//get last known position
    		Location last=locationManager.getLastKnownLocation(currentProvider);
    		if(last!=null)
    			handlerLocationChanged.sendNewLocation(last);
    	}
	}
}