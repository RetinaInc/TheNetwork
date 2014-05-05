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
 * This class retrieves active records of the Comment table from the database.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class CommentActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from Comment";
    
    /**
     * This String contains the part of the SQL command counting the number of rows.
     */
    private static final String COUNT_ROWS =
            " Select count(*) as Number" + 
            " From Comment";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific commentID.
     */
    private static final String BY_ID =
            " Where CommentID=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific postID.
     */
    private static final String BY_POSTID =
            " Where RelatedPost=?";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<CommentActiveRecord> executeQuery(PreparedStatement stmt, String viewingUser)
    {
        ArrayList<CommentActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs = null;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    CommentActiveRecord e = createComment(rs, viewingUser);
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
     * This function executes a count query according to a prepared statement. The column with the counting result needs to be renamed to "Number" within the statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return The result of the counting.
     */
    private static int executeCount(PreparedStatement stmt)
    {
        int result = 0;
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {
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
     * This function counts all comments published on the network.
     * @return The number of comments.
     */
    public static int countComments()
    {
        int result;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(COUNT_ROWS);
                
            result = executeCount(stmt);
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This counts all comments of a post.
     * @param postID The ID of the post.
     * @return The number of comments.
     */
    public static int countCommentsByPostID(int postID)
    {
        int result;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(COUNT_ROWS + BY_POSTID);
            stmt.setInt(1, postID);
            
            result = executeCount(stmt);
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This function retrieves all comments matching the provided postID from the database.
     * @param postID The postID of the searched comments.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByPostID(int postID)
    {
        return findCommentByPostID(postID, null);
    }
    
    /**
     * This function retrieves all comments matching the provided postID from the database using the viewing user.
     * @param postID The postID of the searched comments.
     * @param user The viewing user.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByPostID(int postID, String user)
    {
        ArrayList<CommentActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_POSTID);
            stmt.setInt(1, postID);

            recs = executeQuery(stmt, user);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all comments matching the provided commentID from the database using the viewing user.
     * @param commentID The commentID of the searched comments.
     * @param user The viewing user.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByCommentID(int commentID, String user)
    {
        ArrayList<CommentActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_ID);
            stmt.setInt(1, commentID);
                
            recs = executeQuery(stmt, user);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    } 
    
    /**
     * This function creates a new comment using the data from the current position of the result set and the viewing user.
     * @param rs The data source for the new fanpage.
     * @param userID The viewing user.
     * @return The new created post.
     */
    private static CommentActiveRecord createComment(ResultSet rs, String userID)
    {
        CommentActiveRecord d = new CommentActiveRecord();
        try
        {   
            d.setCommentID(rs.getInt("CommentID"));
            d.setCommentTimestamp(rs.getTimestamp("CommentTimestamp"));
            d.setContent(rs.getString("Content"));
            d.setPublishingUser(rs.getInt("PublishingUser"));
            d.setPublishingPage(rs.getInt("PublishingPage"));
            d.setRelatedPost(rs.getInt("RelatedPost"));
            if(d.getPublishingPage()!=0)
            {
                d.setPublisherName(FanpageActiveRecordFactory.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecordFactory.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            if(userID != null)
            {
                ArrayList<VotingActiveRecord> temp;
                if(userID.startsWith("u"))
                {
                    temp = VotingActiveRecordFactory.findVoteByUserAndComment(Integer.valueOf(userID.substring(1)), d.getCommentID());
                    if(!temp.isEmpty())
                    {
                        d.setVoteRecord(temp.get(0));
                    }
                    else
                    {
                        d.setVoteRecord(null);
                    }
                }
                else if (userID.startsWith("f"))
                {
                    temp = VotingActiveRecordFactory.findVoteByPageAndComment(Integer.valueOf(userID.substring(1)), d.getCommentID());
                    if(!temp.isEmpty())
                    {
                        d.setVoteRecord(temp.get(0));
                    }
                    else
                    {
                        d.setVoteRecord(null);
                    }
                }
                else
                {
                    d.setVoteRecord(null);
                }
            }
            else
            {
                d.setVoteRecord(null);
            }
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
