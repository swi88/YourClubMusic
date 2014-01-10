package de.uniol.yourclubmusic;


public class Genre {
	private String name;
	private double ratingPercent;
	int iconID;
	
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
	}
}
