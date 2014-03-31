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
 * This class represents a row of the UfollowsF table and provides functions to check if a user is following a page and insert data in the table.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UfollowsFActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into UfollowsF(FollowingUser, FollowedFanpage)" +
            "Values (?, ?) ";
    
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
    private static String BY_USERID_PAGEID =
            " Where FollowingUser = ?" + 
            " And FollowedFanpage = ?";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private static final String DELETE =
            " Delete" +
            " from UfollowsF";
    
    private int followingUser;
    private int followedPage;
    
    /**
     * This function retrieves if there is a record that the user is following the page
     * @param userID The userID who is checked
     * @param pageID The pageID who is checked
     * @return An array list with all rows fitting the user and pageID
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByUserIDAndPageID(int userID, int pageID)
    {
        ArrayList<UfollowsFActiveRecord> recs = new ArrayList<UfollowsFActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_USERID_PAGEID);
                stmt.setInt(1, userID);
                stmt.setInt(2, pageID);
                
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
     * This gets all followed pages of a specific user
     * @param userID The userID of the user.
     * @return An array list with all rows fitting the userID.
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByUserID(int userID)
    {
        ArrayList<UfollowsFActiveRecord> recs = new ArrayList<UfollowsFActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_USERID);
                stmt.setInt(1, userID);
                
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
     * This gets all following user of a page.
     * @param pageID The pageID of the page.
     * @return An array list with all rows fitting the userID.
     */
    public static ArrayList<UfollowsFActiveRecord> findFollowingByPageID(int pageID)
    {
        ArrayList<UfollowsFActiveRecord> recs = new ArrayList<UfollowsFActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_PAGEID);
                stmt.setInt(1, pageID);
                
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
            PreparedStatement stmt = null;
            ResultSet rs = null;

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
     * This function creates a new friends set using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created comment.
     */
    protected static UfollowsFActiveRecord createUfollowsFActiveRecord(ResultSet rs)
    {
        UfollowsFActiveRecord d = new UfollowsFActiveRecord();
        try
        {
            d.setFollowedPage(rs.getInt("FollowedFanpage"));
            d.setFollowingUser(rs.getInt("FollowingUser"));
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
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
                
                stmt.setInt(1, followingUser);
                stmt.setInt(2, followedPage);
                
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
     * Removes the row, the object is representing from the database.
     * @return True if remove was successful, false otherwise.
     */
    public boolean remove()
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
               
                stmt = con.prepareStatement(DELETE + BY_USERID_PAGEID);

                stmt.setInt(1, followingUser);
                stmt.setInt(2, followedPage);

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
     * This function checks if a user is following a page.
     * @param user The current user.
     * @param friend The fanpage.
     * @return True if the two users are friends.
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
    
    /**
     * @return The followingUser.
     */
    public int getFollowingUser() {
        return followingUser;
    }

    /**
     * @param followingUser The followingUser to set.
     */
    public void setFollowingUser(int followingUser) {
        this.followingUser = followingUser;
    }

    /**
     * @return The followedPage.
     */
    public int getFollowedPage() {
        return followedPage;
    }

    /**
     * @param followedPage The followedPage to set.
     */
    public void setFollowedPage(int followedPage) {
        this.followedPage = followedPage;
    }
}
