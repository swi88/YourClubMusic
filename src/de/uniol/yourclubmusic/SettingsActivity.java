package de.uniol.yourclubmusic;

import java.lang.ref.WeakReference;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	ArrayList<Genre> genres;
	SwipeDetector swipeDetector;
	
	static class PrefferedGenresHandler extends Handler {
		private final WeakReference<SettingsActivity> settingsActivity; 

		private PrefferedGenresHandler(SettingsActivity settingsActivity) {
			this.settingsActivity = new WeakReference<SettingsActivity>(settingsActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			final SettingsActivity context = settingsActivity.get();
			if(context != null) {
				context.updateView();
			}
		}
	}
	
	ArrayList<Integer> selectedGenres;

	private Handler newPrefferedGenresHandler;
	
	ArrayAdapter<Genre> genresAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ArrayList<Genre> genres = new ArrayList<Genre>();
		genresAdapter = new PrefListAdapter(this,R.layout.view_pref,genres);
		
		ListView listView=(ListView)findViewById(R.id.prefList);
        listView.setAdapter(genresAdapter);
        swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);
        
        newPrefferedGenresHandler = new PrefferedGenresHandler(this);
        
        // Swipe horizontally to delete item
        listView.setOnItemClickListener(new MyListViewOnClickListener(this));
        
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new MyAddButtonOnClickListener(this));
        
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
		genres= new ArrayList<Genre>();
		for (String genreID : preferredGenresList) {
			Genre g= new Genre(genreID);
			genresAdapter.add(g);
			genres.add(g);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	@Override
	public void onBackPressed() {
		//update genres
		Websocket.getInstance().sendGenres(genres);
		super.onBackPressed();
	}

	static class MyListViewOnClickListener implements OnItemClickListener {
		private final WeakReference<SettingsActivity> settingsActivity;
		
        public MyListViewOnClickListener(SettingsActivity settingsActivity) {
        	this.settingsActivity = new WeakReference<SettingsActivity>(settingsActivity);
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final SettingsActivity context = settingsActivity.get();
			final SharedPreferences sharedPref = context.getSharedPreferences(
					context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
			if(context != null) {
				ListView listView=(ListView)context.findViewById(R.id.prefList);
				
				if (context.swipeDetector.swipeDetected()){
	                // do onSwipe action 
	                TextView genreTextView = (TextView) parent.getChildAt(position - listView.getFirstVisiblePosition()).findViewById(R.id.genreDescription);
	                if(genreTextView != null) {
	                	// Remove genre from favorites
	                	String genre = genreTextView.getText().toString();
	                	Set<String> preferredGenresOld = sharedPref.getStringSet(context.getString(R.string.saved_preferred_genres), new HashSet<String>());
	            		// Copy because preferredGenresOld is immutable
	            		final Set<String> preferredGenres = new HashSet<String>(preferredGenresOld);
	            		if(preferredGenres.remove(genre)) {
	            			Log.i("SettingsActivity", "Removing " + genre);
	            			SharedPreferences.Editor editor = sharedPref.edit();
	            			editor.putStringSet(context.getString(R.string.saved_preferred_genres), preferredGenres);
	            			editor.apply();
	            			context.updateView();
	            		}
	                }
	            } else {
	                // do an onItemClick action
	            }
			}
        }
    }
	
	static class MyAddButtonOnClickListener implements OnClickListener {
		private final WeakReference<SettingsActivity> settingsActivity;
		
        public MyAddButtonOnClickListener(SettingsActivity settingsActivity) {
        	this.settingsActivity = new WeakReference<SettingsActivity>(settingsActivity);
		}

		public void onClick(View v) {
			final SettingsActivity context = settingsActivity.get();
			if(context != null) {
				FragmentManager fm = context.getFragmentManager();
	            SetGenresDialogFragment setGenresDialog = new SetGenresDialogFragment();
	            setGenresDialog.setParentHandler(context.newPrefferedGenresHandler);
	            setGenresDialog.show(fm, "fragment_set_genre");
			}
        }
    }
}
