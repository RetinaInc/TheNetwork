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
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class represents a row of the Comment table providing functions to insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class CommentActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into Comment(CommentTimestamp, Content, RelatedPost, PublishingUser, PublishingPage)" +
            " Values (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update Post" + 
            " Set PostTimestamp = ?, Content = ?, RelatedPost = ?, PublishingUser = ?, PublishingPage = ?";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private final String DELETE =
            " Delete" +
            " from Comment";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific commentID.
     */
    private final String BY_ID =
            " Where CommentID=?";
    
    private int commentID;
    private Timestamp commentTimestamp;
    private String content;
    private int relatedPost;
    private int publishingUser = 0;
    private int publishingPage = 0;
    private String publisherName;
    private VotingActiveRecord voteRecord;
    
    /**
     * Upvotes the current post using the provided userID.
     * @param userID The user who upvotes the post.
     * @return True if upvote was successfull, false otherwise.
     */
    public boolean upvote(String userID)
    {
        int user = Integer.valueOf(userID.substring(1));
        if(voteRecord != null)
        {
            return false;
        }
        else
        {
            voteRecord = new VotingActiveRecord();
            if(userID.startsWith("u"))
            {
                voteRecord.setVotingUser(user);
            }
            else if(userID.startsWith("f"))
            {
                voteRecord.setVotingPage(user);
            }
            else
            {
                return false;
            }
            voteRecord.setUpvote(true);
            voteRecord.setVotedComment(commentID);
            
            if(!voteRecord.insert())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Downvotes the current post using the provided userID.
     * @param userID The user who upvotes the post.
     * @return True if upvote was successfull, false otherwise.
     */
    public boolean downvote(String userID)
    {
        int user = Integer.valueOf(userID.substring(1));
        if(voteRecord != null)
        {
            return false;
        }
        else
        {
            voteRecord = new VotingActiveRecord();
            if(userID.startsWith("u"))
            {
                voteRecord.setVotingUser(user);
            }
            else if(userID.startsWith("f"))
            {
                voteRecord.setVotingPage(user);
            }
            else
            {
                return false;
            }
            voteRecord.setUpvote(false);
            voteRecord.setVotedComment(commentID);
            if(!voteRecord.insert())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Removes the vote from the current post using the provided userID.
     * @param userID The user who upvotes the post.
     * @return True if upvote was successfull, false otherwise.
     */
    public boolean removeVote(String userID)
    {
        int user = Integer.valueOf(userID.substring(1));
        if(voteRecord == null)
        {
            return false;
        }
        else
        {
            if (!voteRecord.remove())
            {
                return false;
            }
            else
            {
                voteRecord = null;
            } 
        }
        return true;
    }
    
    /**
     * This function inserts the object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO);
                
            stmt.setString(1, content);
            stmt.setInt(2, relatedPost);
            if(publishingUser == 0)
            {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(3, publishingUser);
            }

            if(publishingPage == 0)
            {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(4, publishingPage);
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
     * Removes the row with the commentID of the object from the database.
     * @return True if remove was successful, false otherwise.
     */
    public boolean remove()
    {
        boolean success = true;
        try
        {
            Connection con = getDatabaseConnection();
            
            ArrayList<VotingActiveRecord> votings = VotingActiveRecordFactory.findVoteByComment(commentID);
            for(int i = 0; i<votings.size() && success; i++)
            {
                success = votings.get(i).remove();
            }

            if(success)
            {
                PreparedStatement stmt = con.prepareStatement(DELETE + BY_ID);

                stmt.setInt(1, commentID);

                success = executeUpdate(stmt);
            }
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * Updates the row with the commentID of the object with the data within this object.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(UPDATE + BY_ID);

            stmt.setTimestamp(1, commentTimestamp);
            stmt.setString(2, content);
            stmt.setInt(3, relatedPost);
            if(publishingUser == 0)
            {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(4, publishingUser);
            }

            if(publishingPage == 0)
            {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            else
            {
                stmt.setInt(5, publishingPage);
            }
            stmt.setInt(6, commentID);
            
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
        ArrayList<CommentActiveRecord> recs = new ArrayList<CommentActiveRecord>();
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
     * @return The commentID.
     */
    public int getCommentID() {
        return commentID;
    }

    /**
     * @param commentID The commentID to set.
     */
    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    /**
     * @return The commentTimestamp.
     */
    public Timestamp getCommentTimestamp() {
        return commentTimestamp;
    }

    /**
     * @param commentTimestamp The commentTimestamp to set.
     */
    public void setCommentTimestamp(Timestamp commentTimestamp) {
        this.commentTimestamp = commentTimestamp;
    }

    /**
     * @return The karma.
     */
    public int getKarma() {
        int karma = 0;
        ArrayList<VotingActiveRecord> votings = VotingActiveRecordFactory.findVoteByComment(commentID);
        for(int i = 0; i< votings.size(); i++)
        {
            if(votings.get(i).isUpvote())
            {
                karma++;
            }
            else
            {
                karma--; 
            }
        } 
       return karma;
    }

    /**
     * @return The content.
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The relatedPost.
     */
    public int getRelatedPost() {
        return relatedPost;
    }

    /**
     * @param relatedPost The relatedPost to set.
     */
    public void setRelatedPost(int relatedPost) {
        this.relatedPost = relatedPost;
    }

    /**
     * @return The publishingUser.
     */
    public int getPublishingUser() {
        return publishingUser;
    }

    /**
     * @param publishingUser The publishingUser to set.
     */
    public void setPublishingUser(int publishingUser) {
        this.publishingUser = publishingUser;
    }

    /**
     * @return The publishingPage.
     */
    public int getPublishingPage() {
        return publishingPage;
    }

    /**
     * @param publishingPage The publishingPage to set.
     */
    public void setPublishingPage(int publishingPage) {
        this.publishingPage = publishingPage;
    }
    
    /**
     * @return The publisherName.
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * @param publisherName The publisherName to set.
     */
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    /**
     * @return The voteRecord.
     */
    public VotingActiveRecord getVoteRecord() {
        return voteRecord;
    }

    /**
     * @param voteRecord The voteRecord to set.
     */
    public void setVoteRecord(VotingActiveRecord voteRecord) {
        this.voteRecord = voteRecord;
    }

    /**
     * @return The upvote flag.
     */
    public boolean isUpvote() {
        return voteRecord.isUpvote();
    }

    /**
     * @return The voted flag.
     */
    public boolean isVoted() {
        return voteRecord != null;
    }
}
