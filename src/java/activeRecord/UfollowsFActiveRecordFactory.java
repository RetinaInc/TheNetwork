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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class retrieves active records of the UfollowsF table from the database.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UfollowsFActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            "Select * " +
            "from UFollowsF";
    
    /**
     * This String contains the part of the SQL command counting the number of rows.
     */
    private static final String COUNT_ROWS =
            " Select count(*) as Number" + 
            " From UfollowsF";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_USERID =
            " Where FollowingUser=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pageID.
     */
    private static final String BY_PAGEID =
            " Where FollowedFanpage=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a page and a user.
     */
    private static final String BY_USERID_PAGEID =
            " Where FollowingUser = ?" + 
            " And FollowedFanpage = ?";
    
    /**
     * This counts all following user of a page.
     * @param pageID The pageID of the page.
     * @return The number of following user.
     */
    public static int countFollowingByPageID(int pageID)
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt;
            ResultSet rs;

            try
            {
                stmt = con.prepareStatement(COUNT_ROWS + BY_PAGEID);
                stmt.setInt(1, pageID);
                
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
                result = 0;
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
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<UfollowsFActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<UfollowsFActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    UfollowsFActiveRecord e = createUfollowsFActiveRecord(rs);
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
     * This function retrieves the record showing that the user is following the page.
     * @param userID The userID who is checked.
     * @param pageID The pageID who is checked.
     * @return An ArrayList with all rows fitting the userID and pageID.
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByUserIDAndPageID(int userID, int pageID)
    {
        ArrayList<UfollowsFActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID_PAGEID);
            stmt.setInt(1, userID);
            stmt.setInt(2, pageID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function gets all followed pages of a specific user.
     * @param userID The userID of the user.
     * @return An ArrayList with all rows fitting the userID.
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByUserID(int userID)
    {
        ArrayList<UfollowsFActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID);
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
     * This function gets all following user of a page.
     * @param pageID The pageID of the page.
     * @return An ArrayList with all rows fitting the pageID.
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByPageID(int pageID)
    {
        ArrayList<UfollowsFActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_PAGEID);
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
     * This function creates a new user-follows-page active record using the data from the current position of the result set.
     * @param rs The data source for the new active record.
     * @return The new created active record.
     */
    private static UfollowsFActiveRecord createUfollowsFActiveRecord(ResultSet rs)
    {
        UfollowsFActiveRecord d = new UfollowsFActiveRecord();
        try
        {
            d.setFollowedPage(rs.getInt("FollowedFanpage"));
            d.setFollowingUser(rs.getInt("FollowingUser"));
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
     * This function checks if a user is following a page.
     * @param user The current user.
     * @param page The fanpage.
     * @return True if the user is following the page.
     */
    public static boolean isFollowing(int user, int page)
    {
        ArrayList<UfollowsFActiveRecord> list = findFollowingByUserIDAndPageID(user, page);
        if(list.size() == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
