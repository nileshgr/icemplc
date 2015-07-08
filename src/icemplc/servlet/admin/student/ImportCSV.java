package icemplc.servlet.admin.student;

import icemplc.bean.Student;
import icemplc.lib.AppException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.validator.routines.FloatValidator;
import org.h2.tools.Csv;

@WebServlet("/admin/student/importcsv")
public class ImportCSV extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/icemplcds")
    private DataSource ds;

    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	boolean isMultiPart = ServletFileUpload.isMultipartContent(request);

	if (!isMultiPart) {
	    response.getWriter().println("Not a file");
	    return;
	}

	DiskFileItemFactory factory = new DiskFileItemFactory();
	File repository = new File(System.getProperty("java.io.tmpdir"));
	factory.setSizeThreshold(1 * 1024 * 1024);
	factory.setRepository(repository);

	ServletFileUpload upload = new ServletFileUpload(factory);
	upload.setFileSizeMax(10 * 1024 * 1024);

	List<FileItem> files;
	try {
	    files = upload.parseRequest(request);
	} catch (FileUploadException e) {
	    throw new AppException("File upload error", e);
	}

	String error = "";

	if (files.iterator().hasNext()) {
	    FileItem fi = files.iterator().next();
	    MagicMatch mm = null;
	    try {
		mm = Magic.getMagicMatch(fi.get());
	    } catch (MagicParseException | MagicMatchNotFoundException
		    | MagicException e) {
		throw new AppException("Mime determiniation error", e);
	    }

	    if (mm.getMimeType().equals("text/plain")
		    || mm.getMimeType().equals("text/csv")) {
		parseCSV(request, response, fi);
		return;
	    } else
		error = "Invalid file. Only CSV accepted.";

	} else {
	    error = "No file uploaded";
	}

	JsonObjectBuilder job = Json.createObjectBuilder();
	job.add("error", error);
	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	response.setContentType("application/json");
	response.getWriter().print(job.build());
    }

    void parseCSV(HttpServletRequest request, HttpServletResponse response,
	    FileItem fi) throws ServletException, IOException {
	String fields[] = { "ID", "Sem 7", "Sem 8" };
	ResultSet csvrs;
	int sem7idx, sem8idx;
	Set<String> updateStatus = null;

	JsonObjectBuilder job = Json.createObjectBuilder();

	try (InputStreamReader isr = new InputStreamReader(fi.getInputStream())) {
	    try {
		csvrs = new Csv().read(isr, fields);
		csvrs.next(); // skip first row which is just field names
	    } catch (Exception e) {
		throw new AppException("Error parsing CSV file", e);
	    }

	    try {
		csvrs.findColumn("ID");
	    } catch (SQLException e) {
		throw new AppException("Column ID not found");
	    }

	    try {
		sem7idx = csvrs.findColumn("Sem 7");
	    } catch (SQLException e) {
		sem7idx = -1;
	    }

	    try {
		sem8idx = csvrs.findColumn("Sem 8");
	    } catch (SQLException e) {
		sem8idx = -1;
	    }

	    // both cols absent
	    if (sem7idx == -1 && sem8idx == -1)
		throw new AppException("No columns found for score updates");

	    // both cols present
	    else if (sem7idx != -1 && sem8idx != -1)
		updateStatus = update2scores(csvrs, sem7idx, sem8idx);

	    // sem7 present, sem8 absent
	    else if (sem7idx != -1 && sem8idx == -1)
		updateStatus = updateS7score(csvrs, sem7idx);

	    // sem7 absent, sem8 present
	    else if (sem7idx == -1 && sem8idx != -1)
		updateStatus = updateS8score(csvrs, sem8idx);

	    JsonArrayBuilder jab = Json.createArrayBuilder();

	    if (updateStatus.size() > 0) {
		for (String k : updateStatus)
		    jab.add(k);
		job.add("failedUpdates", jab);
	    }
	} catch (AppException e) {
	    job.add("error", e.getMessage());
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	response.setContentType("application/json");
	response.getWriter().print(job.build());
    }

    Set<String> update2scores(ResultSet csvrs, int sem7idx, int sem8idx)
	    throws AppException {
	Set<String> failedUpdates = new LinkedHashSet<>();
	String ID = "";
	FloatValidator fv = new FloatValidator();

	try {
	    while (csvrs.next()) {
		try {
		    ID = csvrs.getString(1);
		    Student s = Student.findStudentByID(ds, ID);

		    if (s == null)
			throw new ServletException();

		    float newScore[] = new float[8];

		    System.arraycopy(s.getScore(), 0, newScore, 0, 6);

		    newScore[6] = csvrs.getFloat(sem7idx);
		    newScore[7] = csvrs.getFloat(sem8idx);

		    if (!fv.isInRange(newScore[6], 40, 100)
			    || !fv.isInRange(newScore[7], 40, 100))
			throw new ServletException();

		    s.setScore(newScore);
		    Student.modifyStudent(ds, s, ID);
		} catch (ServletException e) {
		    failedUpdates.add(ID);
		}
	    }
	} catch (SQLException e) {
	    throw new AppException(e);
	}
	return failedUpdates;
    }

    Set<String> updateS7score(ResultSet csvrs, int sem7idx) throws AppException {
	Set<String> failedUpdates = new LinkedHashSet<>();
	String ID = "";
	FloatValidator fv = new FloatValidator();

	try {
	    while (csvrs.next()) {
		try {
		    ID = csvrs.getString(1);
		    Student s = Student.findStudentByID(ds, ID);
		    if (s == null)
			throw new ServletException();

		    float newScore[] = new float[7];
		    System.arraycopy(s.getScore(), 0, newScore, 0, 6);
		    newScore[6] = csvrs.getFloat(sem7idx);

		    if (!fv.isInRange(newScore[6], 40, 100))
			throw new ServletException();

		    s.setScore(newScore);
		    Student.modifyStudent(ds, s, ID);
		} catch (ServletException e) {
		    failedUpdates.add(ID);
		}
	    }
	} catch (SQLException e) {
	    throw new AppException(e);
	}
	return failedUpdates;
    }

    Set<String> updateS8score(ResultSet csvrs, int sem8idx) throws AppException {
	Set<String> failedUpdates = new LinkedHashSet<>();
	String ID = "";
	FloatValidator fv = new FloatValidator();

	try {
	    while (csvrs.next()) {
		try {

		    ID = csvrs.getString(1);
		    Student s = Student.findStudentByID(ds, ID);
		    if (s == null)
			throw new ServletException();

		    float score[] = s.getScore();

		    if (score.length < 7)
			throw new ServletException();

		    float newScore[] = new float[8];
		    System.arraycopy(score, 0, newScore, 0, 7);
		    newScore[7] = csvrs.getFloat(sem8idx);

		    if (!fv.isInRange(newScore[7], 40, 100))
			throw new ServletException();

		    s.setScore(newScore);
		    Student.modifyStudent(ds, s, ID);
		} catch (ServletException e) {
		    failedUpdates.add(ID);
		}
	    }
	} catch (SQLException e) {
	    throw new AppException(e);
	}
	return failedUpdates;
    }
}