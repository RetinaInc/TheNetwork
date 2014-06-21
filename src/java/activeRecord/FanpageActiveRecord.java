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
import java.sql.Statement;

/**
 * This class represents a row of the Fanpage table providing functions to insert and update these rows.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class FanpageActiveRecord extends DatabaseUtility{
        
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into Fanpage(DisplayName, PageName, Subject, Email, Premium, Password, Salt, AdministratingUser)" +
            "Values (?, ?, ?, ?, ?, ?, ?, ?) ";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update Fanpage" + 
            " Set DisplayName = ?, PageName = ?, Subject = ?, Email = ?, Premium = ?, Password = ?, AdministratingUser = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pageID.
     */
    private final String BY_ID =
            " Where PageID=?";
    
    private int pageID;
    private String pageName;
    private String displayName;
    private String subject;
    private String eMail;
    private String password;
    private String salt;
    private boolean premium;
    private int administratingUser=0;
    
    /**
     * This function inserts the current object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, displayName);
            stmt.setString(2, pageName);
            stmt.setString(3, subject);
            stmt.setString(4, eMail);
            stmt.setBoolean(5, premium);
            stmt.setString(6, password);
            stmt.setString(7, salt);
            if(administratingUser == 0)
            {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(8, administratingUser);
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
     * Updates the row with the pageID of the object with the data within the current object.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE + BY_ID);
            stmt.setString(1, displayName);
            stmt.setString(2, pageName);
            stmt.setString(3, subject);
            stmt.setString(4, eMail);
            stmt.setBoolean(5, premium);
            stmt.setString(6, password);
            if(administratingUser == 0)
            {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(7, administratingUser);
            }

            stmt.setInt(8, pageID);
             
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
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs != null && rs.next())
                {
                    pageID = rs.getInt(1);
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
     * @return The pageID.
     */
    public int getPageID() {
        return pageID;
    }

    /**
     * @param pageID The pageID to set.
     */
    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    /**
     * @return The pageName.
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * @param pageName The pageName to set.
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * @return The displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return The Subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject The Subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
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
     * @return The premium flag.
     */
    public boolean isPremium() {
        return premium;
    }

    /**
     * @param premium The premium flag to set.
     */
    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    /**
     * @return The administratingUser.
     */
    public int getAdministratingUser() {
        return administratingUser;
    }

    /**
     * @param administratingUser The administratingUser to set.
     */
    public void setAdministratingUser(int administratingUser) {
        this.administratingUser = administratingUser;
    }
    
    /**
     * 
     * @return The pageID as string.
     */
    public String getPageIDString()
    {
        return "f" + pageID;
    }
    
    /**
     * 
     * @param pageID The pageID as string
     */
    public void setPageIDString(String pageID)
    {
        if(pageID.startsWith("f"))
        {
            this.pageID = Integer.valueOf(pageID.substring(1));
        }
    }
    
    /**
     * Checks if this fanpage is followed by a specific user.
     * @param userID The userID of the user who is checked.
     * @return True if the user is following, false otherwise.
     */
    public boolean isFollowedBy(int userID)
    {
        return UfollowsFActiveRecordFactory.isFollowing(userID, this.pageID);
    }
}
