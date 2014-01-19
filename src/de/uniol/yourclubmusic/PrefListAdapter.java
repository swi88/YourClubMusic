package de.uniol.yourclubmusic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PrefListAdapter extends ArrayAdapter<Genre> {
	Context context;
	int layoutResourceId;
	List<Genre> genres= null;


	public PrefListAdapter(Context context, int resource,List<Genre> genres) {
		super(context, resource,genres);
		this.context=context;
		this.layoutResourceId=resource;
		this.genres=genres;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View view = convertView; // re-use an existing view
		 if(view==null){
			 // create view
			 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 view=inflater.inflate(R.layout.view_pref, parent, false);
		 }
		 ImageView image= (ImageView)view.findViewById(R.id.genreIcon);
		 TextView text= (TextView) view.findViewById(R.id.genreDescription);
		 image.setImageResource(genres.get(position).getIconID());
		 text.setText(genres.get(position).getName());
		 return view;
	}
}
