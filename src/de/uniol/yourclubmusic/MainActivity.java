package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
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
import de.uniol.yourclubmusic.handler.HandlerClientOnlineOffline;
import de.uniol.yourclubmusic.handler.HandlerLocationChanged;
import de.uniol.yourclubmusic.handler.HandlerReceiveData;
import de.uniol.yourclubmusic.util.LocationListener;

public class MainActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket= Websocket.getInstance();
        setContentView(R.layout.activity_main);
        registerHandlers();
        registerLocationListener();
        
        genreAdapter= new GenreListAdapter(this,R.layout.view_genre,genres);
        
       
        ListView listView=(ListView)findViewById(R.id.listViewCurrentMood);
        listView.setAdapter(genreAdapter);

        
        /*
         * Note:
         * 
         * Is used the "NDEF Writer" app to write the following on a nfc tag:
         * 
         * value    = Amadeus		(Example, this is the 'code of the day', which is required to vote)
         * Mimetype = application/de.uniol.yourclubmusic
         */
        
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for "application/de.uniol.yourclubmusic" MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            // ndef.addDataType("*/*");
        	ndef.addDataType("application/de.uniol.yourclubmusic");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
	protected void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        
        NdefMessage[] msgs = null;
        String payloadString = null;
        
        // Get Ndef messages from nfc tag
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
        }

        //process the messages array (extract our desired payload string)
        if(msgs != null && msgs.length>0) {
        	// System.out.println("xxx_" + Arrays.toString(msgs));
        	NdefRecord[] records = msgs[0].getRecords();
        	if(records != null && records.length>0) {
        		// System.out.println("xxy_" + records[0]);
        		byte[] payload = records[0].getPayload();
        		if(payload.length > 0) { // payload always exists, but may be of len 0
        			payloadString = new String(payload);
        		}
        	}
        }
        
        if(payloadString != null) {
        	Log.i("Foreground dispatch", "Code of the day is: " + payloadString);
        }
	}

	@Override
	protected void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
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
    	Switch buttonSwitch=(Switch) findViewById(R.id.switchVote);
    	if(buttonSwitch!=null){
    		buttonSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    			
    			@Override
    			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
    				ArrayList<Genre> g= new ArrayList<Genre>();
    				if(isChecked) socket.sendGenres(getGenres());
    				else socket.sendGenres(new ArrayList<Genre>());
    			}
    		});
    	}
    	handlerOnOff= new HandlerClientOnlineOffline(){
        	@Override
        	public void handleMessage(Message msg) {
        		
        		switch (msg.what) {
				case CLIENT_OFFLINE: 
					((CheckBox)findViewById(R.id.checkBoxConnected)).setChecked(false);
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
        handlerReceiveData= new HandlerReceiveData(){
        	public void handleMessage(Message msg) {
        		Bundle bundle= msg.getData();
        		ArrayList<Genre> genresNew= (ArrayList<Genre>) bundle.getSerializable(GENRES);
        		genres.clear();
        		genreAdapter.clear();
        		
        		for (Genre genre : genresNew) {
					genres.add(genre);
				}
        		((TextView)findViewById(R.id.activeUsers)).setText(""+bundle.getInt(USERS));
        		
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
