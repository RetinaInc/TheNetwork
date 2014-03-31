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

import static activeRecord.DatabaseUtility.getDatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a row of the SysAdmin table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class SysAdminActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into SysAdmin(Email, Password, Salt, ConnectedUser)" +
            "Values (?, ?, ?, ?)";
    
    /**
     * This String contains the part of the SQL command selecting the AdminID from the table.
     */
    private static final String SELECT_ID =
            "Select AdminID " +
            "from SysAdmin";
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            "Select * " +
            "from SysAdmin";
    
    /**
     * This String contains the part of the SQL command selecting the UserID of the administrating user from the table.
     */
    private static final String SELECT_ADMINISTRATINGUSER =
            "Select ConnectedUser " +
            "from SysAdmin";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific adminID.
     */
    private static final String BY_ID =
            " Where AdminID=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ADMINISTRATINGUSER =
            " Where ConnectedUser=?";
    
     /**
     * This String contains the part of the SQL command reducing the selection to a specific eMail.
     */
    private static final String BY_EMAIL =
            " Where Email=?";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the UserID.
     */
    private static final String ORDER_BY_ID_DESC =
            " Order by AdminID desc";
    
     /**
     * This String cointains the SQL command to update data in the database.
     */
    private static final String UPDATE =
            " Update SysAdmin" + 
            " Set Email = ?, Password = ?, Salt = ?, ConnectedUser = ?";
    
    private int adminID;
    private String eMail;
    private String password;
    private String salt;
    private int connectedUser;
    
    /**
     * Finds the administrting user for a system administrator.
     * @param adminID The admin ID of the admin whose administrating user is searched.
     * @return The userID of the administrating user. '0' if there is no administrating user.
     */
    public static int findAdministratingUser(int adminID)
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ADMINISTRATINGUSER + BY_ID);
            stmt.setInt(1, adminID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                result = rs.getInt("ConnectedUser");
            }
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * Updates the row with the adminID of the object with the data within this object.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
                stmt = con.prepareStatement(UPDATE + BY_ID);
                
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
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                stmt.close();
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
                success = false;
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
    
    /**
     * This function will return an array with all adminID's administrated by a specific user
     * @param userID The userID of the administrating user.
     * @return An ArrayList with all admins administrated by the user.
     */
    public static ArrayList<SysAdminActiveRecord> findAdminsByAdministratingUser(int userID)
    {
        ArrayList<SysAdminActiveRecord> recs = new ArrayList<SysAdminActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_ADMINISTRATINGUSER);
                stmt.setInt(1, userID);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    SysAdminActiveRecord e = createSysAdmin(rs);
                    recs.add(e);
                }

                rs.close();
                stmt.close();
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }

    /**
     * This function retrieves all system administrators matching the provided eMail from the database.
     * @param eMail The Email of the searched system administrator.
     * @return An array list with all admins matching the Email.
     */
    public static ArrayList<SysAdminActiveRecord> findAdminByEmail(String eMail)
    {
        ArrayList<SysAdminActiveRecord> recs = new ArrayList<SysAdminActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_EMAIL);
                stmt.setString(1, eMail);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    SysAdminActiveRecord e = createSysAdmin(rs);
                    recs.add(e);
                }

                rs.close();
                stmt.close();
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all system administrators matching the provided AdminID from the database.
     * @param adminID The adminID of the searched page.
     * @return An array list with all admins matching the adminID (Should only contain one element since AdminID is primary key).
     */
    public static ArrayList<SysAdminActiveRecord> findAdminByID(int adminID)
    {
        ArrayList<SysAdminActiveRecord> recs = new ArrayList<SysAdminActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_ID);
                stmt.setInt(1, adminID);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    SysAdminActiveRecord e = createSysAdmin(rs);
                    recs.add(e);
                }

                rs.close();
                stmt.close();
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }

    /**
     * This function creates a new system administrator using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created fanpage.
     */
    protected static SysAdminActiveRecord createSysAdmin(ResultSet rs)
    {
        SysAdminActiveRecord d = new SysAdminActiveRecord();
        try
        {
            d.setAdminID(rs.getInt("AdminID"));
            d.setPassword(rs.getString("Password"));
            d.setEmail(rs.getString("Email"));
            d.setSalt(rs.getString("Salt"));
            d.setConnectedUser(rs.getInt("ConnectedUser"));
        }
        catch (SQLException sqle)
        {
            d = null;
        }
        finally
        {
            return d;
        }
    }    
    
    /**
     * This function inserts the object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
                stmt = con.prepareStatement(INSERT_INTO);
                
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
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                stmt.close();
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
                success = false;
            }
            finally
            {
                closeDatabaseConnection(con);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
