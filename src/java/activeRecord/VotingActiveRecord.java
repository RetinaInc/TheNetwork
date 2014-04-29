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
import java.sql.SQLException;

/**
 * This class represents a row of the Votings table providing functions to insert and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class VotingActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            " Insert into Voting(VotingUser, VotingFanpage, VotedPost, VotedComment, Upvote)" +
            " Values (?, ?, ?, ?, ?)";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private final String DELETE =
            " Delete" +
            " from Voting";
    
    /**
     * Reduces the selection to the pageID and commentID.
     */
    private final String BY_PAGEID_AND_COMMENTID =
            " Where VotingFanpage = ?"
            + " And VotedComment = ?";
    
    /**
     * Reduces the selection to the pageID and postID.
     */
    private final String BY_PAGEID_AND_POSTID =
            " Where VotingFanpage = ?"
            + " And VotedPost = ?";
    
    /**
     * Reduces the selection to the userID and commentID.
     */
    private final String BY_USERID_AND_COMMENTID =
            " Where VotingUser = ?"
            + " And VotedComment = ?";
    
    /**
     * Reduces the selection to the userID and postID.
     */
    private final String BY_USERID_AND_POSTID =
            " Where VotingUser = ?"
            + " And VotedPost = ?";
    
    private int votingUser = 0;
    private int votingPage = 0;
    private int votedPost = 0;
    private int votedComment = 0;
    private boolean upvote;

    /**
     * This function inserts the object into the database.
     * @return True if insert was successful, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(INSERT_INTO);
                
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
                
            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
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
            PreparedStatement stmt;
            if(votingPage != 0)
            {
                if(votedComment != 0)
                {
                    stmt = getDatabaseConnection().prepareStatement(DELETE + BY_PAGEID_AND_COMMENTID);
                    stmt.setInt(2, votedComment);
                }
                else if(votedPost != 0)
                {
                    stmt = getDatabaseConnection().prepareStatement(DELETE + BY_PAGEID_AND_POSTID);
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
                    stmt = getDatabaseConnection().prepareStatement(DELETE + BY_USERID_AND_COMMENTID);
                    stmt.setInt(2, votedComment);
                }
                else if(votedPost != 0)
                {
                    stmt = getDatabaseConnection().prepareStatement(DELETE + BY_USERID_AND_POSTID);
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
