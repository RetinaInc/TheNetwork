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
import static activeRecord.UfollowsPActiveRecord.createUfollowsP;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a row of the FfollowsP table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class FfollowsPActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into FfollowsP(FollowingFanpage, FollowedPost, PostRead)" +
            " Values (?, ?, ?)";
    
    /**
     * This String contians the SQL command to update the read flag in the database.
     */
    private static final String UPDATE_READ =
            " Update FfollowsP" + 
            " Set PostRead = ?";
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from FfollowsP";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private static final String DELETE =
            " Delete" +
            " from FfollowsP";
    
    /**
     * Reduces the selection to the userID.
     */
    private static final String BY_POSTID =
            " Where FollowedPost = ?";
    
    /**
     * Reduces the selection to the userID.
     */
    private static final String BY_POSTID_PAGEID =
            " Where FollowedPost = ?" +
            " And FollowingFanpage = ?";
    
    private int followingFanpage;
    private int followedPost;
    private boolean postRead;
    
    /**
     * This function retrieves all connections between the post and fanpage.
     * @param postID The postID of the post.
     * @return An arrayList containing one object if the user has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<FfollowsPActiveRecord> findFfollowsPByPostID(int postID)
    {
        ArrayList<FfollowsPActiveRecord> recs = new ArrayList<FfollowsPActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_POSTID);
                stmt.setInt(1, postID);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    FfollowsPActiveRecord e = createFfollowsP(rs);
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
     * This function retrieves all connections between the post and fanpage.
     * @param postID The postID of the post.
     * @param pageID The pageID of the viewing user.
     * @return An arrayList containing one object if the user has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<FfollowsPActiveRecord> findFfollowsPByPostIDPageID(int postID, int pageID)
    {
        ArrayList<FfollowsPActiveRecord> recs = new ArrayList<FfollowsPActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_POSTID_PAGEID);
                stmt.setInt(1, postID);
                stmt.setInt(2, pageID);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    FfollowsPActiveRecord e = createFfollowsP(rs);
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
                
                stmt.setInt(1, followingFanpage);
                stmt.setInt(2, followedPost);
                stmt.setBoolean(3, postRead);
                
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
               
                stmt = con.prepareStatement(DELETE + BY_POSTID_PAGEID);

                stmt.setInt(1, followedPost);
                stmt.setInt(2, followingFanpage);

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
     * Resets the read flag of every row with the postID.
     * @param postID The postID of the changed post.
     * @return True if remove was successful, false otherwise.
     */
    public static boolean notify(int postID)
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
               
                stmt = con.prepareStatement(UPDATE_READ + BY_POSTID);
                stmt.setBoolean(1, false);
                stmt.setInt(2, postID);
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
     * Sets the read flag of the row defined by a postID and pageID
     * @param postID The postID of the changed post.
     * @param pageID The pageID of the accessing user.
     * @return True if remove was successful, false otherwise.
     */
    public static boolean setRead(int postID, int pageID)
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
               
                stmt = con.prepareStatement(UPDATE_READ + BY_POSTID_PAGEID);
                stmt.setBoolean(1, true);
                stmt.setInt(2, postID);
                stmt.setInt(3, pageID);
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
     * This function creates a new voting record using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created post.
     */
    protected static FfollowsPActiveRecord createFfollowsP(ResultSet rs)
    {
        FfollowsPActiveRecord d = new FfollowsPActiveRecord();
        try
        {
            d.setFollowedPost(rs.getInt("FollowedPost"));
            d.setFollowingFanpage(rs.getInt("FollowingFanpage"));
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

    /**
     * @return The followingFanpage.
     */
    public int getFollowingFanpage() {
        return followingFanpage;
    }

    /**
     * @param followingFanpage The followingFanpage to set.
     */
    public void setFollowingFanpage(int followingFanpage) {
        this.followingFanpage = followingFanpage;
    }

    /**
     * @return The followedPost.
     */
    public int getFollowedPost() {
        return followedPost;
    }

    /**
     * @param followedPost The followedPost to set.
     */
    public void setFollowedPost(int followedPost) {
        this.followedPost = followedPost;
    }

    /**
     * @return The postRead flag.
     */
    public boolean isPostRead() {
        return postRead;
    }

    /**
     * @param postRead The postRead to set.
     */
    public void setPostRead(boolean postRead) {
        this.postRead = postRead;
    }
}
