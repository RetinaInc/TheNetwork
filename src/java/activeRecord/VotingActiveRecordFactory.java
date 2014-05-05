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
 * This class retrieves active records of the Votings table from the database.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class VotingActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from Voting";
    
    /**
     * Reduces the selection to the postID.
     */
    private static final String BY_POSTID =
            " Where VotedPost = ?";
    
    /**
     * Reduces the selection to the commentID.
     */
    private static final String BY_COMMENTID =
            " Where VotedComment = ?";
    
    /**
     * Reduces the selection to the pageID and commentID.
     */
    private static final String BY_PAGEID_AND_COMMENTID =
            " Where VotingFanpage = ?"
            + " And VotedComment = ?";
    
    /**
     * Reduces the selection to the pageID and postID.
     */
    private static final String BY_PAGEID_AND_POSTID =
            " Where VotingFanpage = ?"
            + " And VotedPost = ?";
    
    /**
     * Reduces the selection to the userID and commentID.
     */
    private static final String BY_USERID_AND_COMMENTID =
            " Where VotingUser = ?"
            + " And VotedComment = ?";
    
    /**
     * Reduces the selection to the userID and postID.
     */
    private static final String BY_USERID_AND_POSTID =
            " Where VotingUser = ?"
            + " And VotedPost = ?";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<VotingActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    VotingActiveRecord e = createVoting(rs);
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
     * This function gets all previous votes on the post.
     * @param postID The postID of the post.
     * @return An ArrayList containing all votes for the post
     */
    public static ArrayList<VotingActiveRecord> findVoteByPost(int postID)
    {
        ArrayList<VotingActiveRecord> recs;
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
     * This function retrieves all votes for a comment.
     * @param commentID The CommentID of the comment.
     * @return An ArrayList containing all votes for the comment.
     */
    public static ArrayList<VotingActiveRecord> findVoteByComment(int commentID)
    {
        ArrayList<VotingActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_COMMENTID);
            stmt.setInt(1, commentID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function checks if the user has allready voted for the post.
     * @param userID The userID of the (current) user.
     * @param postID The postID of the post.
     * @return An ArrayList containing one object if the user has voted for the post, otherwise the ArrayList is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByUserAndPost(int userID, int postID)
    {
        ArrayList<VotingActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID_AND_POSTID);
            stmt.setInt(1, userID);
            stmt.setInt(2, postID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function checks if the user has allready voted for the comment.
     * @param userID The userID of the (current) user.
     * @param commentID The commentID of the comment.
     * @return An ArrayList containing one object if the user has voted for the comment, otherwise the ArrayList is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByUserAndComment(int userID, int commentID)
    {
        ArrayList<VotingActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID_AND_COMMENTID);
            stmt.setInt(1, userID);
            stmt.setInt(2, commentID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function checks if the page has allready voted for the post.
     * @param pageID The pageID of the (current) page.
     * @param postID The postID of the post
     * @return An ArrayList containing one object if the page has voted for the post, otherwise the ArrayList is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByPageAndPost(int pageID, int postID)
    {
        ArrayList<VotingActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_PAGEID_AND_POSTID);
            stmt.setInt(1, pageID);
            stmt.setInt(2, postID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function checks if the page has allready voted for the comment.
     * @param pageID The pageID of the (current) page.
     * @param commentID The commentID of the post.
     * @return An ArrayList containing one object if the page has voted for the comment, otherwise the ArrayList is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByPageAndComment(int pageID, int commentID)
    {
        ArrayList<VotingActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_PAGEID_AND_COMMENTID);
            stmt.setInt(1, pageID);
            stmt.setInt(2, commentID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function creates a new voting record using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created post.
     */
    private static VotingActiveRecord createVoting(ResultSet rs)
    {
        VotingActiveRecord d = new VotingActiveRecord();
        try
        {
            d.setUpvote(rs.getBoolean("Upvote"));
            d.setVotedComment(rs.getInt("VotedComment"));
            d.setVotedPost(rs.getInt("VotedPost"));
            d.setVotingPage(rs.getInt("VotingFanpage"));
            d.setVotingUser(rs.getInt("VotingUser"));
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
