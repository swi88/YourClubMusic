package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	ArrayList<Integer> selectedGenres;

	private Handler newPrefferedGenresHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updateView();
		}
	};
	
	ArrayAdapter<Genre> genresAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ArrayList<Genre> genres = new ArrayList<Genre>();
		genresAdapter = new PrefListAdapter(this,R.layout.view_pref,genres);
		
		final ListView listView=(ListView)findViewById(R.id.prefList);
        listView.setAdapter(genresAdapter);
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);
        
        // Swipe horizontally to delete item
        final SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (swipeDetector.swipeDetected()){
                        // do onSwipe action 
                        TextView genreTextView = (TextView) parent.getChildAt(position - listView.getFirstVisiblePosition()).findViewById(R.id.genreDescription);
                        if(genreTextView != null) {
                        	// Remove genre from favorites
                        	String genre = genreTextView.getText().toString();
                        	Set<String> preferredGenresOld = sharedPref.getStringSet(getString(R.string.saved_preferred_genres), new HashSet<String>());
                    		// Copy because preferredGenresOld is immutable
                    		final Set<String> preferredGenres = new HashSet<String>(preferredGenresOld);
                    		if(preferredGenres.remove(genre)) {
                    			System.out.println("Removing " + genre);
                    			SharedPreferences.Editor editor = sharedPref.edit();
                    			editor.putStringSet(getString(R.string.saved_preferred_genres), preferredGenres);
                    			editor.apply();
                    			updateView();
                    		}
                        }
                    } else {
                        // do an onItemClick action
                    }
                }
        });
        
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                SetGenresDialogFragment setGenresDialog = new SetGenresDialogFragment();
                setGenresDialog.setParentHandler(newPrefferedGenresHandler);
                setGenresDialog.show(fm, "fragment_set_genre");
            }
        });
        
        updateView();
	}

	private void updateView() {
		// Get preferred genres
		final SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		final Set<String> preferredGenres = sharedPref.getStringSet(
				getString(R.string.saved_preferred_genres),
				new HashSet<String>());
		// Sort set in list
		ArrayList<String> preferredGenresList = new ArrayList<String>(preferredGenres);
		Collections.sort(preferredGenresList);
		
		// Update view
		genresAdapter.clear();
		for (String genreID : preferredGenresList) {
			genresAdapter.add(new Genre(genreID, R.drawable.rock)); // TODO drawable
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
