
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Servlet implementation class FreeMarkerServlet
 * 
 * @author kalkidan
 * 
 */
@WebServlet({ "/search" })
public class IMDbServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Configuration cfg = null;
	private String templateDir = "/WEB-INF/templates";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IMDbServlet() {
		super();
	}

	public void init() {
		cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setServletContextForTemplateLoading(getServletContext(), templateDir);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean isGet)
			throws Exception {
		String searchBy = (String) request.getParameter("searchBy");
		DefaultObjectWrapperBuilder df = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
		SimpleHash root = new SimpleHash(df.build());
		boolean valid = validateInput(request);
		if (!valid) {
			root.put("error", true);
			root.put("message", "Invalid input. Please try again.");
			renderTemplate(request, response, "error.ftl", root);
			return;
		}
		String query = null;
		boolean actor = false;
		boolean director = false;
		boolean movie = false;
		if ("actor".equals(searchBy)) {
			query = getActorQuery(request);
			actor = true;
		} else if ("director".equals(searchBy)) {
			director = true;
			query = getDirectorQuery(request);
		} else if ("movie".equals(searchBy)) {
			movie = true;
			query = getMovieQuery(request);
		}
		Connection con = DatabaseAccess.connect();
		if (con == null) {
			root.put("error", true);
			root.put("message", "Something went wrong while searcing DB query. Please try again.");
			renderTemplate(request, response, "error.ftl", root);
			return;
		}
		List<? extends Object> results = null;
		if (actor) {
			results = DatabaseAccess.retrieve(con, query, Actor.class);
			root.put("actors", true);
			root.put("results", results);
		} else if (director) {
			results = DatabaseAccess.retrieve(con, query, Director.class);
			root.put("directors", true);
			root.put("results", results);
		} else if (movie) {
			results = DatabaseAccess.retrieve(con, query, Movie.class);
			root.put("movies", true);
			root.put("results", results);
		}
		renderTemplate(request, response, "result.ftl", root);
		DatabaseAccess.closeConnection(con);
	}

	private String getMovieQuery(HttpServletRequest request) throws Exception {
		StringBuilder bd = new StringBuilder();
		bd.append("SELECT * FROM MOVIES ");
		String mtitle = (String) request.getParameter("title");
		String myear = (String) request.getParameter("year");
		boolean whereadded = false;
		if (StringUtils.isNotBlank(mtitle)) {
			bd.append("WHERE ");
			whereadded = true;
			bd.append("name = '").append(mtitle).append("' ");
		}
		if (StringUtils.isNotBlank(myear)) {
			if (!whereadded) {
				bd.append("WHERE ");
			} else {
				bd.append("and ");
			}
			bd.append("year = '").append(myear).append("' ");
		}
		// String[] genres = request.getParameterValues("genres");
		// String movieGenres = singleQuoteAndComma(genres);
		// if (StringUtils.isNotBlank(movieGenres)) {
		// if (!whereadded) {
		// bd.append("WHERE ");
		// whereadded = true;
		// } else {
		// bd.append("and ");
		// }
		// bd.append(" id in (select mg.movie_id from movies_genres mg and
		// ").append("mg.genre in (")
		// .append(movieGenres).append(")").append(") ");
		// }
		// String afirstname = (String) request.getParameter("afirstname");
		// Map<String, String> firstLastNameMap =
		// getNamesAsMap(afirstname.trim());
		//
		// // roles-movie-id=movies_directors.movie_id and
		// // movies_directors.director_id = directors.id and
		// directors.first_name
		// // and directors.last_name
		// if (!firstLastNameMap.isEmpty()) {
		// if (!whereadded) {
		// bd.append("WHERE ");
		// whereadded = true;
		// } else {
		// bd.append("and ");
		// }
		// bd.append("id in (select r.movie_id from roles r inner join actors a
		// on a.id = r.actor_id ");
		// int i = 0;
		// for (Map.Entry<String, String> entry : firstLastNameMap.entrySet()) {
		// if (i == 0) {
		// bd.append("and a.first_name ='").append(entry.getKey()).append("'
		// ").append("and a.last_name ='")
		// .append(entry.getValue()).append("' ");
		// } else {
		// bd.append("or a.first_name ='").append(entry.getKey()).append("'
		// ").append("and a.last_name ='")
		// .append(entry.getValue()).append("' ");
		// }
		// i++;
		// }
		// bd.append(") ");
		// }
		// String dfirstname = (String) request.getParameter("dfirstname");
		// Map<String, String> dfirstnameNameMap =
		// getNamesAsMap(dfirstname.trim());
		//
		// if (!dfirstnameNameMap.isEmpty()) {
		// if (!whereadded) {
		// bd.append("WHERE ");
		// whereadded = true;
		// } else {
		// bd.append("and ");
		// }
		// bd.append(
		// "id in (select md.movie_id from movies_directors md inner join
		// directors d on d.id = md.director_id ");
		// int i = 0;
		// for (Map.Entry<String, String> entry : firstLastNameMap.entrySet()) {
		// if (i == 0) {
		// bd.append("and d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// } else {
		// bd.append("or d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// }
		// i++;
		// }
		// bd.append(") ");
		// }
		return bd.toString();
	}

	private String getDirectorQuery(HttpServletRequest request) throws Exception {
		StringBuilder bd = new StringBuilder();
		bd.append("SELECT * FROM DIRECTORS ");
		String firstname = (String) request.getParameter("firstname");
		boolean whereadded = false;
		if (StringUtils.isNotBlank(firstname)) {
			bd.append("WHERE ");
			whereadded = true;
			bd.append("first_name = '").append(firstname).append("' ");
		}
		String lastname = (String) request.getParameter("lastname");
		if (StringUtils.isNotBlank(lastname)) {
			if (!whereadded) {
				bd.append("WHERE ");
			} else {
				bd.append("and ");
			}
			bd.append("last_name = '").append(lastname).append("' ");
		}
		// String[] genres = request.getParameterValues("genres");
		// String directorGenres = singleQuoteAndComma(genres);
		// if (StringUtils.isNotBlank(directorGenres)) {
		// bd.append("and id in (select g.director_id from directors_genres g
		// and ").append("g.genre in (")
		// .append(directorGenres).append(")").append(") ");
		// }
		// String mtitle = (String) request.getParameter("mtitle");
		// String myear = (String) request.getParameter("myear");
		// if (StringUtils.isNotBlank(mtitle) || StringUtils.isNotBlank(myear))
		// {
		// bd.append(
		// "and id in (select md.director_id from movies_directors md inner join
		// movies m on md.movie_id = m.id ");
		// if (StringUtils.isNotBlank(mtitle)) {
		// bd.append("and m.name = '").append(mtitle).append("' ");
		// }
		// if (StringUtils.isNotBlank(myear)) {
		// bd.append("and m.year = '").append(myear).append("' ");
		// }
		// bd.append(") ");
		// }
		// String afirstname = (String) request.getParameter("afirstname");
		// Map<String, String> firstLastNameMap =
		// getNamesAsMap(afirstname.trim());
		//
		// // roles-movie-id=movies_directors.movie_id and
		// // movies_directors.director_id = directors.id and
		// directors.first_name
		// // and directors.last_name
		// if (!firstLastNameMap.isEmpty()) {
		// bd.append(
		// "and id in (select md.director_id from movies_directors md inner join
		// movies m on md.movie_id = m.id ")
		// .append("inner join roles r on r.movie_id = m.id inner join actors a
		// on a.id = r.actor_id ");
		// int i = 0;
		// for (Map.Entry<String, String> entry : firstLastNameMap.entrySet()) {
		// if (i == 0) {
		// bd.append("and d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// } else {
		// bd.append("or d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// }
		// i++;
		// }
		// bd.append(") ");
		// }
		// String[] actorgeneres = request.getParameterValues("actorgeneres");
		// String actorgeneresString = singleQuoteAndComma(actorgeneres);
		// // roles-movie-id=movies_directors.movie_id and
		// // movies_directors.director_id = directors_genres.director_id and
		// // directors_genres.genre
		// if (StringUtils.isNotBlank(actorgeneresString)) {
		// bd.append(
		// "and id in (select md.director_id from movies_directors md inner join
		// movies m on md.movie_id = m.id ")
		// .append("inner join roles r on r.movie_id = m.id inner join actors a
		// on a.id = r.actor_id ")
		// .append(" and m.genre in
		// (").append(actorgeneresString).append(")").append(") ");
		// }
		return bd.toString();
	}

	private boolean validateInput(HttpServletRequest request) {
		String searchBy = (String) request.getParameter("searchBy");
		if ("actor".equals(searchBy)) {
			String numberofmovies = (String) request.getParameter("numberofmovies");
			if (StringUtils.isNotBlank(numberofmovies) && !StringUtils.isNumeric(numberofmovies)) {
				return false;
			}
			String myear = (String) request.getParameter("myear");
			if (StringUtils.isNotBlank(myear) && !StringUtils.isNumeric(myear)) {
				return false;
			}
		}
		if ("director".equals(searchBy)) {
			String myear = (String) request.getParameter("myear");
			if (StringUtils.isNotBlank(myear) && !StringUtils.isNumeric(myear)) {
				return false;
			}
		}
		if ("movie".equals(searchBy)) {
			String year = (String) request.getParameter("year");
			if (StringUtils.isNotBlank(year) && !StringUtils.isNumeric(year)) {
				return false;
			}
		}
		return true;
	}

	private String getActorQuery(HttpServletRequest request) throws Exception {
		StringBuilder bd = new StringBuilder();
		bd.append("SELECT * FROM ACTORS WHERE ");
		String gender = (String) request.getParameter("gender");
		if (StringUtils.isNotBlank(gender)) {
			bd.append("gender = '").append(gender).append("' ");
		}
		String firstname = (String) request.getParameter("firstname");
		if (StringUtils.isNotBlank(firstname)) {
			bd.append("and first_name = '").append(firstname).append("' ");
		}
		String lastname = (String) request.getParameter("lastname");
		if (StringUtils.isNotBlank(lastname)) {
			bd.append("and last_name = '").append(lastname).append("' ");
		}
		String numberofmovies = (String) request.getParameter("numberofmovies");
		if (StringUtils.isNotBlank(numberofmovies)) {
			bd.append("and film_count = ").append(numberofmovies).append(" ");
		}
		// String[] genres = request.getParameterValues("genres");
		// String actorGenres = singleQuoteAndComma(genres);
		// if (StringUtils.isNotBlank(actorGenres)) {
		// bd.append(
		// "and id in (select r.actor_id from roles r inner join movies_genres g
		// on r.movie_id = g.movie_id and ")
		// .append("g.genre in (").append(actorGenres).append(")").append(") ");
		// }
		// String mtitle = (String) request.getParameter("mtitle");
		// String myear = (String) request.getParameter("myear");
		// if (StringUtils.isNotBlank(mtitle) || StringUtils.isNotBlank(myear))
		// {
		// bd.append("and id in (select r.actor_id from roles r inner join
		// movies m on r.movie_id = m.id ");
		// if (StringUtils.isNotBlank(mtitle)) {
		// bd.append("and m.name = '").append(mtitle).append("' ");
		// }
		// if (StringUtils.isNotBlank(myear)) {
		// bd.append("and m.year = '").append(myear).append("' ");
		// }
		// bd.append(") ");
		// }
		// String dfirstname = (String) request.getParameter("dfirstname");
		// Map<String, String> firstLastNameMap =
		// getNamesAsMap(dfirstname.trim());
		//
		// // roles-movie-id=movies_directors.movie_id and
		// // movies_directors.director_id = directors.id and
		// directors.first_name
		// // and directors.last_name
		// if (!firstLastNameMap.isEmpty()) {
		// bd.append(
		// "and id in (select r.actor_id from roles r inner join
		// movies_directors m on r.movie_id = m.movie_id ")
		// .append("inner join directors d on m.director_id = d.id ");
		// int i = 0;
		// for (Map.Entry<String, String> entry : firstLastNameMap.entrySet()) {
		// if (i == 0) {
		// bd.append("and d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// } else {
		// bd.append("or d.first_name ='").append(entry.getKey()).append("'
		// ").append("and d.last_name ='")
		// .append(entry.getValue()).append("' ");
		// }
		// i++;
		// }
		// bd.append(") ");
		// }
		// String[] directorgenres =
		// request.getParameterValues("directorgeneres");
		// String directorgenresString = singleQuoteAndComma(directorgenres);
		// // roles-movie-id=movies_directors.movie_id and
		// // movies_directors.director_id = directors_genres.director_id and
		// // directors_genres.genre
		// if (StringUtils.isNotBlank(directorgenresString)) {
		// bd.append(
		// "and id in (select r.actor_id from roles r inner join
		// movies_directors m on r.movie_id = m.movie_id ")
		// .append("inner join directors_genres d on m.director_id =
		// d.director_id ")
		// .append(" and d.genre in
		// (").append(directorgenresString).append(")").append(") ");
		// }
		return bd.toString();
	}

	private Map<String, String> getNamesAsMap(String dfirstname) throws Exception {
		Map<String, String> firstLastMap = new HashMap<>();
		if (StringUtils.isBlank(dfirstname)) {
			return firstLastMap;
		}
		String[] name = StringUtils.split(dfirstname, ",");
		for (String firstlast : name) {
			String[] mfirstlast = firstlast.split(" ");
			if (mfirstlast.length > 1) {
				firstLastMap.put(mfirstlast[0], mfirstlast[1]);
			} else {
				throw new Exception("You have to enter first name and last name with comma delimeted.");
			}
		}
		return firstLastMap;
	}

	private String singleQuoteAndComma(String[] strArray) {
		String in = "";
		if (strArray == null || strArray.length == 0) {
			return in;
		}
		for (int i = 0; i < strArray.length; i++) {
			in += "'" + strArray[i] + "'";
			if (i != strArray.length - 1) {
				in += ",";
			}
		}
		return in;
	}

	// private void processSignup(HttpServletRequest request,
	// HttpServletResponse response, boolean isGet)
	// throws Exception {
	// if (isGet) {
	// renderTemplate(request, response, "register.ftl", null);
	// return;
	// }
	// String firstname = (String) request.getParameter("firstname");
	// String lastname = (String) request.getParameter("lastname");
	// String email = (String) request.getParameter("email");
	// String username = (String) request.getParameter("username");
	// String password = (String) request.getParameter("password");
	// DefaultObjectWrapperBuilder df = new
	// DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
	// SimpleHash root = new SimpleHash(df.build());
	// if (username == null || password == null || "".equals(username) ||
	// "".equals(password) || firstname == null
	// || lastname == null || "".equals(firstname) || "".equals(lastname) ||
	// email == null
	// || "".equals(email)) {
	// root.put("error", true);
	// root.put("message", "Please enter all required fields.");
	// renderTemplate(request, response, "register.ftl", root);
	// return;
	// }
	// Connection con = DatabaseAccess.connect();
	// if (con == null) {
	// root.put("error", true);
	// root.put("message", "Something went wrong while processing register.
	// Please try again.");
	// renderTemplate(request, response, "register.ftl", root);
	// return;
	// }
	// boolean exists = doesUserExists(con, username);
	// if (exists) {
	// root.put("error", true);
	// root.put("message", "Username " + username + " exists. Choose different
	// username.");
	// renderTemplate(request, response, "register.ftl", root);
	// DatabaseAccess.closeConnection(con);
	// return;
	// }
	// int id = getNextUserId(con);
	// int rows = DatabaseAccess.create(con, CREATE_USERD, String.valueOf(id),
	// firstname, lastname, email, username,
	// password);
	// if (rows == 0) {
	// root.put("error", true);
	// root.put("message", "Database error while executing insert statement.");
	// renderTemplate(request, response, "register.ftl", root);
	// DatabaseAccess.closeConnection(con);
	// return;
	// }
	// root.put("error", true);
	// root.put("message", "Your account is created. Please login.");
	// renderTemplate(request, response, "login.ftl", root);
	// DatabaseAccess.closeConnection(con);
	// return;
	//
	// }

	// private boolean doesUserExists(Connection con, String username) throws
	// SQLException {
	// ResultSet resultSet = DatabaseAccess.retrieve(con, FETCH_BY_USER_NAME,
	// username);
	// try {
	// if (resultSet == null || resultSet.getFetchSize() == 0) {
	// return false;
	// }
	// } catch (Exception e) {
	// return false;
	// }
	// return true;
	// }

	private void renderTemplate(HttpServletRequest request, HttpServletResponse response, String templateName,
			SimpleHash root) {
		try {
			Template template = null;
			template = cfg.getTemplate(templateName);
			response.setContentType("text/html");
			Writer out = response.getWriter();
			template.process(root, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

	}

}
