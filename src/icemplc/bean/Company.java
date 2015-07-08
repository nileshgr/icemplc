package icemplc.bean;

import icemplc.lib.AppException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ID;
    private String name;
    private float criteria;
    private Branch branch;

    public Company() {

    }

    public int getID() {
	return ID;
    }

    public String getName() {
	return name;
    }

    public float getCriteria() {
	return criteria;
    }

    public void setCriteria(String criteria) {
	try {
	    this.criteria = Float.parseFloat(StringUtils
		    .defaultString(criteria));
	} catch (NumberFormatException e) {
	    this.criteria = -1;
	}
    }

    public Branch getBranch() {
	return branch;
    }

    public void setBranch(Branch branch) {
	this.branch = branch;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setCriteria(float criteria) {
	this.criteria = criteria;
    }

    public void setID(int iD) {
	ID = iD;
    }

    public LinkedHashMap<String, LinkedHashSet<String>> validate(
	    DataSource ds) throws AppException {
	LinkedHashMap<String, LinkedHashSet<String>> ret = new LinkedHashMap<String, LinkedHashSet<String>>();
	LinkedHashSet<String> valid = new LinkedHashSet<String>();
	LinkedHashSet<String> invalid = new LinkedHashSet<String>();

	name = StringUtils.defaultString(StringUtils.capitalize(StringUtils
		.trim(name)));

	if (name.length() > 0)
	    valid.add("name");
	else
	    invalid.add("name");

	if (criteria == 0 || (criteria >= 40 && criteria <= 100))
	    valid.add("criteria");
	else
	    invalid.add("criteria");

	if (branch.isIDValid(ds))
	    valid.add("branch");
	else
	    invalid.add("branch");

	ret.put("valid", valid);
	ret.put("invalid", invalid);
	return ret;
    }

    public static Company findByID(String ID, DataSource ds)
	    throws AppException {	
	
	ID = StringUtils.defaultString(StringUtils.trim(ID));	
	
	int id;
	
	if(ID.length() == 0)
	    return null;
	
	try {
	    id = Integer.valueOf(ID);
	} catch(NumberFormatException e) {
	    return null;
	}
	
	try (Connection con = ds.getConnection();
		PreparedStatement ps = con
			.prepareStatement("SELECT c.id id, c.name name, c.criteria criteria, b.id bid, b.name bname from company c, branch b where c.id = ? and b.id = c.bid")) {

	    ps.setInt(1, id);

	    ResultSet rs = ps.executeQuery();

	    if (rs.next()) {
		Company c = new Company();
		Branch b = new Branch();

		b.setID(rs.getInt("bid"));
		b.setName(rs.getString("bname"));
		c.setBranch(b);

		c.setID(rs.getInt("id"));
		c.setName(rs.getString("name"));
		c.setCriteria(rs.getFloat("criteria"));

		return c;
	    } else
		return null;
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	} 
    }
}