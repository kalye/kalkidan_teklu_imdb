/**
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mehdi
 *
 */
public class DatabaseAccess {

	static final String DRIVE_NAME = "com.mysql.jdbc.Driver";

	static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/imdb";

	static final String DB_CONNECTION_USERNAME = "demo";

	static final String DB_CONNECTION_PASSWORD = "demo";

	public static Connection connect() {
		Connection con = null;
		try {
			Class.forName(DRIVE_NAME);
			con = DriverManager.getConnection(CONNECTION_URL, DB_CONNECTION_USERNAME, DB_CONNECTION_PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	} // end of connect

	/*
	 * Generic method to fetch data from DB and return them as java object
	 * @param Connection con
	 * @param String query
	 * @Param T generic type
	 * @return List of Object of Type T
	 */
	public static <T> List<? extends Object> retrieve(Connection con, String query, T clazz) {
		ResultSet rset = null;
		List<T> list = new ArrayList<T>();
		try {
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery(query);
			if (rset != null) {
				while (rset.next()) {
					list.add(getEntity(clazz, rset));
				}
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}// end of retrieve

	/*
	 * Generic method to convert ResultSet to java Type object
	 * @param T generic type
	 * @param ResultSet resultSet
	 * @return Object of Type T
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getEntity(T clazz, ResultSet resultSet) throws SQLException {
		if (clazz.equals(Actor.class)) {
			Actor actor = new Actor();
			actor.setFields(resultSet);
			return (T) actor;
		} else if (clazz.equals(Director.class)) {
			Director director = new Director();
			director.setFields(resultSet);
			return (T) director;
		} else if (clazz.equals(Movie.class)) {
			Movie movie = new Movie();
			movie.setFields(resultSet);
			return (T) movie;
		}
		return null;
	}

	public static void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // end of closeConnection

}
