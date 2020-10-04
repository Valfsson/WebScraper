/**
 * Apartment object
 * 
 */
public class Appartment {

	private String location, link;
	private double rating = -1.0;

	public Appartment(String linkIn) {
		link = linkIn;
	}

	public void setLocation(String locationIn) {
		location = locationIn;
	}

	public void setRating(double ratingIn) {
		rating = ratingIn;
	}

	public String getLink() {
		return link;
	}

	public double getRating() {
		return rating;
	}

	public String getLocation() {
		return location;
	}

}
