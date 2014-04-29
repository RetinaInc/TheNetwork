/*
 * Copyright (C) 2014 Frank Steiler <frank@steiler.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package activeRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class represents a row of the SysAdmin table providing functions to insert and update these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class SysAdminActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into SysAdmin(Email, Password, Salt, ConnectedUser)" +
            "Values (?, ?, ?, ?)";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific adminID.
     */
    private final String BY_ID =
            " Where AdminID=?";
    
     /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update SysAdmin" + 
            " Set Email = ?, Password = ?, Salt = ?, ConnectedUser = ?";
    
    private int adminID;
    private String eMail;
    private String password;
    private String salt;
    private int connectedUser;
    
    /**
     * Updates the row with the adminID of the object with the data within this object.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE + BY_ID);
            stmt.setString(1, eMail);
            stmt.setString(2, password);
            stmt.setString(3, salt);

            if(connectedUser == 0)
            {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(4, connectedUser);
            }

            stmt.setInt(5, adminID);

            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * This function inserts the object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(INSERT_INTO);
            stmt.setString(1, eMail);
            stmt.setString(2, password);
            stmt.setString(3, salt);
            if(connectedUser == 0)
            {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(4, connectedUser);
            }
                
            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return True if successful, false otherwise.
     */
    private boolean executeUpdate(PreparedStatement stmt)
    {
        boolean success = false;
        try
        {
            Connection con = stmt.getConnection();
            try
            {               
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                stmt.close();
            }
            catch (SQLException sqle)
            {
                success = false;
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * @return The adminID.
     */
    public int getAdminID() {
        return adminID;
    }

    /**
     * @param adminID The adminID to set.
     */
    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    /**
     * @return The eMail.
     */
    public String getEmail() {
        return eMail;
    }

    /**
     * @param eMail The eMail to set.
     */
    public void setEmail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * @return The salt.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt The salt to set.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * @return The connectedUser.
     */
    public int getConnectedUser() {
        return connectedUser;
    }

    /**
     * @param connectedUser The connectedUser to set.
     */
    public void setConnectedUser(int connectedUser) {
        this.connectedUser = connectedUser;
    }
    
    /**
     * 
     * @return The adminID as string.
     */
    public String getAdminIDString()
    {
        return "a" + adminID;
    }
    
    /**
     * 
     * @param adminID The adminID as a String
     */
    public void setUserIDString(String adminID)
    {
        if(adminID.startsWith("a"))
        {
            this.adminID = Integer.valueOf(adminID.substring(1));
        }
    }
}
