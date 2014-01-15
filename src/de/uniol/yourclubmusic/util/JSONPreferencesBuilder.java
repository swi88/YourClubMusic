package de.uniol.yourclubmusic.util;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.uniol.yourclubmusic.Genre;

public class JSONPreferencesBuilder {	
	public JSONPreferencesBuilder() {
	}
	
	public void createAndSend(ArrayList<Genre> genres) throws JSONException{
		JSONArray objects= new JSONArray();
		for (Genre genre : genres) {
			JSONObject object= new JSONObject();
			object.put(JSONParser.ID_GENRE, genre);
			objects.put(object);
		}
	}
}