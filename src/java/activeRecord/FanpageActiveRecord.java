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
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class represents a row of the FanpageAdmin table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class FanpageActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into Fanpage(DisplayName, PageName, Subject, Email, Premium, Password, Salt, AdministratingUser)" +
            "Values (?, ?, ?, ?, ?, ?, ?, ?) ";
    
    /**
     * This String contains the part of the SQL command selecting the PageID from the table.
     */
    private static final String SELECT_ID =
            "Select PageID " +
            "from Fanpage";
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            "Select * " +
            "from Fanpage";
    
    /**
     * This String contains the part of the SQL command selecting the UserID of the administrating user from the table.
     */
    private static final String SELECT_ADMINISTRATINGUSER =
            "Select AdministratingUser " +
            "from Fanpage";
    
    /**
     * This String contains the part of the SQL command selecting the columns provided by the fanpage table from the table.
     */
    private static final String SELECT_ALL_PAGE =
            " Select Fanpage.*" +
            " from Fanpage";
    
    /**
     * This String contains the part of the SQL command counting the number of rows.
     */
    private static final String COUNT_ROWS =
            " Select count(*) as Number" + 
            " From Fanpage";
    
    /**
     * This String contains the part of the SQL command reducing the selection to followed pages.
     */
    private static final String FROM_FOLLOWEDPAGES =
            " Inner Join UfollowsF" +
            " On UfollowsF.FollowedFanpage = Fanpage.PageID" + 
            " Where UfollowsF.FollowingUser = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all user after the specified fanpage.
     */
    private static final String AND_AFTER_PAGE =
            " And Fanpage.DisplayName > ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all user after the specified fanpage.
     */
    private static final String AFTER_PAGE =
            " Where Fanpage.DisplayName > ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ID =
            " Where PageID=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ADMINISTRATINGUSER =
            " Where AdministratingUser=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific eMail.
     */
    private static final String BY_EMAIL =
            " Where Email=?";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the UserID.
     */
    private static final String ORDER_BY_ID_DESC =
            " Order by PageID desc";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the displaName.
     */
    private static final String ORDER_BY_DISPLAYNAME_ASC =
            " Order by DisplayName asc";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private static final String UPDATE =
            " Update Fanpage" + 
            " Set DisplayName = ?, PageName = ?, Subject = ?, Email = ?, Premium = ?, Password = ?, AdministratingUser = ?";
    
    
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
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<FanpageActiveRecord> executeQuery(PreparedStatement stmt)
    {
        return executeQuery(stmt, 0);
    }
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @param amount The maximum amount of items in the resulting Array.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<FanpageActiveRecord> executeQuery(PreparedStatement stmt, int amount)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            ResultSet rs = null;

            try
            {               
                rs = stmt.executeQuery();
                
                if(amount == 0)
                {
                    while (rs.next())
                    {
                        FanpageActiveRecord e = createFanpage(rs);
                        recs.add(e);
                    }
                }
                else
                {
                    for(int i = 0; i < amount && rs.next(); i++)
                    {
                        FanpageActiveRecord e = createFanpage(rs);
                        recs.add(e);
                    }
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
     * This counts all fanpages subscibed to the network.
     * @return The number of fanpages.
     */
    public static int countFanpages()
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(COUNT_ROWS);
                
                rs = stmt.executeQuery();
                
                if(rs.next())
                {
                    result = rs.getInt("Number");
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
            result = 0;
        }
        return result;
    }
    
    /**
     * Finds the administrting user for a page.
     * @param pageID The pageID of the page whose administrating user is searched.
     * @return The userID of the administrating user. '0' if there is no administrating user.
     */
    public static int findAdministratingUser(int pageID)
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ADMINISTRATINGUSER + BY_ID);
            stmt.setInt(1, pageID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                result = rs.getInt("AdministratingUser");
            }
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This function will return an array with all pageID's administrated by a specific user
     * @param userID The userID of the administrating user.
     * @return An ArrayList with all Fanpages administrated by the user.
     */
    public static ArrayList<FanpageActiveRecord> findPagesByAdministratingUser(int userID)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL + BY_ADMINISTRATINGUSER);
            stmt.setInt(1, userID);
            recs = executeQuery(stmt, 0);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all fanpages.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @return An ArrayList with all fanpages.
     */
    public static ArrayList<FanpageActiveRecord> findAllPages(int amount)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL + ORDER_BY_DISPLAYNAME_ASC);
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all fanpages starting at a specific page.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param afterPage The display name of the page determing the first page of the result set.
     * @return An ArrayList with all fanpages.
     */
    public static ArrayList<FanpageActiveRecord> findAllPagesAfterPage(int amount, String afterPage)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL + AFTER_PAGE + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setString(1, afterPage);
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all followed fanpages of a user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @return An ArrayList with all friends of the user.
     */
    public static ArrayList<FanpageActiveRecord> findAllFollowingPages(int userID, int amount)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL_PAGE + FROM_FOLLOWEDPAGES + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setInt(1, userID);
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all following pages starting at a specific page.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @param afterPage The display name of the page determing the first page of the result set.
     * @return An ArrayList with all followed pages of the user.
     */
    public static ArrayList<FanpageActiveRecord> findAllFollowingPagesAfterPage(int userID, int amount, String afterPage)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL_PAGE + FROM_FOLLOWEDPAGES + AND_AFTER_PAGE + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setInt(1, userID);
            stmt.setString(2, afterPage);
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all fanpages matching the provided eMail from the database.
     * @param eMail The Email of the searched fanpage.
     * @return An array list with all fanpages matching the eMail.
     */
    public static ArrayList<FanpageActiveRecord> findPageByEmail(String eMail)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(SELECT_ALL + BY_EMAIL);
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
     * This function retrieves all pages matching the provided PageID from the database.
     * @param pageID The pageID of the searched page.
     * @return An array list with all pages matching the pageID (Should only contain one element since PageID is primary key).
     */
    public static ArrayList<FanpageActiveRecord> findPageByID(int pageID)
    {
        ArrayList<FanpageActiveRecord> recs = new ArrayList<FanpageActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(SELECT_ALL + BY_ID);
                stmt.setInt(1, pageID);
            
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }

    /**
     * This function creates a new fanpage using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created fanpage.
     */
    protected static FanpageActiveRecord createFanpage(ResultSet rs)
    {
        FanpageActiveRecord d = new FanpageActiveRecord();
        try
        {
            d.setPageID(rs.getInt("PageID"));
            d.setDisplayName(rs.getString("DisplayName"));
            d.setPageName(rs.getString("PageName"));
            d.setSubject(rs.getString("Subject"));
            d.setEmail(rs.getString("Email"));
            d.setPremium(rs.getBoolean("Premium"));
            d.setPassword(rs.getString("Password"));
            d.setSalt(rs.getString("Salt"));
            d.setAdministratingUser(rs.getInt("AdministratingUser"));
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
                stmt = con.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS);
                
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
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                rs = stmt.getGeneratedKeys();
                rs.next();
                pageID = rs.getInt(1);
                
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
     * Updates the row with the pageID of the object with the data within this object.
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
     * @param Subject The Subject to set.
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
     * @param pageID The pageID as a String
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
        return UfollowsFActiveRecord.isFollowing(userID, this.pageID);
    }
    
}
