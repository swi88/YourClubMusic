package de.uniol.yourclubmusic;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * An example of how to use the NFC foreground dispatch APIs. This will intercept any MIME data
 * based NDEF dispatch as well as all dispatched for NfcF tags.
 */
public class NFCDispatchActivity extends Activity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        
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

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
        
        String payloadString = tryToExtractNFCPayload(getIntent());
        
        if(payloadString != null) {
        	Log.i("NFCDispatch/OnCreate", "Code of the day is: " + payloadString);
        	sendMessage(payloadString, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        
        String payloadString = tryToExtractNFCPayload(intent);
        
        if(payloadString != null) {
        	Log.i("Foreground dispatch", "Code of the day is: " + payloadString);
        	sendMessage(payloadString, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }
    
	private String tryToExtractNFCPayload(Intent intent) {
		String payloadString = null;
        
        // Get Ndef messages from nfc tag
        NdefMessage[] msgs = null;
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
		return payloadString;
	}
	
	public void sendMessage(String code, boolean startInNewTask) {
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.putExtra(MainActivity.EXTRA_MESSAGE, code);
	    if(startInNewTask) {
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    } else {
	    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    }
	    startActivity(intent);
	}
}