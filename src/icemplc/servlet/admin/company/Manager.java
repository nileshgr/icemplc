package icemplc.servlet.admin.company;

import icemplc.bean.Branch;
import icemplc.bean.Company;
import icemplc.bean.Student;
import icemplc.lib.AppException;
import icemplc.lib.Common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;

@WebServlet("/admin/company/*")
public class Manager extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/icemplcds")
    private DataSource ds;

    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String action = request.getRequestURI()
		.substring(request.getContextPath().length())
		.substring("/admin/company/".length());

	Company c = null;

	switch (action) {
	case "search":
	    request.setAttribute("companyset",
		    search(request.getParameter("name")));
	    
	    /* batch filtering for student list generation. Used to generate URLs in search.jsp */
	    
	    request.setAttribute("batch", request.getParameter("batch"));
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/company/search.jsp").forward(
		    request, response);
	    break;
	case "add":
	    request.setAttribute("branches", Branch.getBranches(ds));
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/company/new.jsp").forward(
		    request, response);
	    break;
	case "modify":
	    c = _loadCompany(request.getParameter("companyID"));
	    request.getSession(true).setAttribute("currentCompanyInDb", c);
	    request.setAttribute("company", c);
	    request.setAttribute("branches", Branch.getBranches(ds));
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/admin/company/modify.jsp").forward(
		    request, response);
	    break;
	case "elist":
	    elist(request, response,
		    _loadCompany(request.getParameter("companyID")));
	    break;
	case "elist_dl":
	    elist_dl(request, response,
		    _loadCompany(request.getParameter("companyID")));
	    break;
	case "plist":
	    plist(request, response,
		    _loadCompany(request.getParameter("companyID")));
	    break;
	case "plist_dl":
	    plist_dl(request, response,
		    _loadCompany(request.getParameter("companyID")));
	    break;
	}
    }

    private Company _loadCompany(String ID) throws AppException {
	Company c = Company.findByID(ID, ds);
	if (c == null)
	    throw new AppException("Company Not Found");
	return c;
    }

    private void plist(HttpServletRequest request,
	    HttpServletResponse response, Company c) throws ServletException,
	    IOException {
	Set<Student> ret = new LinkedHashSet<>();
	int batch = -1;
	String sql = "SELECT id, firstname, middlename, lastname FROM student, placed where cid = ? and id = sid";

	String _batch = request.getParameter("batch");
	CalendarValidator cv = new CalendarValidator();
	if (!cv.isValid(_batch, "yyyy"))
	    batch = -1;
	else
	    batch = Integer.parseInt(_batch);

	if (batch != -1)
	    sql = sql + " and batch = ?";

	try (Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {
	    ps.setInt(1, c.getID());

	    if (batch != -1)
		ps.setInt(2, batch);

	    ResultSet rs = ps.executeQuery();
	    Student s = null;

	    while (rs.next()) {
		s = new Student();
		s.setID(rs.getString(1));
		s.setFirstName(rs.getString(2));
		s.setMiddleName(rs.getString(3));
		s.setLastName(rs.getString(4));
		ret.add(s);
	    }
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}

	request.setAttribute("studentset", ret);
	request.setAttribute("company", c);
	request.setAttribute("batch", request.getParameter("batch"));
	request.getRequestDispatcher(
		"/WEB-INF/templates/admin/company/plist.jsp").forward(request,
		response);
    }

    private void plist_dl(HttpServletRequest request,
	    HttpServletResponse response, Company c) throws ServletException,
	    IOException {
	int batch = -1;
	String sql = "SELECT firstname, middlename, lastname FROM student, placed where cid = ? and id = sid";

	String _batch = request.getParameter("batch");
	CalendarValidator cv = new CalendarValidator();
	if (!cv.isValid(_batch, "yyyy"))
	    batch = -1;
	else
	    batch = Integer.parseInt(_batch);

	if (batch != -1)
	    sql = sql + " and batch = ?";

	try (SimpleResultSet csv_res = new SimpleResultSet()) {
	    csv_res.addColumn("", Types.VARCHAR, 60, 0);
	    csv_res.addRow("Students placed under " + c.getName() + " ("
		    + c.getBranch().getName() + ")");
	    csv_res.addRow("");

	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con.prepareStatement(sql)) {
		ps.setInt(1, c.getID());

		if (batch != -1)
		    ps.setInt(2, batch);

		ResultSet rs = ps.executeQuery();

		while (rs.next())
		    csv_res.addRow(rs.getString("firstname") + " "
			    + rs.getString("middlename") + " "
			    + rs.getString("lastname"));

	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	    response.setContentType("text/csv");
	    response.addHeader(
		    "Content-disposition",
		    "attachment; filename=\"Students placed under "
			    + c.getName() + " (" + c.getBranch().getName()
			    + ").csv\"");
	    new Csv().write(response.getWriter(), csv_res);
	} catch (SQLException e) {
	    throw new AppException("CSV Error", e);
	}

    }

    private void elist(HttpServletRequest request,
	    HttpServletResponse response, Company c) throws ServletException,
	    IOException {
	Set<Student> ret = new LinkedHashSet<>();
	int batch = -1;
	String sql = "SELECT s.id, firstname, middlename, lastname FROM student s, company c, branch b "
		+ "WHERE scoreavg >= criteria AND s.bid = c.bid AND s.bid = b.id AND s.id NOT in (SELECT sid from placed) "
		+ "AND c.id = ? AND active = true";

	String _batch = request.getParameter("batch");
	CalendarValidator cv = new CalendarValidator();
	if (!cv.isValid(_batch, "yyyy"))
	    batch = -1;
	else
	    batch = Integer.parseInt(_batch);

	if (batch != -1)
	    sql = sql + " and s.batch = ?";

	try (Connection con = ds.getConnection();
		PreparedStatement ps = con.prepareStatement(sql)) {
	    ps.setInt(1, c.getID());

	    if (batch != -1)
		ps.setInt(2, batch);

	    ResultSet rs = ps.executeQuery();
	    Student s = null;

	    while (rs.next()) {
		s = new Student();
		s.setID(rs.getString(1));
		s.setFirstName(rs.getString(2));
		s.setMiddleName(rs.getString(3));
		s.setLastName(rs.getString(4));
		ret.add(s);
	    }
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
	request.setAttribute("studentset", ret);
	request.setAttribute("company", c);	
	request.setAttribute("batch", request.getParameter("batch"));
	request.getRequestDispatcher(
		"/WEB-INF/templates/admin/company/elist.jsp").forward(request,
		response);
    }

    private void elist_dl(HttpServletRequest request,
	    HttpServletResponse response, Company c) throws ServletException,
	    IOException {
	int batch = -1;
	String sql = "SELECT firstname, middlename, lastname FROM student s, company c, branch b "
		+ "WHERE scoreavg >= criteria AND s.bid = c.bid AND s.bid = b.id AND s.id NOT in (SELECT sid from placed) "
		+ "AND c.id = ? AND active = true";

	String _batch = request.getParameter("batch");
	CalendarValidator cv = new CalendarValidator();
	if (!cv.isValid(_batch, "yyyy"))
	    batch = -1;
	else
	    batch = Integer.parseInt(_batch);

	if (batch != -1)
	    sql = sql + " and s.batch = ?";

	try (SimpleResultSet csv_res = new SimpleResultSet();) {
	    csv_res.addColumn("", Types.VARCHAR, 60, 0);
	    csv_res.addRow("Students eligible for " + c.getName() + " ("
		    + c.getBranch().getName() + ")");
	    csv_res.addRow("");

	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con.prepareStatement(sql)) {
		ps.setInt(1, c.getID());
		
		if (batch != -1)
		    ps.setInt(2, batch);
		
		ResultSet rs = ps.executeQuery();

		while (rs.next())
		    csv_res.addRow(rs.getString("firstname") + " "
			    + rs.getString("middlename") + " "
			    + rs.getString("lastname"));

	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	    response.setContentType("text/csv");
	    response.addHeader(
		    "Content-disposition",
		    "attachment; filename=\"Students Eligible for "
			    + c.getName() + " (" + c.getBranch().getName()
			    + ").csv\"");
	    new Csv().write(response.getWriter(), csv_res);
	} catch (SQLException e) {
	    throw new AppException("CSV Error", e);
	}

    }

    private LinkedHashSet<Company> search(String pattern) throws AppException {
	pattern = StringUtils.defaultString(StringUtils.lowerCase(StringUtils
		.trim(pattern)));
	LinkedHashSet<Company> ret = new LinkedHashSet<>();

	if (pattern.length() > 0) {
	    pattern = "%" + pattern + "%";

	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con
			    .prepareStatement("SELECT c.id, c.name, criteria, b.id, b.name FROM company c, branch b WHERE c.bid = b.id AND LCASE(c.name) LIKE ? LIMIT 15")) {
		ps.setString(1, pattern);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
		    Company c = new Company();
		    Branch b = new Branch();

		    c.setID(rs.getInt(1));
		    c.setName(rs.getString(2));
		    c.setCriteria(rs.getFloat(3));

		    b.setID(rs.getInt(4));
		    b.setName(rs.getString(5));

		    c.setBranch(b);

		    ret.add(c);
		}

	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }
	} else
	    try (Connection con = ds.getConnection();
		    Statement stmt = con.createStatement()) {
		ResultSet rs = stmt
			.executeQuery("SELECT c.id, c.name, criteria, b.id, b.name FROM company c, branch b WHERE c.bid = b.id LIMIT 15");

		while (rs.next()) {
		    Company c = new Company();
		    Branch b = new Branch();

		    c.setID(rs.getInt(1));
		    c.setName(rs.getString(2));
		    c.setCriteria(rs.getFloat(3));

		    b.setID(rs.getInt(4));
		    b.setName(rs.getString(5));

		    c.setBranch(b);

		    ret.add(c);
		}
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	return ret;
    }

    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	String action = request.getRequestURI()
		.substring(request.getContextPath().length())
		.substring("/admin/company/".length());

	Company c = new Company();
	c.setName(request.getParameter("name"));
	c.setCriteria(request.getParameter("criteria"));

	Branch b = new Branch();
	b.setID(request.getParameter("branch"));

	c.setBranch(b);

	if (action.equals("add")) {
	    LinkedHashMap<String, LinkedHashSet<String>> validity = c
		    .validate(ds);

	    if (validity.get("invalid").size() > 0) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("application/json");
		response.getWriter().println(
			Common.convertValidityMapToJson(validity));
		return;
	    } else
		try (Connection con = ds.getConnection();
			PreparedStatement ps = con
				.prepareStatement("INSERT INTO company (name, criteria, bid) VALUES (?, ?, ?)")) {
		    ps.setString(1, c.getName());
		    ps.setFloat(2, c.getCriteria());
		    ps.setInt(3, c.getBranch().getID());

		    if (ps.executeUpdate() != 1)
			throw new AppException("Database Error",
				new SQLException("Row count is not 1"));
		} catch (SQLException e) {
		    throw new AppException("Database Error", e);
		}

	} else if (action.equals("modify")) {

	    try {
		c.setID(Integer.parseInt(request.getParameter("companyID")));
	    } catch (Exception e) {
		throw new AppException(404, "Invalid company", e);
	    }

	    Company currentInDb = (Company) request.getSession(true)
		    .getAttribute("currentCompanyInDb");

	    if (currentInDb == null || currentInDb.getID() != c.getID())
		throw new AppException(404, "Invalid company");

	    LinkedHashMap<String, LinkedHashSet<String>> validity = c
		    .validate(ds);

	    if (validity.get("invalid").size() > 0) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("application/json");
		response.getWriter().println(
			Common.convertValidityMapToJson(validity));
		return;
	    } else {
		try (Connection con = ds.getConnection();
			PreparedStatement ps = con
				.prepareStatement("UPDATE company SET name = ?, criteria = ?, bid = ? WHERE id = ?")) {
		    ps.setString(1, c.getName());
		    ps.setFloat(2, c.getCriteria());
		    ps.setInt(3, c.getBranch().getID());
		    ps.setInt(4, c.getID());

		    if (ps.executeUpdate() != 1)
			throw new AppException("Database Error",
				new SQLException("Update count is not 1"));
		} catch (SQLException e) {
		    throw new AppException("Database Error", e);
		}

		request.getSession().removeAttribute("currentCompanyInDb");
	    }
	}

	response.setStatus(HttpServletResponse.SC_OK);
    }
}