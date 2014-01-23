package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.IntentFilter;

import android.location.LocationManager;
import android.nfc.NfcAdapter;
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
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView mText;
    private int mCount = 0;
    private int selectedStation=0;
    private Context mContext; 
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	mContext=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket= Websocket.getInstance();
        setContentView(R.layout.activity_main);
        registerHandlers();
        LocationHelper location=new LocationHelper(mContext,handlerLocationChanged);

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
    		buttonSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    			
    			@Override
    			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
    				if(isChecked){
    					socket.setCanSend(true);
    					socket.sendGenres(getGenres());
    					Toast.makeText(mContext, "An Abstimmung teilgenommen", 50).show();
    					
    				}
    				else{
    					socket.sendGenres(new ArrayList<Genre>());
    					socket.setCanSend(false);
    					Toast.makeText(mContext, "Stimmen zurückgenommen", 50).show();
    					
    				}
    			}
    		});
    	}
    	handlerOnOff= new HandlerClientOnlineOffline(){
        	@Override
        	public void handleMessage(Message msg) {
        		
        		switch (msg.what) {
				case CLIENT_OFFLINE: 
					((CheckBox)findViewById(R.id.checkBoxConnected)).setChecked(false);
					((TextView)findViewById(R.id.activeUsers)).setText("0");
					((Switch)findViewById(R.id.switchVote)).setEnabled(false);
					((Switch)findViewById(R.id.switchVote)).setChecked(false);
					genres.clear();
					genreAdapter.clear();
					String reason=msg.getData().getString(REASON);
					AlertDialog.Builder builder = 
        		            new AlertDialog.Builder(mContext);
		            builder.setTitle("Warnung");
		            builder.setMessage("Verbindung zu "+socket.getStationName()+" verloren, da "+ reason);
		            builder.setPositiveButton("OK", null);
		            builder.show();
					break;
				case CLIENT_ONLINE:
					((CheckBox)findViewById(R.id.checkBoxConnected)).setChecked(true);
					((Switch)findViewById(R.id.switchVote)).setChecked(false);
					((Switch)findViewById(R.id.switchVote)).setEnabled(true);
					Toast.makeText(mContext, "Verbunden mit "+socket.getStationName(), 50).show();
					break;
				default:
					break;
				}
        		super.handleMessage(msg);
        	}
        };
        handlerReceiveData= new HandlerReceiveData(){
        	public void handleMessage(Message msg) {
        		Bundle bundle= msg.getData();
        		if(bundle.containsKey(GENRES)){
        			ArrayList<Genre> genresNew= (ArrayList<Genre>) bundle.getSerializable(GENRES);
            		genres.clear();
            		genreAdapter.clear();
            		genres.addAll(genresNew);
        			Collections.sort(genres);
            		((TextView)findViewById(R.id.activeUsers)).setText(""+bundle.getInt(USERS));
        		}else if(bundle.containsKey(STATIONS) &&socket.isStationRequest()){
        			socket.setRequestStations(false);
        			final ArrayList<String> stations= (ArrayList<String>) bundle.getSerializable(STATIONS);
        			AlertDialog.Builder builder = 
        		            new AlertDialog.Builder(mContext);
        		        builder.setTitle("Wähle eine Station:");
        		        builder.setSingleChoiceItems(
        		                stations.toArray(new CharSequence[stations.size()]), 
        		                -1,new DialogInterface.OnClickListener() {
        		    				
        		    				@Override
        		    				public void onClick(DialogInterface dialog, int which) {
        		    					selectedStation=which;
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
		    	            	((Switch)findViewById(R.id.switchVote)).setChecked(false);
		    	            	connectToClub(stations.get(selectedStation));
		    	            }
		    	        });
        		     builder.create().show();
        			//socket.setStations("Amadeus");
        		}
        	};
        };

    	socket.registerOnOfflineHandler(handlerOnOff);
    	socket.registerHandlerReceiveData(handlerReceiveData);
    	
        handlerLocationChanged= new HandlerLocationChanged(){
        	@Override
        	public void handleMessage(Message msg) {
        		Bundle bundle= msg.getData();
        		socket.sendLocation(bundle.getDouble(LATITUDE),bundle.getDouble(LONGITUDE));
        		super.handleMessage(msg);
        	}
        };
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
}
