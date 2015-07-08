package icemplc.servlet.admin.student;

import icemplc.bean.Branch;
import icemplc.bean.Company;
import icemplc.bean.Student;
import icemplc.lib.AppException;
import icemplc.lib.Common;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.commons.validator.routines.FloatValidator;
import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;
import org.javatuples.Triplet;

/*
 * Student manager for admin
 */

@WebServlet("/admin/student/*")
public class Manager extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/icemplcds")
    private DataSource ds;

    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String action = request.getRequestURI()
		.substring(request.getContextPath().length())
		.substring("/admin/student/".length());

	/*
	 * We need session for passing success/failure message to administration
	 * interface because it happens over redirects and not forwards
	 */

	HttpSession session = request.getSession(true);
	Student s = null;
	Branch b = null;
	Company c = null;

	switch (action) {
	case "add":
	    request.setAttribute("batches", Common.generateBatches());
	    request.setAttribute("branches", Branch.getBranches(ds));
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/student/new.jsp").forward(
		    request, response);
	    break;
	case "modify":
	    s = Student.findStudentByID(ds, request.getParameter("studentID"));
	    if (s == null)
		throw new AppException(HttpServletResponse.SC_NOT_FOUND,
			"Student was not found");
	    else {

		/*
		 * Our form allows modification of PRN, so we need to
		 * store current student object which will be used while
		 * validating changed-submitted data.
		 */
		session.setAttribute("currentStudentInDb", s);

		request.setAttribute("student", s);
		request.setAttribute("batches", Common.generateBatches());
		request.setAttribute("branches", Branch.getBranches(ds));
		request.getRequestDispatcher(
			"/WEB-INF/templates/admin/student/modify.jsp").forward(
			request, response);
	    }
	    break;
	case "get":
	    s = get(request.getParameter("studentID"));
	    if (s == null)
		throw new AppException(HttpServletResponse.SC_NOT_FOUND,
			"Student was not found");
	    else {
		request.setAttribute("student", s);
		request.getRequestDispatcher(
			"/WEB-INF/templates/admin/student/get.jsp").forward(
			request, response);
	    }
	    break;
	case "search":
	    b = new Branch();
	    b.setID(request.getParameter("branch"));
	    Triplet<LinkedHashSet<Student>, Integer, Integer> searchresult = search(
		    request.getParameter("name_id"),
		    request.getParameter("minCriteria"),
		    request.getParameter("maxCriteria"), b,
		    request.getParameter("page"), request.getParameter("batch"));
	    request.setAttribute("studentset", searchresult.getValue0());
	    request.setAttribute("currentPage", searchresult.getValue1());
	    request.setAttribute("totalPages", searchresult.getValue2());

	    /*
	     * Attributes needed for generating CSV URL. See search.jsp
	     */

	    request.setAttribute("name_id", request.getParameter("name_id"));
	    request.setAttribute("minCriteria",
		    request.getParameter("minCriteria"));
	    request.setAttribute("maxCriteria",
		    request.getParameter("maxCriteria"));
	    request.setAttribute("branch", request.getParameter("branch"));
	    request.setAttribute("batch", request.getParameter("batch"));

	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/student/search.jsp").forward(
		    request, response);
	    break;
	case "pendingsearch":
	    request.setAttribute("studentset",
		    pendingsearch(request.getParameter("pattern")));
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/student/pendingsearch.jsp")
		    .forward(request, response);
	    break;
	case "activate":
	    activate(request.getParameter("studentID"));
	    break;
	case "get/data.csv":
	    b = new Branch();
	    b.setID(request.getParameter("branch"));
	    response.setContentType("application/octet-stream");
	    getcsv(response.getWriter(), request.getParameter("name_id"),
		    request.getParameter("minCriteria"),
		    request.getParameter("maxCriteria"), b,
		    request.getParameter("batch"));
	    break;

	case "place":
	    s = Student.findStudentByID(ds, request.getParameter("studentID"));
	    c = Company.findByID(request.getParameter("companyID"), ds);
	    if (s == null)
		throw new AppException(404, "Student not found");
	    if (c == null)
		throw new AppException(404, "Company not found");
	    place(c, s);
	    break;
	
	case "deplace":
	    s = Student.findStudentByID(ds, request.getParameter("studentID"));
	    if(s==null)
		throw new AppException(404, "Student not found");
	    deplace(s);
	    break;
	}
    }

    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String action = request.getRequestURI()
		.substring(request.getContextPath().length())
		.substring("/admin/student/".length());

	Student student = new Student();
	Branch branch = new Branch();

	student.setID(request.getParameter("ID"));
	student.setFirstName(request.getParameter("firstName"));
	student.setMiddleName(request.getParameter("middleName"));
	student.setLastName(request.getParameter("lastName"));
	student.setEmail(request.getParameter("email"));
	student.setBatch(request.getParameter("batch"));
	student.setPmobile(request.getParameter("pmobile"));
	student.setSmobile(request.getParameter("smobile"));
	student.setScore(request.getParameterValues("score"));
	student.setAdditional_qualifications(request
		.getParameter("additional_qualifications"));
	student.setActive(true);

	branch.setID(request.getParameter("branch"));
	student.setBranch(branch);

	LinkedHashMap<String, LinkedHashSet<String>> validity = Student
		.validateStudentUnique(ds, student);

	HttpSession session = request.getSession();
	Student currentInDb = null;

	if (action.equals("modify")) {
	    currentInDb = (Student) session.getAttribute("currentStudentInDb");

	    /*
	     * If the submitted form has the same ID and email as the one stored
	     * currently in db mark the fields as valid, because
	     * Common.validateStudentUnique() will mark them invalid.
	     */

	    if (student.getID().equals(currentInDb.getID())) {
		validity.get("invalid").remove("ID");
		validity.get("valid").add("ID");
	    }
	    if (student.getEmail().equals(currentInDb.getEmail())) {
		validity.get("invalid").remove("email");
		validity.get("valid").add("email");
	    }
	}

	if (validity.get("invalid").size() > 0) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().print(
		    Common.convertValidityMapToJson(validity));
	    return;
	} else {
	    switch (action) {
	    case "add":
		Student.createStudent(ds, student);
		response.setStatus(HttpServletResponse.SC_OK);
		break;
	    case "modify":
		Student.modifyStudent(ds, student, currentInDb.getID());
		response.setStatus(HttpServletResponse.SC_OK);
		session.removeAttribute("currentStudentInDb");
		break;
	    }
	}
    }

    private void place(Company c, Student s) throws AppException {
	try (Connection con = ds.getConnection();
		PreparedStatement ps = con
			.prepareStatement("INSERT INTO placed VALUES (?, ?)")) {
	    ps.setInt(1, c.getID());

	    ps.setString(2, s.getID());
	    if (ps.executeUpdate() != 1) {
		throw new SQLException("Updated row count is not 1");
	    }
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }
    
    private void deplace(Student s) throws AppException {
	try(Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement("DELETE FROM placed where sid = ?")) {
	    ps.setString(1, s.getID());
	    ps.execute();
	} catch(SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }

    private Student get(String ID) throws ServletException {
	return Student.findStudentByID(ds, ID);
    }

    /*
     * Search student by pattern, but inactive only And return latest 15 rows if
     * pattern is empty
     */

    private LinkedHashSet<Student> pendingsearch(String pattern)
	    throws ServletException {
	pattern = StringUtils.defaultString(StringUtils.lowerCase(StringUtils
		.trim(pattern)));
	LinkedHashSet<Student> ret = new LinkedHashSet<Student>();

	if (pattern.length() == 0) {
	    try (Connection con = ds.getConnection();
		    Statement stmt = con.createStatement(
			    ResultSet.TYPE_SCROLL_INSENSITIVE,
			    ResultSet.CONCUR_READ_ONLY);) {
		ResultSet rs = stmt
			.executeQuery("SELECT s.id, firstname, middlename, lastname, b.name from student s, branch b where active = false and b.id = s.bid limit 15");
		rs.afterLast();
		while (rs.previous()) {
		    int i = 1;

		    Student s = new Student();
		    s.setID(rs.getString(i++));
		    s.setFirstName(rs.getString(i++));
		    s.setMiddleName(rs.getString(i++));
		    s.setLastName(rs.getString(i++));

		    Branch b = new Branch();
		    b.setName(rs.getString(i++));

		    s.setBranch(b);

		    ret.add(s);
		}
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }
	} else {
	    pattern = "%" + pattern + "%";
	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con
			    .prepareStatement("SELECT id, firstname, middlename, lastname FROM student WHERE (lower(id) LIKE ? OR lower(firstname) LIKE ? OR lower(middlename) LIKE ? OR lower(lastname) LIKE ?) AND active = false limit 15")) {
		ps.setString(1, pattern);
		ps.setString(2, pattern);
		ps.setString(3, pattern);
		ps.setString(4, pattern);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
		    int i = 1;

		    Student s = new Student();
		    s.setID(rs.getString(i++));
		    s.setFirstName(rs.getString(i++));
		    s.setMiddleName(rs.getString(i++));
		    s.setLastName(rs.getString(i++));

		    ret.add(s);
		}
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }
	}

	return ret;
    }

    private void activate(String ID) throws ServletException {

	ID = StringUtils.defaultString(StringUtils.trim(ID));
	if (ID.length() == 0)
	    throw new AppException(404, "ID is a mandatory parameter");
	try (Connection con = ds.getConnection()) {
	    con.setAutoCommit(false);
	    try (PreparedStatement ps = con
		    .prepareStatement("UPDATE student set active = true WHERE id = ?")) {
		ps.setString(1, ID);
		if (ps.executeUpdate() != 1) {
		    con.rollback();
		    throw new AppException("Database Error", new SQLException(
			    "Update Count is not 1"));
		}
		con.commit();
	    }
	    con.setAutoCommit(true);
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }

    /*
     * Search database depending on conditions Returns the result set, current
     * page number and total pages See helper functions (overloaded) _search
     */

    private Triplet<LinkedHashSet<Student>, Integer, Integer> search(
	    String name_id, String minCriteria, String maxCriteria,
	    Branch branch, String page, String batch) throws ServletException {

	name_id = StringUtils.defaultString(StringUtils.lowerCase(StringUtils
		.trim(name_id)));

	int pageNum = 0, totalPages = 0, iBatch = 0;

	float fMinCriteria, fMaxCriteria;
	FloatValidator fv = new FloatValidator();

	try {
	    fMinCriteria = Float.parseFloat(StringUtils
		    .defaultString(minCriteria));
	    if (!fv.isInRange(fMinCriteria, 40, 100))
		fMinCriteria = 40;
	} catch (NumberFormatException e) {
	    fMinCriteria = 40;
	}

	try {
	    fMaxCriteria = Float.parseFloat(StringUtils
		    .defaultString(maxCriteria));
	    if (!fv.isInRange(fMaxCriteria, 40, 100))
		fMaxCriteria = 100;
	} catch (NumberFormatException e) {
	    fMaxCriteria = 100;
	}

	try {
	    CalendarValidator cv = CalendarValidator.getInstance();
	    if (cv.isValid(batch, "yyyy"))
		iBatch = Integer.parseInt(StringUtils.defaultString(batch));
	} catch (NumberFormatException e) {
	    iBatch = 0;
	}

	LinkedHashSet<Student> ret;

	name_id = "%" + name_id + "%";

	if (branch.isIDValid(ds)) {

	    /*
	     * Page counter
	     */

	    String sql = "SELECT CEIL(COUNT(s.id)/15.0) FROM student s, branch b "
		    + "WHERE (lower(s.id) LIKE ? OR lower(firstname) LIKE ? OR lower(middlename) LIKE ? "
		    + "OR lower(lastname) LIKE ?) AND b.id = s.bid AND b.id = ? AND scoreavg BETWEEN ? AND ?";

	    if (iBatch != 0)
		sql = sql + " AND batch = ?";

	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con.prepareStatement(sql)) {
		int i = 1;

		ps.setString(i++, name_id);
		ps.setString(i++, name_id);
		ps.setString(i++, name_id);
		ps.setString(i++, name_id);

		ps.setInt(i++, branch.getID());
		ps.setFloat(i++, fMinCriteria);
		ps.setFloat(i++, fMaxCriteria);

		if (iBatch != 0)
		    ps.setInt(i++, iBatch);

		ResultSet rs = ps.executeQuery();

		if (rs.next())
		    totalPages = rs.getInt(1);
		else
		    throw new SQLException("Could not fetch result");
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	    try {
		pageNum = Integer.parseInt(StringUtils.defaultString(page)) - 1;
		if (pageNum < 0)
		    pageNum = 0;
		else if (pageNum > (totalPages - 1))
		    pageNum = totalPages - 1;
	    } catch (NumberFormatException e) {
		pageNum = 0;
	    }

	    /*
	     * End page counter
	     */

	    ret = _search(branch, name_id, fMinCriteria, fMaxCriteria, iBatch,
		    pageNum, null);
	}

	else {
	    /*
	     * Page counter
	     */

	    String sql = "SELECT CEIL(COUNT(s.id)/15.0) FROM student s, branch b "
		    + "WHERE (lower(s.id) LIKE ? OR lower(firstname) LIKE ? OR lower(middlename) LIKE ? "
		    + "OR lower(lastname) LIKE ?) AND b.id = s.bid AND scoreavg BETWEEN ? AND ?";

	    if (iBatch != 0)
		sql = sql + " AND batch = ?";

	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con.prepareStatement(sql)) {
		int i = 1;

		ps.setString(i++, name_id);
		ps.setString(i++, name_id);
		ps.setString(i++, name_id);
		ps.setString(i++, name_id);

		ps.setFloat(i++, fMinCriteria);
		ps.setFloat(i++, fMaxCriteria);

		if (iBatch != 0)
		    ps.setInt(i++, iBatch);

		ResultSet rs = ps.executeQuery();

		if (rs.next())
		    totalPages = rs.getInt(1);
		else
		    throw new SQLException("Could not fetch result");
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	    try {
		pageNum = Integer.parseInt(StringUtils.defaultString(page)) - 1;
		if (pageNum < 0)
		    pageNum = 0;
		else if (pageNum > (totalPages - 1))
		    pageNum = totalPages - 1;
	    } catch (NumberFormatException e) {
		pageNum = 0;
	    }

	    /*
	     * End page counter
	     */

	    ret = _search(name_id, fMinCriteria, fMaxCriteria, iBatch, pageNum,
		    null);
	}

	return Triplet.with(ret, pageNum, totalPages);
    }

    /*
     * CSV generator writes csv to writer uses _search helper function
     */

    private void getcsv(Writer writer, String name_id, String minCriteria,
	    String maxCriteria, Branch branch, String batch)
	    throws ServletException {

	name_id = StringUtils.defaultString(StringUtils.lowerCase(StringUtils
		.trim(name_id)));

	int iBatch = 0;

	float fMinCriteria, fMaxCriteria;
	FloatValidator fv = new FloatValidator();

	try {
	    fMinCriteria = Float.parseFloat(StringUtils
		    .defaultString(minCriteria));
	    if (!fv.isInRange(fMinCriteria, 40, 100))
		fMinCriteria = 40;
	} catch (NumberFormatException e) {
	    fMinCriteria = 40;
	}

	try {
	    fMaxCriteria = Float.parseFloat(StringUtils
		    .defaultString(maxCriteria));
	    if (!fv.isInRange(fMaxCriteria, 40, 100))
		fMaxCriteria = 100;
	} catch (NumberFormatException e) {
	    fMaxCriteria = 100;
	}

	try {
	    CalendarValidator cv = CalendarValidator.getInstance();
	    if (cv.isValid(batch, "yyyy"))
		iBatch = Integer.parseInt(StringUtils.defaultString(batch));
	} catch (NumberFormatException e) {
	    iBatch = 0;
	}

	name_id = "%" + name_id + "%";

	try (SimpleResultSet csvrs = new SimpleResultSet()) {
	    csvrs.addColumn("ID", Types.VARCHAR, 20, 0);
	    csvrs.addColumn("Name", Types.VARCHAR, 60, 0);
	    csvrs.addColumn("Branch", Types.VARCHAR, 50, 0);
	    csvrs.addColumn("Score Average", Types.FLOAT, 5, 2);
	    csvrs.addColumn("Active", Types.VARCHAR, 5, 0);
	    csvrs.addRow("", "", "", "");

	    if (branch.isIDValid(ds))
		_search(branch, name_id, fMinCriteria, fMaxCriteria, iBatch, 0,
			csvrs);
	    else
		_search(name_id, fMinCriteria, fMaxCriteria, iBatch, 0, csvrs);

	    new Csv().write(writer, csvrs);
	} catch (SQLException e) {
	    throw new AppException(e);
	}
    }

    /*
     * Searching when branch is valid
     */

    private LinkedHashSet<Student> _search(Branch branch, String name_id,
	    float fMinCriteria, float fMaxCriteria, int batch, int pageNum,
	    SimpleResultSet csvrs) throws AppException {
	String sql = "SELECT s.id, firstname, middlename, lastname, active, b.name branchname, scoreavg FROM student s, branch b "
		+ "WHERE (lower(s.id) LIKE ? OR lower(firstname) LIKE ? OR lower(middlename) LIKE ? "
		+ "OR lower(lastname) LIKE ?) AND b.id = s.bid AND b.id = ? AND scoreavg BETWEEN ? AND ?";

	LinkedHashSet<Student> ret = null;

	if (batch != 0)
	    sql = sql + " AND batch = ?";

	/*
	 * if csvrs is not given, then we must paginate and also allocate memory
	 * for the set
	 */

	if (csvrs == null) {
	    sql = sql + " LIMIT 15 OFFSET ?";
	    ret = new LinkedHashSet<>();
	}

	try (Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {
	    int i = 1;
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);

	    ps.setInt(i++, branch.getID());
	    ps.setFloat(i++, fMinCriteria);
	    ps.setFloat(i++, fMaxCriteria);

	    if (batch != 0)
		ps.setInt(i++, batch);

	    if (csvrs == null)
		ps.setInt(i++, pageNum * 15);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
		if (csvrs == null) {
		    i = 1;
		    Student s = new Student();
		    s.setID(rs.getString(i++));
		    s.setFirstName(rs.getString(i++));
		    s.setMiddleName(rs.getString(i++));
		    s.setLastName(rs.getString(i++));
		    s.setActive(rs.getBoolean(i++));

		    Branch b = new Branch();
		    b.setName(rs.getString(i++));

		    s.setBranch(b);

		    s.setScoreavg(rs.getFloat(i++));
		    ret.add(s);
		} else {
		    csvrs.addRow(
			    rs.getString("ID"),
			    rs.getString("firstname") + " "
				    + rs.getString("middlename") + " "
				    + rs.getString("lastname"), rs
				    .getString("branchname"), rs
				    .getFloat("scoreavg"), rs
				    .getBoolean("active") ? "True" : "False");
		}
	    }

	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}

	return ret;
    }

    /*
     * Searching when branch is invalid
     */

    private LinkedHashSet<Student> _search(String name_id, float fMinCriteria,
	    float fMaxCriteria, int batch, int pageNum, SimpleResultSet csvrs)
	    throws AppException {
	String sql = "SELECT s.id, firstname, middlename, lastname, active, b.name branchname, scoreavg FROM student s, branch b "
		+ "WHERE (lower(s.id) LIKE ? OR lower(firstname) LIKE ? OR lower(middlename) LIKE ? "
		+ "OR lower(lastname) LIKE ?) AND b.id = s.bid AND scoreavg BETWEEN ? AND ?";

	LinkedHashSet<Student> ret = null;

	if (batch != 0)
	    sql = sql + " AND batch = ?";

	/*
	 * if csvrs is not given, then we must paginate and also allocate memory
	 * for the set
	 */

	if (csvrs == null) {
	    sql = sql + " LIMIT 15 OFFSET ?";
	    ret = new LinkedHashSet<>();
	}

	try (Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {
	    int i = 1;
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);
	    ps.setString(i++, name_id);

	    ps.setFloat(i++, fMinCriteria);
	    ps.setFloat(i++, fMaxCriteria);

	    if (batch != 0)
		ps.setInt(i++, batch);

	    if (csvrs == null)
		ps.setInt(i++, pageNum * 15);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next()) {
		if (csvrs == null) {
		    i = 1;
		    Student s = new Student();
		    s.setID(rs.getString(i++));
		    s.setFirstName(rs.getString(i++));
		    s.setMiddleName(rs.getString(i++));
		    s.setLastName(rs.getString(i++));
		    s.setActive(rs.getBoolean(i++));

		    Branch b = new Branch();
		    b.setName(rs.getString(i++));

		    s.setBranch(b);

		    s.setScoreavg(rs.getFloat(i++));
		    ret.add(s);
		} else {
		    csvrs.addRow(
			    rs.getString("ID"),
			    rs.getString("firstname") + " "
				    + rs.getString("middlename") + " "
				    + rs.getString("lastname"), rs
				    .getString("branchname"), rs
				    .getFloat("scoreavg"), rs
				    .getBoolean("active") ? "True" : "False");
		}
	    }

	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
	return ret;
    }

}