package icemplc.servlet.admin;

import icemplc.bean.AdminBatchStats;
import icemplc.bean.Branch;
import icemplc.lib.AppException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/*
 * Admin home page
 */

@WebServlet(urlPatterns={"/admin", "/admin/*"})
public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/icemplcds")
    private DataSource ds;

    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {	
	String batchstat = request.getParameter("batchstat");
	
	if(batchstat != null) {	
	    request.setAttribute("batchstat", getBatchStats());
	    request.getRequestDispatcher("/WEB-INF/templates/admin/batchstat.jsp").forward(request, response);
	    return;
	}
	
	request.setAttribute("branchset", Branch.getBranches(ds));

	Set<Integer> distinct_batch_set = new LinkedHashSet<>();

	try (Connection con = ds.getConnection();
		Statement stmt = con.createStatement()) {
	    ResultSet rs = stmt
		    .executeQuery("SELECT DISTINCT batch from student");
	    while (rs.next())
		distinct_batch_set.add(rs.getInt(1));
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}

	request.setAttribute("distinct_batch_set", distinct_batch_set);
	request.getRequestDispatcher("/WEB-INF/templates/admin/home.jsp")
		.forward(request, response);
    }

    /*
     * Generates batch wise statistics for last 5 batches
     */

    private LinkedHashSet<AdminBatchStats> getBatchStats()
	    throws ServletException {
	LinkedHashSet<AdminBatchStats> absset = new LinkedHashSet<AdminBatchStats>();
	try (Connection con = ds.getConnection();
		Statement stmt = con.createStatement();) {
	    ResultSet rs = stmt
		    .executeQuery("select batch, count(id) numstuds, count(sid) numplaced from student left outer join placed on sid=id group by batch order by batch desc limit 5");
	    while (rs.next()) {
		AdminBatchStats abs = new AdminBatchStats(rs.getInt(1),
			rs.getInt(2), rs.getInt(3));
		absset.add(abs);
	    }
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
	return absset;
    }
}