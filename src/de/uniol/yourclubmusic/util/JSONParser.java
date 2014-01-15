package de.uniol.yourclubmusic.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JSONParser extends AsyncTask<JSONObject, Void, Void>  {
	//TAGS GENRE
	private static final String ID_GENRE="genre";
	private static final String NAME_GENRE="name";
	private static final String RATE_GENRE="rate";
	//TAGS logged in numbers
	private static final String ID_USERS_LOGGED_IN="users";
	private static final String ONLINE="number";

	@Override
	protected Void doInBackground(JSONObject... object) {
		//get JSON Array node
		try {
			JSONArray genres=object[0].getJSONArray(ID_GENRE);
			String genre;
			double rate;
			for (int i = 0; i < genres.length(); i++) {
				  JSONObject c = genres.getJSONObject(i);
				  genre=c.getString(NAME_GENRE);
				  rate=c.getDouble(RATE_GENRE);
				  Log.d("incoming genre", genre+" "+rate);
			}
			JSONArray users_online=object[0].getJSONArray(ID_USERS_LOGGED_IN);
			int number=0;
			if(users_online.length()>0) number=((JSONObject)users_online.getJSONObject(0)).getInt(ONLINE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}