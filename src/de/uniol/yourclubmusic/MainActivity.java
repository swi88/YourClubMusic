package de.uniol.yourclubmusic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import de.uniol.yourclubmusic.handler.HandlerClientOnlineOffline;
import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import de.uniol.yourclubmusic.handler.HandlerReceiveData;
import de.uniol.yourclubmusic.util.LocationHelper;
import de.uniol.yourclubmusic.util.LocationListener;

public class MainActivity extends Activity {

	public static final String EXTRA_MESSAGE = "CODEOFTHEDAY";
	
	private List<Genre> genres= new ArrayList<Genre>();
	private Websocket socket;
	
	private HandlerClientOnlineOffline handlerOnOff;
	private HandlerLocationChanged handlerLocationChanged;
	private HandlerReceiveData handlerReceiveData;
	private ArrayAdapter<Genre> genreAdapter;
    private int selectedStation=0;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket= Websocket.getInstance();
        registerHandlers();
        // LocationHelper location=new LocationHelper(this,handlerLocationChanged);

		((Switch)findViewById(R.id.switchVote)).setEnabled(false);
        
        genreAdapter= new GenreListAdapter(this,R.layout.view_genre,genres);
        
       
        ListView listView=(ListView)findViewById(R.id.listViewCurrentMood);
        listView.setAdapter(genreAdapter);
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		String code = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		// FIXME this must work, so that the commented rfid part below can work
		/*
		code = "Amadeus";
		if(!socket.isStarted()){
			socket.start();
		}
		connectToClub(code);
		*/
		 /*
		if(code != null) {
			Log.i("Main/onResume", "Current code is: " + code);
			
			//TODO start from NFC
			socket.start();
			connectToClub(code);
			// Update view
			// in the future maybe grep the first part, if we use an actual code,
			// that changes, e.g "Amadeus:hF6Dbcg"
			this.setTitle(code + " - " + getString(R.string.app_name));
		} else {
			Log.i("Main/onResume", "Current code is not set");
		}
		*/
	}
	private void connect() {
		socket.setRequestStations(true);
		if(!socket.isStarted()){
    		socket.start();
    	}else socket.sendStationRequest();
	}
	private void connectToClub(String code) {
		// TODO open the websocket here
		// Be careful, when the Tag is scanned twice, this activity gets restarted
		// But maybe we should ignore this case for now
		this.setTitle(code + " - " + getString(R.string.app_name));
	    socket.setStations(code);
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
        	connect();
        	return true;
        }
        default:
            return super.onOptionsItemSelected(item);
    	}
		
	}

	public void registerHandlers(){
    	Switch buttonSwitch=(Switch) findViewById(R.id.switchVote);
    	if(buttonSwitch!=null){
    		buttonSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener(this));
    	}
    	handlerOnOff = new MyHandlerClientOnlineOffline(this);
        handlerReceiveData = new MyHandlerReceiveData(this);

    	socket.registerOnOfflineHandler(handlerOnOff);
    	socket.registerHandlerReceiveData(handlerReceiveData);
    	
        handlerLocationChanged= new MyHandlerLocationChanged(this);
    }


	protected List<Genre> getGenres() {
		ArrayList<Genre> preferedGenres= new ArrayList<Genre>();
		final SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		final Set<String> preferredGenres = sharedPref.getStringSet(
				getString(R.string.saved_preferred_genres),
				new HashSet<String>());
		// Sort set in list
		ArrayList<String> preferredGenresList = new ArrayList<String>(preferredGenres);
		Collections.sort(preferredGenresList);
		
		for (String genreID : preferredGenresList) {
			preferedGenres.add(new Genre(genreID));
		}
		return preferedGenres;
	}
	
	static class MyOnCheckedChangeListener implements OnCheckedChangeListener {
		private final WeakReference<MainActivity> mainActivity; 

		private MyOnCheckedChangeListener(MainActivity mainActivity) {
			this.mainActivity = new WeakReference<MainActivity>(mainActivity);
		}

		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			MainActivity context = mainActivity.get();
			if(context != null) {
				if(isChecked){
					context.socket.setCanSend(true);
					context.socket.sendGenres(context.getGenres());
					Toast.makeText(context, context.getString(R.string.voting_activated), Toast.LENGTH_SHORT).show();
				} else {
					context.socket.sendGenres(new ArrayList<Genre>());
					context.socket.setCanSend(false);
					Toast.makeText(context, context.getString(R.string.voting_deactivated), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	static class MyHandlerClientOnlineOffline extends HandlerClientOnlineOffline {
		private final WeakReference<MainActivity> mainActivity; 

		private MyHandlerClientOnlineOffline(MainActivity mainActivity) {
			this.mainActivity = new WeakReference<MainActivity>(mainActivity);
		}
		
    	@Override
    	public void handleMessage(Message msg) {
    		MainActivity context = mainActivity.get();
			if(context != null) {
				switch (msg.what) {
				case CLIENT_OFFLINE: 
					((CheckBox)context.findViewById(R.id.checkBoxConnected)).setChecked(false);
					((TextView)context.findViewById(R.id.activeUsers)).setText("0");
					((Switch)context.findViewById(R.id.switchVote)).setEnabled(false);
					((Switch)context.findViewById(R.id.switchVote)).setChecked(false);
					context.genres.clear();
					context.genreAdapter.clear();
					String reason=msg.getData().getString(REASON);
					AlertDialog.Builder builder = 
	    		            new AlertDialog.Builder(context);
		            builder.setTitle("Warnung");
		            builder.setMessage("Verbindung zu "+context.socket.getStationName()+" verloren, da "+ reason);
		            builder.setPositiveButton("OK", null);
		            builder.show();
					break;
				case CLIENT_ONLINE:
					((CheckBox)context.findViewById(R.id.checkBoxConnected)).setChecked(true);
					((Switch)context.findViewById(R.id.switchVote)).setChecked(false);
					((Switch)context.findViewById(R.id.switchVote)).setEnabled(true);
					Toast.makeText(context, "Verbunden mit "+context.socket.getStationName(), 50).show();
					break;
				default:
					break;
				}
	    		super.handleMessage(msg);
			}
    	}
    }
	
	static class MyHandlerReceiveData extends HandlerReceiveData {
		private final WeakReference<MainActivity> mainActivity; 

		private MyHandlerReceiveData(MainActivity mainActivity) {
			this.mainActivity = new WeakReference<MainActivity>(mainActivity);
		}
		
    	public void handleMessage(Message msg) {
    		final MainActivity context = mainActivity.get();
			if(context != null) {
				Bundle bundle= msg.getData();
	    		if(bundle.containsKey(GENRES)){
	    			ArrayList<Genre> genresNew= (ArrayList<Genre>) bundle.getSerializable(GENRES);
	    			context.genres.clear();
	    			context.genreAdapter.clear();
	    			context.genres.addAll(genresNew);
	    			Collections.sort(context.genres);
	        		((TextView)context.findViewById(R.id.activeUsers)).setText(""+bundle.getInt(USERS));
	    		}else if(bundle.containsKey(STATIONS) &&context.socket.isStationRequest()){
	    			context.socket.setRequestStations(false);
	    			final ArrayList<String> stations= (ArrayList<String>) bundle.getSerializable(STATIONS);
	    			AlertDialog.Builder builder = 
	    		            new AlertDialog.Builder(context);
	    		        builder.setTitle(context.getString(R.string.voting_choose_station));
	    		        builder.setSingleChoiceItems(
	    		                stations.toArray(new CharSequence[stations.size()]), 
	    		                -1,new DialogInterface.OnClickListener() {
	    		    				
	    		    				@Override
	    		    				public void onClick(DialogInterface dialog, int which) {
	    		    					context.selectedStation=which;
	    		    				}
	    		    			});
	    		      builder.setCancelable(true)
	    		      .setNegativeButton("abbrechen", 
	    		    	        new DialogInterface.OnClickListener() 
	    		    	        {
	    		    	            @Override
	    		    	            public void onClick(DialogInterface dialog, 
	    		    	                    int which) {
	    		    	               
	    		    	            }
	    		    	        });
	    		     builder.setPositiveButton("verbinden",  new DialogInterface.OnClickListener() 
		    	        {
		    	            @Override
		    	            public void onClick(DialogInterface dialog, 
		    	                    int which) {
		    	            	((Switch)context.findViewById(R.id.switchVote)).setChecked(false);
		    	            	context.connectToClub(stations.get(context.selectedStation));
		    	            }
		    	        });
	    		     builder.create().show();
	    			//socket.setStations("Amadeus");
	    		}
			}
    	}
    }
	
	static class MyHandlerLocationChanged extends HandlerLocationChanged {
		private final WeakReference<MainActivity> mainActivity; 

		private MyHandlerLocationChanged(MainActivity mainActivity) {
			this.mainActivity = new WeakReference<MainActivity>(mainActivity);
		}
		
    	@Override
    	public void handleMessage(Message msg) {
    		final MainActivity context = mainActivity.get();
			if(context != null) {
				Bundle bundle= msg.getData();
				context.socket.sendLocation(bundle.getDouble(LATITUDE),bundle.getDouble(LONGITUDE));
	    		super.handleMessage(msg);
			}
    	}
    }
}
