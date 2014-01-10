package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private List<Genre> genres= new ArrayList<Genre>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO in final version only create entry, if genre exists
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        genres.add(new Genre("Rock", 75,R.drawable.rock));
        
        genres.add(new Genre("Alternative", 25,R.drawable.alternative));
        ArrayAdapter<Genre> adapter= new GenreListAdapter(this,R.layout.view_genre,genres);
        ListView listView=(ListView)findViewById(R.id.listViewCurrentMood);
        listView.setAdapter(adapter);
        genres.get(1).setRatingInPercent(22);
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
        default:
            return super.onOptionsItemSelected(item);
    }
    }
    
}
