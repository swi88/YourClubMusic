package de.uniol.yourclubmusic;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SetGenresDialogFragment extends DialogFragment {
	
	private ArrayList<Integer> selectedItems;
	private Handler parentSelectionHandler;
	
    public SetGenresDialogFragment() {
    }
    
    public void setSelectionHandler(Handler selectionHandler) {
    	parentSelectionHandler = selectionHandler;
    }

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    selectedItems = new ArrayList<Integer>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Set the dialog title
	    builder.setTitle(R.string.add_button)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(Genre.genreArray, null,
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
	                   // User clicked OK, so save the selectedItems results somewhere
	                   // or return them to the component that opened the dialog
	            	   Message msg = new Message();
	            	   Bundle bundle = new Bundle();
	            	   bundle.putSerializable("Selection", selectedItems);
	            	   msg.setData(bundle);
	            	   parentSelectionHandler.dispatchMessage(msg);
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //...
	               }
	           });

	    return builder.create();
	}
}