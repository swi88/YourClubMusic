package de.uniol.yourclubmusic.util;

import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class LocationListener  implements android.location.LocationListener{
	private HandlerLocationChanged handler;
	
	public LocationListener(HandlerLocationChanged handler) {
		this.handler=handler;
	}

	@Override
	public void onLocationChanged(Location location) {
        Log.d("Location", "changed");
		handler.sendNewLocation(location);
		
		
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

}
