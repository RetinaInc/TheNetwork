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
 * This class represents a row of the Votings table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class VotingActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            " Insert into Voting(VotingUser, VotingFanpage, VotedPost, VotedComment, Upvote)" +
            " Values (?, ?, ?, ?, ?)";
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from Voting";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private static final String DELETE =
            " Delete" +
            " from Voting";
    
    /**
     * Reduces the selection to the pageID and commentID.
     */
    private static final String BY_PAGEID_AND_COMMENTID =
            " Where VotingFanpage = ?"
            + " And VotedComment = ?";
    
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
    
    private int votingUser = 0;
    private int votingPage = 0;
    private int votedPost = 0;
    private int votedComment = 0;
    private boolean upvote;

    
     /**
     * This function checks if the user has allready voted for the post.
     * @param userID The userID of the (current) user.
     * @param postID The postID of the post.
     * @return An arrayList containing one object if the user has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByUserAndPost(int userID, int postID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_USERID_AND_POSTID);
                stmt.setInt(1, userID);
                stmt.setInt(2, postID);
                
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
     * This function gets all previous votes on the post.
     * @param postID The postID of the post.
     * @return An arrayList containing one object if the user has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByPost(int postID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
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
                    VotingActiveRecord e = createVoting(rs);
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
     * This function retrieves all votes for a comment.
     * @param commentID The CommentID of the comment.
     * @return An arrayList containing one object if the user has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByComment(int commentID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_COMMENTID);
                stmt.setInt(1, commentID);
                
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
     * This function checks if the user has allready voted for the comment.
     * @param userID The userID of the (current) user.
     * @param commentID The commentID of the comment.
     * @return An arrayList containing one object if the user has voted for the comment, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByUserAndComment(int userID, int commentID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_USERID_AND_COMMENTID);
                stmt.setInt(1, userID);
                stmt.setInt(2, commentID);
                
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
     * This function checks if the page has allready voted for the post.
     * @param pageID The pageID of the (current) page.
     * @param postID The postID of the post
     * @return An arrayList containing one object if the page has voted for the post, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByPageAndPost(int pageID, int postID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_PAGEID_AND_POSTID);
                stmt.setInt(1, pageID);
                stmt.setInt(2, postID);
                
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
     * This function checks if the page has allready voted for the comment.
     * @param pageID The pageID of the (current) page.
     * @param commentID The commentID of the post.
     * @return An arrayList containing one object if the page has voted for the comment, otherwise the arraylist is empty.
     */
    public static ArrayList<VotingActiveRecord> findVoteByPageAndComment(int pageID, int commentID)
    {
        ArrayList<VotingActiveRecord> recs = new ArrayList<VotingActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_PAGEID_AND_COMMENTID);
                stmt.setInt(1, pageID);
                stmt.setInt(2, commentID);
                
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
     * This function creates a new voting record using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created post.
     */
    protected static VotingActiveRecord createVoting(ResultSet rs)
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
    
    /**
     * This function inserts the object into the database.
     * @return True if insert was successful, false otherwise.
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
                
                if(votingUser == 0)
                {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                else
                {
                    stmt.setInt(1, votingUser);
                }
                
                if(votingPage == 0)
                {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }
                else
                {
                    stmt.setInt(2, votingPage);
                }
                if(votedPost == 0)
                {
                    stmt.setNull(3, java.sql.Types.INTEGER);
                }
                else
                {
                    stmt.setInt(3, votedPost);
                }
                
                if(votedComment == 0)
                {
                    stmt.setNull(4, java.sql.Types.INTEGER);
                }
                else
                {
                    stmt.setInt(4, votedComment);
                }
                
                stmt.setBoolean(5, upvote);
                
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
                if(votingPage != 0)
                {
                    if(votedComment != 0)
                    {
                        stmt = con.prepareStatement(DELETE + BY_PAGEID_AND_COMMENTID);
                        stmt.setInt(2, votedComment);
                    }
                    else if(votedPost != 0)
                    {
                        stmt = con.prepareStatement(DELETE + BY_PAGEID_AND_POSTID);
                        stmt.setInt(2, votedPost);
                    }
                    else
                    {
                        return false;
                    }
                    stmt.setInt(1, votingPage);
                }
                else if (votingUser != 0)
                {
                    if(votedComment != 0)
                    {
                        stmt = con.prepareStatement(DELETE + BY_USERID_AND_COMMENTID);
                        stmt.setInt(2, votedComment);
                    }
                    else if(votedPost != 0)
                    {
                        stmt = con.prepareStatement(DELETE + BY_USERID_AND_POSTID);
                        stmt.setInt(2, votedPost);
                    }
                    else
                    {
                        return false;
                    }
                    stmt.setInt(1, votingUser);
                }
                else
                {
                    return false;
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
     * @return The votingUser.
     */
    public int getVotingUser() {
        return votingUser;
    }

    /**
     * @param votingUser The votingUser to set.
     */
    public void setVotingUser(int votingUser) {
        this.votingUser = votingUser;
    }

    /**
     * @return The votingPage.
     */
    public int getVotingPage() {
        return votingPage;
    }

    /**
     * @param votingPage The votingPage to set.
     */
    public void setVotingPage(int votingPage) {
        this.votingPage = votingPage;
    }

    /**
     * @return The votedPost.
     */
    public int getVotedPost() {
        return votedPost;
    }

    /**
     * @param votedPost The votedPost to set.
     */
    public void setVotedPost(int votedPost) {
        this.votedPost = votedPost;
    }

    /**
     * @return The votedComment.
     */
    public int getVotedComment() {
        return votedComment;
    }

    /**
     * @param votedComment The votedComment to set.
     */
    public void setVotedComment(int votedComment) {
        this.votedComment = votedComment;
    }

    /**
     * @return The upvote flag.
     */
    public boolean isUpvote() {
        return upvote;
    }

    /**
     * @param upvote The upvote flag to set.
     */
    public void setUpvote(boolean upvote) {
        this.upvote = upvote;
    }
    
}
