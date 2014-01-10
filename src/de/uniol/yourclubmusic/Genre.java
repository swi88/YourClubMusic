package de.uniol.yourclubmusic;

import java.util.Comparator;

import android.widget.ArrayAdapter;


public class Genre  implements Comparable<Genre>{
	private String name;
	private double ratingPercent;
	private GenreListAdapter adapter;//observer
	int iconID;
	
	public Genre(String name, int iconID){
		this.name=name;
		this.iconID=iconID;
	}
	public Genre(String name,double ratingPercent,int iconID){
		this.name=name;
		this.ratingPercent=ratingPercent;
		this.iconID=iconID;
	}
	public int getIconID(){
		return iconID;
	}
	public String getName(){
		return name;
	}
	public double getRatingInPercent(){
		return ratingPercent;
	}
	public void setRatingInPercent(float ratingPercent){
		this.ratingPercent=ratingPercent;
		adapter.update();
	}
	public void registerGenreAdapter(GenreListAdapter adapter){
		this.adapter=adapter;
		
	}
	@Override
	public int compareTo(Genre another) {
		 if (getRatingInPercent() < another.getRatingInPercent()) return -1;
	     if (getRatingInPercent() > another.getRatingInPercent()) return 1;
	     return 0;
	}
}