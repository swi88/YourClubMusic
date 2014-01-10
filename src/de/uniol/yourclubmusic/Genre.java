package de.uniol.yourclubmusic;

import java.util.Comparator;	
import android.widget.ArrayAdapter;


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

	public final static String[] genreArray= {
		ALTERNATIVE,
		BLUES,
		DANCE,
		ELEKTRO,
		FUNK,
		HARDROCK,
		HIPHOP,
		HOUSE,
		JAZZ,
		METAL,
		NEWWAVE,
		PUNK,
		REGGAE,
		RNB,
		ROCK,
		SOUL,
		TECHNO,
		TRANCE
	};
	
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
