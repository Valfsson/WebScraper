import java.sql.Connection;
import java.sql.DriverManager;
import java.util.TimeZone;

/**
 * Establishing connection to Database
 * 
 */
public class DatabaseConnection {

	public Connection connection;

	/**
	 * Establishes connection to Database
	 * 
	 * @return Connection object
	 */
	public Connection getConnection() {

		/*
		 * Apparently, to get version 5.1.33 of MySQL JDBC driver to work with UTC time
		 * zone, one has to specify the serverTimezone explicitly in the connection
		 * string.
		 */
		String url = "jdbc:mysql://YOUR DATABASE" + TimeZone.getDefault().getID();
		String userName = "YOUR USERNAME";
		String password = "YOUR PASSWORD";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, userName, password);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return connection;
	}
}
