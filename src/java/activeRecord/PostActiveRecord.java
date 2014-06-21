/*
 * Copyright (C) 2014 Frank Steiler <frank.steiler@steilerdev.de>
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class represents a row of the Post table providing functions to insert, update and delete these rows.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class PostActiveRecord extends DatabaseUtility {
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into Post(PostTimestamp, PostPublic, Content, PublishingUser, PublishingPage)" +
            " Values (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update Post" + 
            " Set PostTimestamp = ?, PostPublic = ?, Content = ?, PublishingUser = ?, PublishingPage = ?";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private final String DELETE =
            " Delete" +
            " from Post";
       
    /**
     * This String contains the part of the SQL command reducing the selection to a specific postID.
     */
    private final String BY_ID =
            " Where PostID=?";
    
    private int postID;
    private Timestamp postTimestamp;
    private boolean postPublic;
    private String content;
    private int publishingUser = 0;
    private int publishingPage = 0;
    private String publisherName;
    private int commentCount;
    private VotingActiveRecord voteRecord;
    
    /**
     * Create a new post active record. A new record does not have any comments.
     */
    public PostActiveRecord()
    {
        commentCount = 0;
    }
    
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
            voteRecord.setVotedPost(postID);
            
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
            voteRecord.setVotedPost(postID);
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
     * This function inserts the object into the database and sets the author of the post as following user.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt  = getDatabaseConnection().prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS);
                
            stmt.setBoolean(1, postPublic);
            stmt.setString(2, content);
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
                
            if(publishingUser != 0)
            {
                UfollowsPActiveRecord followPost = new UfollowsPActiveRecord();
                followPost.setFollowedPost(postID);
                followPost.setPostRead(true);
                followPost.setFollowingUser(publishingUser);
                success = followPost.insert();
            } 
            else if(publishingPage != 0)
            {
                FfollowsPActiveRecord followPost = new FfollowsPActiveRecord();
                followPost.setFollowedPost(postID);
                followPost.setPostRead(true);
                followPost.setFollowingFanpage(publishingPage);
                success = followPost.insert();
            }
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * Updates the row with the postID of the object with the data within this object.
     * @param notify True if the following user should be notified about the change.
     * @return True if update was successful, false otherwise.
     */
    public boolean update(boolean notify)
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE + BY_ID);
                
            stmt.setTimestamp(1, postTimestamp);
            stmt.setBoolean(2, postPublic);
            stmt.setString(3, content);
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
            stmt.setInt(6, postID);
                
            success = executeUpdate(stmt);
                
            if(notify)
            {
                FfollowsPActiveRecordFactory.notify(postID);
                UfollowsPActiveRecordFactory.notify(postID);
            }
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
        boolean success = true;
        try
        {   
            ArrayList<CommentActiveRecord> comments = CommentActiveRecordFactory.findCommentByPostID(postID);
            for(int i = 0; i<comments.size() && success; i++)
            {
                success = comments.get(i).remove();
            }
            ArrayList<VotingActiveRecord> votings = VotingActiveRecordFactory.findVoteByPost(postID);
            for(int i = 0; i<votings.size() && success; i++)
            {
                success = votings.get(i).remove();
            }  
            ArrayList<UfollowsPActiveRecord> uFollowsP = UfollowsPActiveRecordFactory.findUfollowsPByPostID(postID);
            for(int i = 0; i<uFollowsP.size() && success; i++)
            {
                success = uFollowsP.get(i).remove();
            }
            ArrayList<FfollowsPActiveRecord> fFollowsP = FfollowsPActiveRecordFactory.findFfollowsPByPostID(postID);
            for(int i = 0; i<fFollowsP.size() && success; i++)
            {
                success = fFollowsP.get(i).remove();
            }
                
            if(success)
            {
                PreparedStatement stmt = getDatabaseConnection().prepareStatement(DELETE + BY_ID);
                stmt.setInt(1, postID);

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
                
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs != null && rs.next())
                {
                    postID = rs.getInt(1);
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
     * @return The postID.
     */
    public int getPostID() {
        return postID;
    }

    /**
     * @param postID The postID to set.
     */
    public void setPostID(int postID) {
        this.postID = postID;
    }

    /**
     * @return The postTimestamp.
     */
    public Timestamp getPostTimestamp() {
        return postTimestamp;
    }

    /**
     * @param postTimestamp The postTimestamp to set.
     */
    public void setPostTimestamp(Timestamp postTimestamp) {
        this.postTimestamp = postTimestamp;
    }

    /**
     * @return The postPublic flag.
     */
    public boolean isPostPublic() {
        return postPublic;
    }

    /**
     * @param postPublic The postPublic flag to set.
     */
    public void setPostPublic(boolean postPublic) {
        this.postPublic = postPublic;
    }

    /**
     * @return The karma counter.
     */
    public int getKarma() {
        int karma = 0;
        ArrayList<VotingActiveRecord> votings = VotingActiveRecordFactory.findVoteByPost(postID);
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
     * @return The content of the post.
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
     * @return The commentCount.
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * @param commentCount The commentCount to set.
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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
    
    /**
     * @param vote The vote active record for the user for this post.
     */
    public void setVoteRecord(VotingActiveRecord vote)
    {
        this.voteRecord = vote;
    }
    
    /**
     * @return The vote active record for the user for this post.
     */
    public VotingActiveRecord getVoteRecord()
    {
        return voteRecord;
    }
}
