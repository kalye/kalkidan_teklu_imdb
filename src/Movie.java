import java.sql.ResultSet;
import java.sql.SQLException;

public class Movie {

	private int id;
	private String name;
	private int year;
	private String rank;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public void setFields(ResultSet rs) throws SQLException{
		this.id = rs.getInt(1);
		this.name = rs.getString(2);
		this.year = rs.getInt(3);
		this.rank = rs.getString(4);
	}
}
