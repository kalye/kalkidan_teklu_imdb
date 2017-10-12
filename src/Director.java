import java.sql.ResultSet;
import java.sql.SQLException;

public class Director {

	private int id;
	private String first_name;
	private String last_name;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public void setFields(ResultSet rs) throws SQLException {
		this.id = rs.getInt(1);
		this.first_name = rs.getString(2);
		this.last_name = rs.getString(3);
	}
}
