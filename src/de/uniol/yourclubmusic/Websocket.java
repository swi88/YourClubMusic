package de.uniol.yourclubmusic;

import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class Websocket {
	private final WebSocketConnection mConnection = new WebSocketConnection();
	static final String TAG = "Websocket";
	final String wsuri = "ws://localhost:9000";
	private void start() {
	
	   
	 
	   try {
	      mConnection.connect(wsuri, new WebSocketHandler() {
	 
	         @Override
	         public void onOpen() {
	            Log.d(TAG, "Status: Connected to " + wsuri);
	            mConnection.sendTextMessage("Hello, world!");
	         }
	 
	         @Override
	         public void onTextMessage(String payload) {
	            
	         }
	         @Override
	        public void onBinaryMessage(byte[] payload) {
	        	 //register handler
	        	 Log.d(TAG, "binary data");
	        }
	 
	         @Override
	         public void onClose(int code, String reason) {
	            Log.d(TAG, "Connection lost.");
	         }
	      });
	   } catch (WebSocketException e) {
	 
	      Log.d(TAG, e.toString());
	   }
	}
}