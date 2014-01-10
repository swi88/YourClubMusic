package de.uniol.yourclubmusic;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SettingsActivity extends Activity {
	
	ArrayList<Integer> selectedGenres;

	private Handler selectionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			selectedGenres = (ArrayList<Integer>)bundle.getSerializable("Selection");
			genresAdapter.clear();
			
			for(Integer selectedGenre : selectedGenres) {
				System.out.println("Selected " + Genre.genreArray[selectedGenre]);
				genresAdapter.add(new Genre(Genre.genreArray[selectedGenre],R.drawable.rock)); // TODO ICON
			}
		}

//		public void setSelection(boolean[] selection) {
//			
//		}
	};
	
	ArrayAdapter<Genre> genresAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ArrayList<Genre> genres = new ArrayList<Genre>();
        genres.add(new Genre("Rock",R.drawable.rock));
        genres.add(new Genre("Alternative",R.drawable.alternative));
		// Create and fill preferences list
		genresAdapter = new PrefListAdapter(this,R.layout.view_pref,genres);
		
		ListView listView=(ListView)findViewById(R.id.prefList);
        listView.setAdapter(genresAdapter);
        
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                SetGenresDialogFragment setGenresDialog = new SetGenresDialogFragment();
                setGenresDialog.setSelectionHandler(selectionHandler);
                setGenresDialog.show(fm, "fragment_set_genre");
                System.out.println("Test");
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
