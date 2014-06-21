/*
 * Copyright (C) 2014 Frank Steiler <frank.steiler@steilerdev.de>
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class retrieves active records of the SysAdmin table from the database.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class SysAdminActiveRecordFactory extends DatabaseUtility {
    
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
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ADMINISTRATINGUSER =
            " Where ConnectedUser=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific adminID.
     */
    private static final String BY_ID =
            " Where AdminID=?";
    
     /**
     * This String contains the part of the SQL command reducing the selection to a specific eMail.
     */
    private static final String BY_EMAIL =
            " Where Email=?";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<SysAdminActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<SysAdminActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
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
                recs = null;
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
            PreparedStatement stmt = con.prepareStatement(SELECT_ADMINISTRATINGUSER + BY_ID);
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
     * This function will return an array with all system administrators administrated by a specific user
     * @param userID The userID of the administrating user.
     * @return An ArrayList with all admins administrated by the user.
     */
    public static ArrayList<SysAdminActiveRecord> findAdminsByAdministratingUser(int userID)
    {
        ArrayList<SysAdminActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_ADMINISTRATINGUSER);
            stmt.setInt(1, userID);
                
            recs = executeQuery(stmt);
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
     * @return An ArrayList with all admins matching the Email.
     */
    public static ArrayList<SysAdminActiveRecord> findAdminByEmail(String eMail)
    {
        ArrayList<SysAdminActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_EMAIL);
            stmt.setString(1, eMail);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all system administrators matching the provided AdminID from the database.
     * @param adminID The adminID of the searched admin.
     * @return An ArrayList with all admins matching the adminID (Should only contain one element since AdminID is primary key).
     */
    public static ArrayList<SysAdminActiveRecord> findAdminByID(int adminID)
    {
        ArrayList<SysAdminActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_ID);
            stmt.setInt(1, adminID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }

    /**
     * This function creates a new system administrator active record using the data from the current position of the result set.
     * @param rs The data source for the new system administrator.
     * @return The new created system administrator.
     */
    private static SysAdminActiveRecord createSysAdmin(ResultSet rs)
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
}
