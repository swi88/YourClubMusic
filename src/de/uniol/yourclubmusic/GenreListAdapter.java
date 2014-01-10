package de.uniol.yourclubmusic;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GenreListAdapter extends ArrayAdapter<Genre> {
	Context context;
	int layoutResourceId;
	List<Genre> genres= null;


	public GenreListAdapter(Context context, int resource,List<Genre> genres) {
		super(context, resource,genres);
		this.context=context;
		this.layoutResourceId=resource;
		this.genres=genres;
		//register observer
		for (Genre genre : genres) {
			genre.registerGenreAdapter(this);
		}
		Collections.sort(genres);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View view = convertView; // re-use an existing view
		 if(view==null){
			 // create view
			 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 view=inflater.inflate(R.layout.view_genre, parent, false);

			 
		 }
		 
		 ImageView bar=(ImageView) view.findViewById(R.id.genrePercentBar);
		 bar.setImageBitmap(drawBar(genres.get(position).getRatingInPercent()));
		 ImageView image= (ImageView)view.findViewById(R.id.genreIcon);
		 TextView text= (TextView) view.findViewById(R.id.genrePercentRate);
		 image.setImageResource(genres.get(position).getIconID());
		 text.setText(genres.get(position).getRatingInPercent()+"%");
		 return view;
	}
	private Bitmap drawBar(double ratingInPercent) {
		Bitmap bitmap = Bitmap.createBitmap(200, 50, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.FILL); 
		paint.setAntiAlias(true); 
		paint.setColor(Color.RED); 

		canvas.drawRect(0, 0, (float)ratingInPercent*2, 50, paint);
			 
		return bitmap;
	}
	public void update(){
		Collections.sort(genres);
		notifyDataSetChanged();
	}

}
