package de.uniol.yourclubmusic;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Handler;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.uniol.yourclubmusic.handler.HandlerClientOnlineOffline;

public class Websocket {
	private static Websocket instance=null;
	private final WebSocketConnection mConnection = new WebSocketConnection();
	static final String TAG = "Websocket";
	private String wsuri = "ws://134.106.27.48:8080";
	Boolean isConnected;
	ArrayList<Handler> handlerData;
	HandlerClientOnlineOffline handlerOnOff;
	private Websocket() {
		isConnected=false;
		handlerData= new ArrayList<Handler>();
		start();
	}
	public static Websocket getInstance(){
		if(instance==null) instance= new Websocket();
		return instance;
	}
	
	public void start() { 
	   try {
	      mConnection.connect(wsuri, new WebSocketHandler() {
	 
	         @Override
	         public void onOpen() {
	            Log.d(TAG, "Status: Connected to " + wsuri);
	            handlerOnOff.sendClientOnline();
	            isConnected=true;
	            
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
	        	 //reset
	        	 handlerOnOff.sendClientOffLine();
	        	 instance=null;
	        	 
	         }
	      });
	   } catch (WebSocketException e) {
	      Log.d(TAG, e.toString());
	   }
	}
	public void sendJSONArray(JSONArray data){
		if(!isConnected){
			Log.d(TAG, "Can't send data, client disconnected");
		}else mConnection.sendTextMessage(data.toString());
	}
	
	public void registerOnOfflineHandler(HandlerClientOnlineOffline handler){
		handlerOnOff=handler;
	}
}