import java.awt.EventQueue;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

/**
 * Web scraper application. Scans filtered link (with custom-filters on) from
 * "airbnb web site". Extracts list of apartments results are recorded to the
 * database (Airbnb) with columns: 1) location (String) 2) rating (double) 3)
 * link (string)
 * 
 */

public class Main {

	private JFrame frame;
	private static Map<String, Appartment> appartments;
	private static JTextField txtEnterLink;
	private Connection connection;
	private static JTextArea textArea;
	private static JButton btnNewButton, btnSaveToDatabase;
	private static Scraper scraperThread;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		frame.getContentPane().setForeground(new Color(153, 153, 204));
		frame.setBounds(100, 100, 671, 484);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblAirbnb = new JLabel("AIRBNB SCRAPER");
		lblAirbnb.setFont(new Font("AppleGothic", Font.PLAIN, 16));
		lblAirbnb.setForeground(new Color(102, 0, 102));
		lblAirbnb.setBounds(258, 19, 179, 31);
		frame.getContentPane().add(lblAirbnb);

		txtEnterLink = new JTextField();
		txtEnterLink.setFont(new Font("AppleGothic", Font.PLAIN, 13));
		txtEnterLink.setText("Enter link");
		txtEnterLink.setBounds(18, 78, 391, 26);
		frame.getContentPane().add(txtEnterLink);
		txtEnterLink.setColumns(10);

		btnNewButton = new JButton("OK");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				appartments = new HashMap<>();

				String temp = txtEnterLink.getText();
				txtEnterLink.setText("Loading..");

				if (temp.equalsIgnoreCase("Enter link") || temp.equalsIgnoreCase("")) {
					System.out.println("Please enter the URL to be scaned");
				} else {
					textArea.setText("The process is going to take few minutes...");
					btnNewButton.setEnabled(false);
					txtEnterLink.setEnabled(false);
					btnSaveToDatabase.setEnabled(false);
					scraperThread = new Scraper(temp); // creates and starts Scraper-Thread

				}

			}
		});
		btnNewButton.setBounds(292, 105, 117, 29);
		frame.getContentPane().add(btnNewButton);

		btnSaveToDatabase = new JButton("Save to database");
		btnSaveToDatabase.setEnabled(false);
		btnSaveToDatabase.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				save();
			}
		});
		btnSaveToDatabase.setBounds(485, 392, 149, 31);
		frame.getContentPane().add(btnSaveToDatabase);

		JLabel lblCreateNewList = new JLabel("Create new list");
		lblCreateNewList.setBounds(23, 57, 93, 16);
		frame.getContentPane().add(lblCreateNewList);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, 143, 448, 280);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
	}

	/**
	 * Gets apartment list from Scraper list and sets UI ready for extraction of
	 * list t o database
	 * 
	 * @param appartmentsIn HashMap with apartments
	 */
	protected static void setAppartmentMap(Map<String, Appartment> appartmentsIn) {

		// gets apartments from Scraper thread
		appartments = appartmentsIn;

		// kills the Scraper thread
		scraperThread.killThread();

		// sets new set of action available for user
		btnNewButton.setEnabled(true);
		txtEnterLink.setEnabled(true);
		btnSaveToDatabase.setEnabled(true);
		txtEnterLink.setText("Enter link");

		String appPrint = "";
		for (Appartment a : appartments.values()) {
			appPrint = appPrint + a.getLocation() + "  --> " + a.getRating() + "  --> " + a.getLink() + "\n";
		}
		System.out.println(appPrint);
		textArea.setText(appPrint);

	}

	/**
	 * Saves apartments-list to database
	 */
	private void save() {

		DatabaseConnection connectionClass = new DatabaseConnection();
		connection = connectionClass.getConnection();

		for (Appartment a : appartments.values()) {
			saveAppartment(a.getLocation(), a.getRating(), a.getLink());
		}
		System.out.println("Import to database completed");
		textArea.setText("Import to database process is done");
		
	}

	/**
	 * Imports one apartment at time to the database
	 * 
	 * @param locationString
	 * @param ratingDouble
	 * @param linkText
	 */
	private void saveAppartment(String locationString, double ratingDouble, String linkText) {

		// adds a comment --> `column name`
		// --> 'value'

		String insert = "INSERT INTO `Airbnb` (`location`, `rating`, `link`) VALUES ('" + locationString + "', '"
				+ ratingDouble + "', '" + linkText + "')";
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(insert);
		} catch (SQLException e) {
			System.out.println("Data inport to database failed");
		}
	}

}