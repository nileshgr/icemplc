package icemplc.bean;

import icemplc.lib.AppException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.lang3.StringUtils;

/*
 * Student bean
 */

public class Student implements Serializable {

    static final long serialVersionUID = 1L;

    private String ID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private boolean active = false;
    private String additional_qualifications;
    private String password;
    private int batch = 0;
    private String smobile;
    private String pmobile;
    private float score[];
    private float scoreavg = 0;
    private boolean score_avg_computed = false;
    private Branch branch;

    /*
     * Bean no-argument constructor
     */

    public Student() {
	score = new float[8];
    }

    public String getID() {
	return ID;
    }

    public boolean isActive() {
	return active;
    }

    public String getFirstName() {
	return firstName;
    }

    public String getMiddleName() {
	return middleName;
    }

    public String getLastName() {
	return lastName;
    }

    public String getEmail() {
	return email;
    }

    public String getAdditional_qualifications() {
	return additional_qualifications;
    }

    public String getPassword() {
	return password;
    }

    public int getBatch() {
	return batch;
    }

    public void setBatch(String batch) {
	try {
	    this.batch = Integer.valueOf(StringUtils.defaultString(batch));
	} catch (NumberFormatException e) {
	    this.batch = -1;
	}
    }

    public String getSmobile() {
	return smobile;
    }

    public String getPmobile() {
	return pmobile;
    }

    public float[] getScore() {
	return score;
    }

    public Object[] getObScore() {
	Float[] ret = new Float[score.length];
	for (int i = 0; i < score.length; i++)
	    ret[i] = score[i];
	return ret;
    }

    public void setScore(String[] score) {
	/*
	 * There will always 8 elements in the score array from
	 * request.getParameter But the user can leave out sem 7 & 8 scores
	 */

	float tmp[] = new float[8];

	for (int i = 0; i < 8; i++)
	    try {
		tmp[i] = Float.parseFloat(score[i]);
	    } catch (Exception e) {
		tmp[i] = -1;
	    }

	/*
	 * Semester 7 & 8 scores get converted to -1 as in the code above remove
	 * them from the array to aid proper score average calculation
	 */

	if (tmp[6] == -1 && tmp[7] == -1) {
	    float tmp1[] = new float[6];
	    System.arraycopy(tmp, 0, tmp1, 0, 6);
	    tmp = tmp1;
	} else if (tmp[7] == -1) {
	    float tmp1[] = new float[7];
	    System.arraycopy(tmp, 0, tmp1, 0, 7);
	    tmp = tmp1;
	}

	this.score = tmp;
	score_avg_computed = false;
    }

    public void setScore(Object[] score) {
	this.score = new float[score.length];
	for (int i = 0; i < score.length; i++)
	    this.score[i] = (Float) score[i];
	score_avg_computed = false;
    }

    public float getScoreavg() {
	if (!score_avg_computed) {
	    for (float f : score)
		scoreavg += f;
	    scoreavg /= score.length;
	    DecimalFormat df = new DecimalFormat("##.##");
	    scoreavg = Float.parseFloat(df.format(scoreavg));
	    score_avg_computed = true;
	}
	return scoreavg;
    }

    public void setScoreavg(float scoreavg) {
	this.scoreavg = scoreavg;
	score_avg_computed = true;
    }

