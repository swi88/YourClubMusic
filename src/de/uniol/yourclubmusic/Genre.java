package de.uniol.yourclubmusic;

import android.util.Log;

public class Genre  implements Comparable<Genre> {
	
	public final static String ROCK = "Rock";
	public final static String HIPHOP = "Hip-Hop";
	public final static String NEWWAVE = "New Wave";
	public final static String BLUES = "Blues";
	public final static String FUNK = "Funk";
	public final static String JAZZ = "Jazz";
	public final static String SOUL = "Soul";
	public final static String REGGAE = "Reggae";
	public final static String DANCE = "Dance";
	public final static String HOUSE = "House";
	public final static String ELEKTRO = "Elektro";
	public final static String TECHNO = "Techno";
	public final static String TRANCE = "Trance";
	public final static String METAL = "Metal";
	public final static String ALTERNATIVE = "Alternative";
	public final static String PUNK = "Punk";
	public final static String HARDROCK = "Hardrock";
	public final static String RNB = "R&B";
	
	private String name;
	private double ratingPercent;
	private GenreListAdapter adapter;//observer
	private int iconID;
	
	public Genre(String name){
		this(name, -1);
	}
	
	Genre(String name,double ratingPercent){
		this.name=name;
		this.ratingPercent=ratingPercent;

		// Switch not possible in Java 1.6
		if(name.equals(ALTERNATIVE)) {
			iconID = R.drawable.genre_alternative;
		} else if(name.equals(BLUES)) {
			iconID = R.drawable.genre_blues;
		} else if(name.equals(DANCE)) {
			iconID = R.drawable.genre_dance;
		} else if(name.equals(ELEKTRO)) {
			iconID = R.drawable.genre_elektro;
		} else if(name.equals(FUNK)) {
			iconID = R.drawable.genre_funk;
		} else if(name.equals(HARDROCK)) {
			iconID = R.drawable.genre_hardrock;
		} else if(name.equals(HIPHOP)) {
			iconID = R.drawable.genre_hiphop;
		} else if(name.equals(HOUSE)) {
			iconID = R.drawable.genre_house;
		} else if(name.equals(JAZZ)) {
			iconID = R.drawable.genre_jazz;
		} else if(name.equals(METAL)) {
			iconID = R.drawable.genre_metal;
		} else if(name.equals(NEWWAVE)) {
			iconID = R.drawable.genre_newwave;
		} else if(name.equals(PUNK)) {
			iconID = R.drawable.genre_punk;
		} else if(name.equals(REGGAE)) {
			iconID = R.drawable.genre_reggae;
		} else if(name.equals(RNB)) {
			iconID = R.drawable.genre_rnb;
		} else if(name.equals(ROCK)) {
			iconID = R.drawable.genre_rock;
		} else if(name.equals(SOUL)) {
			iconID = R.drawable.genre_soul;
		} else if(name.equals(TECHNO)) {
			iconID = R.drawable.genre_techno;
		} else if(name.equals(TRANCE)) {
			iconID = R.drawable.genre_trance;
		} else {
			Log.i("Genre", "Using default icon for " + name);
			iconID = R.drawable.genre_default;
		}
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
		 if (getRatingInPercent() > another.getRatingInPercent()) return -1;
	     if (getRatingInPercent() < another.getRatingInPercent()) return 1;
	     return 0;
	}
}
