package icemplc.lib;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/*
 * Custom exception type containing status code
 * extends ServletException because methods inside
 * servlet must throw ServletException or subclass of it.
 */

public class AppException extends ServletException {	
	
	private static final long serialVersionUID = 1L;
	
	protected int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	public AppException(String message) {
		super(message);		
	}

	public AppException(Throwable rootCause) {
		super(rootCause);		
	}

	public AppException(String message, Throwable rootCause) {
		super(message, rootCause);		
	}	

	public AppException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public AppException(int code, String message, Throwable rootCause) {
		super(message, rootCause);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}	
}