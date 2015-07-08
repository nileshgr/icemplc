package icemplc.bean;

import icemplc.lib.AppException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

public class Branch implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ID;
    private String name;

    public int getID() {
	return ID;
    }

    public void setID(int iD) {
	ID = iD;
    }

    public void setID(String iD) {
	try {
	ID = Integer.parseInt(StringUtils.defaultString(iD, "0"));
	} catch(NumberFormatException e) {
	    ID = 0;
	}
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = StringUtils.defaultString(StringUtils.trim(name));
    }

    public boolean isNameValid(DataSource ds) throws AppException {
	if (name.length() > 0)
	    try (Connection con = ds.getConnection();
		    PreparedStatement ps = con
			    .prepareStatement("SELECT id FROM branch WHERE name = ?")) {
		ps.setString(1, name);
		if (ps.executeQuery().next())
		    return false;
		else
		    return true;
	    } catch (SQLException e) {
		throw new AppException("Database Error", e);
	    }
	else
	    return false;
    }

    public boolean isIDValid(DataSource ds) throws AppException {
	try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT id FROM branch WHERE id = ?")) {
	    ps.setInt(1, ID);
	    if(ps.executeQuery().next())
		return true;
	    else
		return false;
	} catch(SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }

    public static Set<Branch> getBranches(DataSource ds)
	    throws AppException {
	Set<Branch> ret = new LinkedHashSet<>();
	try (Connection con = ds.getConnection();
		Statement stmt = con.createStatement()) {
	    ResultSet rs = stmt.executeQuery("SELECT * from branch");
	    while (rs.next()) {
		Branch b = new Branch();
		b.setID(rs.getInt("ID"));
		b.setName(rs.getString("name"));
		ret.add(b);
	    }
	    return ret;
	} catch (SQLException e) {
	    throw new AppException("Database Error", e);
	}
    }
}