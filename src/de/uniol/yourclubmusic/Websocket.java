package de.uniol.yourclubmusic;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codebutler.android_websockets.WebSocketClient;

import android.util.Log;
import de.uniol.yourclubmusic.handler.HandlerClientOnlineOffline;
import de.uniol.yourclubmusic.handler.HandlerReceiveData;

public class Websocket {
	private static Websocket instance=null;
	static final String TAG = "Websocket";
	private String wsuri = "ws://192.168.178.30:1988";
	//private String wsuri = "ws://swi.us.to:1988";
	Boolean isConnected,firstRequest,canSend,requestStations;
	WebSocketClient client;
	double latitude,longitude;
	String connectedToStation;

	HandlerClientOnlineOffline handlerOnOff;
	HandlerReceiveData handlerReceiveData;
	private Websocket() {
		isConnected=false;
		firstRequest=true;
		canSend=false;
		requestStations=false;
	}
	public static Websocket getInstance(){
		if(instance==null) instance= new Websocket();
		return instance;
	}
	
	public void start() { 

			List<BasicNameValuePair> extraHeaders = Arrays.asList(
				    new BasicNameValuePair("Cookie", "session=abcd")
				);
		   client = new WebSocketClient(URI.create(wsuri), new WebSocketClient.Listener() {
			    @Override
			    public void onConnect() {
			    	//send location, so the server can send stations in the neighbourhood
			    	isConnected=true;
			    	if(requestStations){
				    	sendLocation(latitude, longitude);
			    	}
			    }

			    @Override
			    public void onMessage(String message) {
			        Log.d(TAG, String.format("Got string message! %s", message));
			        try {
						JSONObject mainObject = new JSONObject(message);
						if(mainObject.has("genres") &&mainObject.has("users")&&mainObject.has("totalVotings")){
							if(firstRequest){
								Log.d(TAG, "Status: Connected to " + wsuri);
					            handlerOnOff.sendClientOnline();
					            firstRequest=false;
							}
							JSONArray genres= mainObject.getJSONArray("genres");
							int users=mainObject.getInt("users");
							int totalVotings=mainObject.getInt("totalVotings");
							ArrayList<Genre> newGenres= new ArrayList<Genre>();
								for (int i = 0; i < genres.length(); i++) {
									JSONObject genre = genres.getJSONObject(i).getJSONObject("genre");
									double voting= Math.round((genre.getInt("votings")/(double)totalVotings)*100);
									newGenres.add(new Genre(genre.getString("name"),voting));
								}
								handlerReceiveData.sendStationData(newGenres, users);	
						}else if(mainObject.has("stations")){
							JSONArray stationsJson= mainObject.getJSONArray("stations");
							ArrayList<String> stations= new ArrayList<String>();
							for (int i = 0; i < stationsJson.length(); i++) {
								stations.add(stationsJson.getJSONObject(i).getString("name"));
							}
							handlerReceiveData.sendStations(stations);	
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }

			    @Override
			    public void onMessage(byte[] data) {
			        Log.d(TAG, "Got binary message!");
			    }

			    @Override
			    public void onDisconnect(int code, String reason) {
			        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
		        	 //reset
		        	 handlerOnOff.sendClientOffLine(reason);
		        	 isConnected=false;
		        	 firstRequest=true;
		        	 //instance=null;
			    }

			    @Override
			    public void onError(Exception error) {
			        Log.e(TAG, "Error!", error);
			    }

			}, extraHeaders);

		   client.connect();

	}
	
	public void registerOnOfflineHandler(HandlerClientOnlineOffline handler){
		handlerOnOff=handler;
	}
	public void registerHandlerReceiveData(HandlerReceiveData handler){
		this.handlerReceiveData=handler;
	}
	public void sendGenres(List<Genre> genres) {
		
		if(!isConnected || !canSend){
			Log.d(TAG, "Can't send data, client disconnected or disabled");
		}else{
			Log.d(TAG, "send data to client");
			JSONObject jsonObject= new JSONObject();
			JSONArray jsonGenres= new JSONArray();
			try {
				for (Genre genre : genres) {
					JSONObject object= new JSONObject();
					object.put("genre", genre.getName());
					jsonGenres.put(object);
				}
				jsonObject.put("genres", jsonGenres);
				jsonObject.put("station", connectedToStation);
				JSONArray jsonLocation= new JSONArray();
				jsonLocation.put(new JSONObject().put("latitude", latitude));
				jsonLocation.put(new JSONObject().put("longitude", longitude));
				jsonObject.put("location", jsonLocation);
				client.send(jsonObject.toString());
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void sendLocation(double latitude, double longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
		if(!isConnected){
			Log.d(TAG, "Can't send data, client disconnected");
		}else{
			Log.d(TAG, "send data to client");
			try{
				JSONObject jsonObject= new JSONObject();
				JSONArray jsonLocation= new JSONArray();
				jsonLocation.put(new JSONObject().put("latitude", latitude));
				jsonLocation.put(new JSONObject().put("longitude", longitude));
				jsonObject.put("location", jsonLocation);
				client.send(jsonObject.toString());
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void setStations(String station) {
		JSONObject jsonObject= new JSONObject();
		try {
			jsonObject.put("station", station);
			client.send(jsonObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.connectedToStation=station;
		
	}
	public String getStationName() {
		return connectedToStation;
	}
	public void setCanSend(boolean canSend) {
		this.canSend=canSend;
		
	}
	public void setRequestStations(boolean state){
		this.requestStations=state;
	}
	public boolean isStationRequest() {
		return requestStations;
	}
}