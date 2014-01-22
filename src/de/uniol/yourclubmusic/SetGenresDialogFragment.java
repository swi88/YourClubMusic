package de.uniol.yourclubmusic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SetGenresDialogFragment extends DialogFragment {
	
	private ArrayList<Integer> selectedItems;
	
	private Handler parentHandler;
	
	private static TreeSet<String> availableGenres;
	
    public SetGenresDialogFragment() {
    }
    
	public void setParentHandler(Handler parentHandler) {
		this.parentHandler = parentHandler;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		availableGenres = new TreeSet<String>();
		availableGenres.add(Genre.ALTERNATIVE);
		availableGenres.add(Genre.BLUES);
		availableGenres.add(Genre.DANCE);
		availableGenres.add(Genre.ELEKTRO);
		availableGenres.add(Genre.FUNK);
		availableGenres.add(Genre.HARDROCK);
		availableGenres.add(Genre.HIPHOP);
		availableGenres.add(Genre.HOUSE);
		availableGenres.add(Genre.JAZZ);
		availableGenres.add(Genre.METAL);
		availableGenres.add(Genre.NEWWAVE);
		availableGenres.add(Genre.PUNK);
		availableGenres.add(Genre.REGGAE);
		availableGenres.add(Genre.RNB);
		availableGenres.add(Genre.ROCK);
		availableGenres.add(Genre.SOUL);
		availableGenres.add(Genre.TECHNO);
		availableGenres.add(Genre.TRANCE);
		
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		String imgSett = prefs.getString(keyChannel, "");
		// Retrieve currently preferred genres
		Context context = getActivity();
		final SharedPreferences sharedPref = context.getSharedPreferences(
		        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		Set<String> preferredGenresOld = sharedPref.getStringSet(getString(R.string.saved_preferred_genres), new HashSet<String>());
		// Copy because preferredGenresOld is immutable
		final Set<String> preferredGenres = new HashSet<String>(preferredGenresOld);
		
		// super inefficient, but whatevs, Get 'unpreffered' genres
		@SuppressWarnings("unchecked")
		Set<String> unPreferredGenresSet = (Set<String>) availableGenres.clone();
		unPreferredGenresSet.removeAll(preferredGenresOld);
		final String [] unPreferredGenres =(String[]) unPreferredGenresSet.toArray(new String[unPreferredGenresSet
				.size()]);
		
	    selectedItems = new ArrayList<Integer>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Set the dialog title
	    builder.setTitle(R.string.add_button)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(unPreferredGenres, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       selectedItems.add(which);
	                   } else if (selectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       selectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	    // Set the action buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   for(int i : selectedItems) {
	            		   preferredGenres.add(unPreferredGenres[i]);
	            	   }
	            	   
	                   // User clicked OK, so save the selectedItems results somewhere
	            	   SharedPreferences.Editor editor = sharedPref.edit();
	            	   editor.putStringSet(getString(R.string.saved_preferred_genres), preferredGenres);
	            	   // for(String s : preferredGenres) System.out.print(s + ", "); System.out.println();
	            	   editor.apply(); //commit();
	            	   
	            	   // notify parent (SettingsActivity)
	            	   parentHandler.dispatchMessage(new Message());
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // Nothing to do
	               }
	           });

	    return builder.create();
	}
}