    public void setID(String iD) {
	ID = iD;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
	this.middleName = middleName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public void setActive(boolean active) {
	this.active = active;
    }

    public void setAdditional_qualifications(String additional_qualifications) {
	this.additional_qualifications = additional_qualifications;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public void setBatch(int batch) {
	this.batch = batch;
    }

    public void setSmobile(String smobile) {
	this.smobile = smobile;
    }

    public void setPmobile(String pmobile) {
	this.pmobile = pmobile;
    }

    public void setScore(float[] score) {
	this.score = score;
	score_avg_computed = false;
    }

    public Branch getBranch() {
	return branch;
    }

    public void setBranch(Branch branch) {
	this.branch = branch;
    }

    /*
     * Data validation Method checks for all data submitted to the object.
     * Returns a Map with two elements - valid, invaild. Both are LinkedHashMap
     * <String> with respective field names.
     */

    public LinkedHashMap<String, LinkedHashSet<String>> validate() {
	LinkedHashSet<String> valid = new LinkedHashSet<String>();
	LinkedHashSet<String> invalid = new LinkedHashSet<String>();
	LinkedHashMap<String, LinkedHashSet<String>> ret = new LinkedHashMap<String, LinkedHashSet<String>>();

	ID = StringUtils.defaultString(StringUtils.upperCase(StringUtils
		.trim(ID)));

	if (ID.length() > 0)
	    valid.add("ID");
	else
	    invalid.add("ID");

	/*
	 * firstName and lastName are mandatory
	 */

	firstName = StringUtils.defaultString(StringUtils
		.capitalize(StringUtils.trim(firstName)));

	if (firstName.length() > 0)
	    valid.add("firstName");
	else
	    invalid.add("firstName");

	lastName = StringUtils.defaultString(StringUtils.capitalize(StringUtils
		.trim(lastName)));

	if (lastName.length() > 0)
	    valid.add("lastName");
	else
	    invalid.add("lastName");

	/*
	 * Middle Name is optional, so we just mark it as a valid if it has been
	 * filled in
	 */

	middleName = StringUtils.defaultString(StringUtils
		.capitalize(StringUtils.trim(middleName)));

	if (middleName.length() > 0)
	    valid.add("middleName");

	email = StringUtils.defaultString(StringUtils.lowerCase(StringUtils
		.trim(email)));
	EmailValidator ev = EmailValidator.getInstance();

	if (ev.isValid(email))
	    valid.add("email");
	else
	    invalid.add("email");

	/*
	 * Additional qualification is not mandatory
	 */

	additional_qualifications = StringUtils.defaultString(StringUtils
		.trim(additional_qualifications));

	if (additional_qualifications.length() > 0)
	    valid.add("addtional_qualifications");

	/*
	 * Validation will be used only when a new student registers Given
	 * current year x, only students from year x-1 and x can register in the
	 * portal
	 */

	int year = Calendar.getInstance().get(Calendar.YEAR);

	if (batch == year - 3 || batch == year - 4)
	    valid.add("batch");
	else
	    invalid.add("batch");

	/*
	 * Primary mobile is mandatory
	 */

	pmobile = StringUtils.defaultString(StringUtils.trim(pmobile));

	RegexValidator rv = new RegexValidator("\\d{10}");

	if (rv.isValid(pmobile))
	    valid.add("pmobile");
	else
	    invalid.add("pmobile");

	/*
	 * Secondary mobile is optional
	 */

	smobile = StringUtils.defaultString(StringUtils.trim(smobile));

	if (smobile.length() > 0)
	    if (rv.isValid(smobile) && !pmobile.equals(smobile))
		valid.add("smobile");
	    else
		invalid.add("smobile");

	if (score != null) {
	    for (int i = 0; i < score.length; i++)
		if (score[i] >= 40 && score[i] <= 100)
		    valid.add("sem" + (i + 1));
		else
		    invalid.add("sem" + (i + 1));
	} else
	    for (int i = 1; i < score.length; i++)
		invalid.add("sem" + i);

	ret.put("valid", valid);
	ret.put("invalid", invalid);
	return ret;
    }

    public static void createStudent(DataSource ds, Student student)
	    throws ServletException {
	try (Connection con = ds.getConnection()) {
	    con.setAutoCommit(false);

	    try (PreparedStatement ps = con
		    .prepareStatement("INSERT INTO student (id, firstname, middlename, lastname, email, batch, pmobile, smobile, score, scoreavg, active, bid) "
			    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
		int i = 1;
		ps.setString(i++, student.getID());
		ps.setString(i++, student.getFirstName());
		ps.setString(i++, student.getMiddleName());
		ps.setString(i++, student.getLastName());
		ps.setString(i++, student.getEmail());
		ps.setInt(i++, student.getBatch());
		ps.setString(i++, student.getPmobile());
		ps.setString(i++, student.getSmobile());
		ps.setObject(i++, student.getObScore());
		ps.setFloat(i++, student.getScoreavg());
		ps.setBoolean(i++, student.isActive());
		ps.setInt(i++, student.getBranch().getID());

		if (ps.executeUpdate() != 1) {
		    con.rollback();
		    throw new AppException("Database Error", new SQLException(
			    "Row count is not 1"));
		}

	    } catch (SQLException e) {
		throw e;
	    }

	    con.commit();
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }

    public static LinkedHashMap<String, LinkedHashSet<String>> validateStudentUnique(
	    DataSource ds, Student student) throws ServletException {
	LinkedHashMap<String, LinkedHashSet<String>> validity = student
		.validate();

	/*
	 * ID and email must be unique On submission check if the ID or email
	 * submitted already exists in database if so mark invalid
	 */

	if (validity.get("valid").contains("ID"))
	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con
			    .prepareStatement("SELECT id FROM student where id = ?")) {
		ps.setString(1, student.getID());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
		    validity.get("valid").remove("ID");
		    validity.get("invalid").add("ID");
		}
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	if (validity.get("valid").contains("email"))
	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con
			    .prepareStatement("SELECT email FROM student where email = ?")) {
		ps.setString(1, student.getEmail());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
		    validity.get("valid").remove("email");
		    validity.get("invalid").add("email");
		}
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }

	if (student.getBranch().isIDValid(ds))
	    validity.get("valid").add("branch");
	else
	    validity.get("invalid").add("branch");

	return validity;
    }

    public static void modifyStudent(DataSource ds, Student student,
	    String curstudentID) throws ServletException {
	try (Connection con = ds.getConnection()) {
	    con.setAutoCommit(false);
	    try (PreparedStatement ps = con
		    .prepareStatement("UPDATE student SET id = ?, firstname = ?, middlename = ?, lastname = ?, email = ?, batch = ?, pmobile = ?, smobile = ?, score = ?, scoreavg = ? WHERE id = ?")) {
		int i = 1;
		ps.setString(i++, student.getID());
		ps.setString(i++, student.getFirstName());
		ps.setString(i++, student.getMiddleName());
		ps.setString(i++, student.getLastName());
		ps.setString(i++, student.getEmail());
		ps.setInt(i++, student.getBatch());
		ps.setString(i++, student.getPmobile());
		ps.setString(i++, student.getSmobile());
		ps.setObject(i++, student.getObScore());
		ps.setFloat(i++, student.getScoreavg());
		ps.setString(i++, curstudentID);

		if (ps.executeUpdate() != 1) {
		    con.rollback();
		    throw new AppException("Database Error", new SQLException(
			    "Row count is not 1"));
		}
	    } catch (SQLException e) {
		con.rollback();
		throw e;
	    }
	    con.commit();
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}

    }

    public static Student findStudentByID(DataSource ds, String ID)
	    throws ServletException {
	ID = StringUtils.defaultString(StringUtils.trim(ID));
	if (ID.length() == 0)
	    return null;
	try (Connection con = ds.getConnection();
		PreparedStatement ps = con
			.prepareStatement("SELECT s.id, firstname, middlename, lastname, email, batch, pmobile, smobile, score, additional_qualifications, b.name FROM student s, branch b WHERE s.id = ? and b.id = s.bid")) {
	    ps.setString(1, ID);
	    ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		int i = 1;

		Student s = new Student();

		s.setID(rs.getString(i++));
		s.setFirstName(rs.getString(i++));
		s.setMiddleName(rs.getString(i++));
		s.setLastName(rs.getString(i++));
		s.setEmail(rs.getString(i++));
		s.setBatch(rs.getInt(i++));
		s.setPmobile(rs.getString(i++));
		s.setSmobile(rs.getString(i++));
		s.setScore((Object[]) rs.getArray(i++).getArray());
		s.setAdditional_qualifications(rs.getString(i++));

		Branch b = new Branch();
		b.setName(rs.getString(i++));

		s.setBranch(b);

		return s;
	    }

	    else
		return null;
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }
}