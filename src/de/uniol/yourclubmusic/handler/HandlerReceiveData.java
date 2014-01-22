package de.uniol.yourclubmusic.handler;

import java.util.ArrayList;

import de.uniol.yourclubmusic.Genre;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerReceiveData extends Handler{
			
			public static final String GENRES="genres";
			public static final String USERS="users";
			public static final String STATIONS="stations";
			
			public void sendStationData(ArrayList<Genre> genres,int users){
				Message msg = new Message();
		 	   Bundle bundle = new Bundle();
		 	   bundle.putSerializable(GENRES, genres);
		 	   bundle.putInt(USERS, users);
		 	   msg.setData(bundle);
		 	   sendMessage(msg);
			}

			public void sendStations(ArrayList<String> stations) {
				Message msg = new Message();
			 	   Bundle bundle = new Bundle();
			 	   bundle.putSerializable(STATIONS, stations);
			 	   msg.setData(bundle);
			 	   sendMessage(msg);
			}
}
