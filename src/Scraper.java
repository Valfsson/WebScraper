import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Thread that handles scraping activities
 *
 */
class Scraper extends Thread {

	private static Map<String, Appartment> appartments = new HashMap<>();;
	private String url;
	private boolean alive;

	/**
	 * Constructs Thread
	 */
	public Scraper(String urlIn) {
		url = urlIn;
		System.out.println("Starting to Scrap");
		alive = true;
		start();

	}

	/**
	 * Kills this thread
	 */
	public void killThread() {
		alive = false;
		System.out.println("Scraping process has finnished. The thread is killed");
	}

	/**
	 * Runs this thread
	 */
	public void run() {
		while (alive) {
			System.out.println("Thread is active");
			try {
				scrap(url);
				alive = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				sleep(1000);
			} catch (InterruptedException ie) {
			}
		} // end- alive
	}

	/**
	 * Gets list of apartments and extracts their links
	 * 
	 * @param urlWebsite
	 * @throws IOException
	 */
	protected void scrap(String urlWebsite) throws IOException {

		System.out.println("Scaping has begun..");

		Document doc = Jsoup.connect(urlWebsite).get();
		Elements elements = doc.getElementsByClass("_sqvp1j");

		for (Element element : elements) {
			String link = "https://www.airbnb.se" + element.attributes().get("href");
			Appartment appartment = new Appartment(link);
			appartments.put(link, appartment);

			// extracts the rating 4 times for each element to increase a chance of getting
			// information from all elements
			int i = 0;
			while (i < 4) {
				extractLocation(link);
				i++;
			}

			int j = 0;
			while (j < 4) {
				extractRating(link);
				j++;
			}
		}
		writeUI();
	}

	/**
	 * Extracts the location element from the apartment URL
	 * 
	 * @param link String with URL to be scanned
	 * @throws IOException
	 */
	private void extractLocation(String link) throws IOException {

		Document doc2 = Jsoup.connect(link).get();
		Elements elements = doc2.getElementsByClass("_13myk77s");

		for (Element element : elements) {
			String location = element.text();

			if (appartments.get(link).getLocation() == null) {
				appartments.get(link).setLocation(location);
			}
		}
	}

	/**
	 * Extracts the rating element from the apartment URL
	 * 
	 * @param link String with URL to be scanned
	 * @throws IOException
	 */
	private void extractRating(String link) throws IOException {

		Document doc2 = Jsoup.connect(link).get();
		Elements elements = doc2.getElementsByClass("_1jpdmc0");

		for (Element element : elements) {
			String rating0 = element.text();
			String rating1 = rating0.replace(",", ".");
			double rating = Double.parseDouble(rating1);

			if (appartments.get(link).getRating() == -(1.0)) {
				appartments.get(link).setRating(rating);
			}
		}
	}

	/**
	 * Sends the Map with the apartments to Main activity
	 */
	private void writeUI() {
		Main.setAppartmentMap(appartments);

	}
}
