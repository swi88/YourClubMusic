package de.uniol.yourclubmusic;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ArrayList<Genre> genres = new ArrayList<Genre>();
        genres.add(new Genre("Rock",R.drawable.rock));
        genres.add(new Genre("Alternative",R.drawable.alternative));
		// Create and fill preferences list
		ArrayAdapter<Genre> adapter= new PrefListAdapter(this,R.layout.view_pref,genres);
		
		ListView listView=(ListView)findViewById(R.id.prefList);
        listView.setAdapter(adapter);
        
        Button addButton = (Button)findViewById(R.id.addButton);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
