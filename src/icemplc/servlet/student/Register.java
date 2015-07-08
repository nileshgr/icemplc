package icemplc.servlet.student;

import icemplc.bean.Branch;
import icemplc.bean.Student;
import icemplc.lib.Common;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/student/register")
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/icemplcds")
    private DataSource ds;

    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	request.setAttribute("pageName", "Register");
	request.setAttribute("batches", Common.generateBatches());
	request.setAttribute("branches", Branch.getBranches(ds));		
	request.getRequestDispatcher(
		"/WEB-INF/templates/student/register/form.jsp").forward(
		request, response);
    }

    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
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
	
	branch.setID(request.getParameter("branch"));
	student.setBranch(branch);
	
	request.setAttribute("student", student);

	LinkedHashMap<String, LinkedHashSet<String>> validity = Student
		.validateStudentUnique(ds, student);

	if (validity.get("invalid").size() > 0) {
	    request.setAttribute("reg_error", true);
	    request.setAttribute("validity", validity);
	    doGet(request, response);
	} else {
	    Student.createStudent(ds, student);
	    request.getRequestDispatcher(
		    "/WEB-INF/templates/student/register/success.jsp").forward(
		    request, response);
	}
    }
}