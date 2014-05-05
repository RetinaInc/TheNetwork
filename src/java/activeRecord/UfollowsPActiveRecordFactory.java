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
 * This class retrieves active records of the UfollowsP table from the database.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UfollowsPActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contians the SQL command to update the read flag in the database.
     */
    private static final String UPDATE_READ =
            " Update UfollowsP" + 
            " Set PostRead = ?";
    
     /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from UfollowsP";
    
    /**
     * This String contians the SQL command reducing the selection to the postID.
     */
    private static final String BY_POSTID =
            " Where FollowedPost = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to the userID and postID.
     */
    private static final String BY_POSTID_USERID =
            " Where FollowedPost = ?" +
            " And FollowingUser = ?";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<UfollowsPActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<UfollowsPActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    UfollowsPActiveRecord e = createUfollowsP(rs);
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
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return True if successful, false otherwise.
     */
    private static boolean executeUpdate(PreparedStatement stmt)
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
     * This function retrieves all connections between the post and user.
     * @param postID The postID of the post.
     * @return An ArrayList containing all following users of a post.
     */
    public static ArrayList<UfollowsPActiveRecord> findUfollowsPByPostID(int postID)
    {
        ArrayList<UfollowsPActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_POSTID);
            stmt.setInt(1, postID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all connections between the post and user.
     * @param postID The postID of the post.
     * @param userID The userID of the viewing user.
     * @return An ArrayList containing one object if the user is following the post.
     */
    public static ArrayList<UfollowsPActiveRecord> findUfollowsPByPostIDUserID(int postID, int userID)
    {
        ArrayList<UfollowsPActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_POSTID_USERID);
            stmt.setInt(1, postID);
            stmt.setInt(2, userID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * Resets the read flag of every row with the postID.
     * @param postID The postID of the changed post.
     * @return True if successful, false otherwise.
     */
    public static boolean notify(int postID)
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE_READ + BY_POSTID);
            stmt.setBoolean(1, false);
            stmt.setInt(2, postID);
            
            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * Sets the read flag of the row defined by a postID and userID
     * @param postID The postID of the changed post.
     * @param userID The userID of the accessing user.
     * @return True if successful, false otherwise.
     */
    public static boolean setRead(int postID, int userID)
    {
        boolean success = false;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE_READ + BY_POSTID_USERID);
            stmt.setBoolean(1, true);
            stmt.setInt(2, postID);
            stmt.setInt(3, userID);
            
            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * This function creates a new active record using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created active record.
     */
    private static UfollowsPActiveRecord createUfollowsP(ResultSet rs)
    {
        UfollowsPActiveRecord d = new UfollowsPActiveRecord();
        try
        {
            d.setFollowedPost(rs.getInt("FollowedPost"));
            d.setFollowingUser(rs.getInt("FollowingUser"));
            d.setPostRead(rs.getBoolean("PostRead"));
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
