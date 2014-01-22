package de.uniol.yourclubmusic.util;

import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import android.location.Location;
import android.os.Bundle;

public class LocationListener  implements android.location.LocationListener{
	private static int DISTANCE_IN_METER=10;
	private HandlerLocationChanged handler;
	private Location oldLocation=null;
	
	public LocationListener(HandlerLocationChanged handler) {
		this.handler=handler;
	}

	@Override
	public void onLocationChanged(Location location) {
        if(oldLocation==null){
        	oldLocation=location;
        	handler.sendNewLocation(location);	
        }else{
        	if(calculateDistance(oldLocation, location)>DISTANCE_IN_METER)
        		handler.sendNewLocation(location);	
        }
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		//GPS is disabled
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		//GPS is enabled
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	private static final int earthRadius = 6371;
    public static double calculateDistance(Location location1,Location location2)
    {
     	double lat1=location1.getLatitude();
    	double lon1=location1.getLongitude();
    	double lat2=location2.getLatitude();
    	double lon2=location2.getLongitude();
    	double dLat = (float) Math.toRadians(lat2 - lat1);
    	double dLon = (float) Math.toRadians(lon2 - lon1);
    	double a =
                (double) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
    	double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    	double d = earthRadius * c;
        return d/1000;
    }

}
