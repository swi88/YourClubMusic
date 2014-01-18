package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.List;

import de.uniol.yourclubmusic.handler.HandlerClientOnlineOffline;
import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import de.uniol.yourclubmusic.util.LocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<Genre> genres= new ArrayList<Genre>();
	private Websocket socket;
	
	private HandlerClientOnlineOffline handlerOnOff;
	private HandlerLocationChanged handlerLocationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerHandlers();
        registerLocationListener();
        setContentView(R.layout.activity_main);
        //TODO in final version only create entry, if genre exists
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        
        genres.add(new Genre("Alternative", 25,R.drawable.alternative));
        ArrayAdapter<Genre> adapter= new GenreListAdapter(this,R.layout.view_genre,genres);
        ListView listView=(ListView)findViewById(R.id.listViewCurrentMood);
        listView.setAdapter(adapter);
        genres.get(1).setRatingInPercent(22);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.action_settings:
            Intent intentSettings= new Intent(this,SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        case R.id.connect:{
        	socket= Websocket.getInstance();
        	socket.registerOnOfflineHandler(handlerOnOff);
        	socket.start();
        	return true;
        }
        default:
            return super.onOptionsItemSelected(item);
    	}
    }
    public void registerLocationListener(){
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	LocationListener listener= new LocationListener(handlerLocationChanged);
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }
    public void registerHandlers(){
    	handlerOnOff= new HandlerClientOnlineOffline(){
        	@Override
        	public void handleMessage(Message msg) {
        		
        		switch (msg.what) {
				case CLIENT_OFFLINE: 
					//((CheckBox)findViewById(R.id.checkBoxConnected)).setChecked(false);
					break;
				case CLIENT_ONLINE:
					((CheckBox)findViewById(R.id.checkBoxConnected)).setChecked(true);
					break;
				default:
					break;
				}
        		super.handleMessage(msg);
        	}
        };
        handlerLocationChanged= new HandlerLocationChanged(){
        	@Override
        	public void handleMessage(Message msg) {
        		Bundle bundle= msg.getData();
        		String string= "Current Location:"+bundle.getDouble(LATITUDE)+"/"+bundle.getDouble(LONGITUDE);
        		//Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        		//TODO check distance and send location to server
        		//auf server secrurity part implementieren (distanz berechnen und entscheiden ob client berechtigt ist)
        		super.handleMessage(msg);
        	}
        };
    }
}
