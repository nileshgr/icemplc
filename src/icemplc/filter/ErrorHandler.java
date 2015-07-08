package icemplc.filter;

import icemplc.lib.AppException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/*
 * A generic error handler.
 * It logs the error message and stack trace in context log in case of 
 * internal server errors
 * 
 * Loads the errorcode.jsp in templates/error for all
 * 
 * This filter is invoked only for regular requests and not for server-level errors.
 */

@WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, urlPatterns = { "/*" })
public class ErrorHandler implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		int errorcode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		boolean error = false;
		String message = "";

		StringWriter sw_stacktrace = new StringWriter();
		PrintWriter pw_stacktrace = new PrintWriter(sw_stacktrace);

		try {
			chain.doFilter(request, response);
		} catch (AppException e) {
			errorcode = e.getCode();
			error = true;
			message = e.getMessage();
			e.printStackTrace(pw_stacktrace);
		} catch (Throwable t) {
			message = "Unknown Error";
			error = true;
			t.printStackTrace(pw_stacktrace);
		}

		if (error && !response.isCommitted()) {
			if (errorcode == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
				String log = String.format("Error %d Occurred - \n %s \n Stack Trace - \n %s",
						errorcode, message, sw_stacktrace.toString());
				request.getServletContext().log(log);
			}

			HttpServletResponse resp = (HttpServletResponse) response;
			resp.setStatus(errorcode);
			request.setAttribute("errorMessage", message);
			request.getRequestDispatcher("/WEB-INF/templates/error/" + errorcode + ".jsp").forward(
					request, response);
		}

		pw_stacktrace.close();
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}
}