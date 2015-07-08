package icemplc.servlet.student;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * TODO -- student login using password in database
 */

@WebServlet(urlPatterns = { 
				"/student/login", 
				"/student/logout"
		})
public class Auth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String uri = request.getRequestURI().substring(request.getContextPath().length());
		HttpSession session = request.getSession();
		
		if(uri.equals("/student/login")) {
			if(session.getAttribute("logged_in") != null)
				response.sendRedirect(request.getContextPath() + "/student/admin");
			else {
				request.setAttribute("pageName", "Login");
				request.getRequestDispatcher("/WEB-INF/templates/student/login.jsp").forward(request, response);
			}
		}		
		else if(uri.equals("/student/logout")) {
			request.getSession().removeAttribute("logged_in");
			response.sendRedirect(request.getContextPath() + "/");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession();
		session.removeAttribute("logged_in");
		request.removeAttribute("login_error");
		
		/* FIXME ADS AUTH */
		
		if(username == null || password == null) {
			request.setAttribute("login_error", true);
			request.setAttribute("errorMsg", "Empty username or password");
			doGet(request, response);
			return;
		}
		
		if (username.equals("admin") == false || password.equals("admin") == false) {
			request.setAttribute("login_error", true);
			request.setAttribute("errorMsg", "Invalid username or password");			
			doGet(request, response);			
		}
		else {
			session.setAttribute("logged_in", true);
			response.sendRedirect(request.getContextPath() + "/dashboard");
		}
	}
}
