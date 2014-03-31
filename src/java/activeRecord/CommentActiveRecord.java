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
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class represents a row of the Comment table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class CommentActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into Comment(CommentTimestamp, Content, RelatedPost, PublishingUser, PublishingPage)" +
            " Values (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private static final String UPDATE =
            " Update Post" + 
            " Set PostTimestamp = ?, Content = ?, RelatedPost = ?, PublishingUser = ?, PublishingPage = ?";
    
    /**
     * This String contains the part of the SQL command selecting the CommentID from the table.
     */
    private static final String SELECT_ID =
            " Select CommentID" +
            " from Comment";
    
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
     * This String contains the part of the SQL command deleting the row.
     */
    private static final String DELETE =
            " Delete" +
            " from Comment";
    
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
     * This String contains the part of the SQL command putting the result into descending order according to the CommentID.
     */
    private static final String ORDER_BY_ID_DESC =
            " Order by CommentID desc";
    
    /**
     * This String contains the part of the SQL command putting the result into ascending order according to the Timestamp.
     */
    private static final String ORDER_BY_TIME_DESC =
            " Order by PostTimestamp asc";
    
    
    private int commentID;
    private Timestamp commentTimestamp;
    private String content;
    private int relatedPost;
    private int publishingUser = 0;
    private int publishingPage = 0;
    private String publisherName;
    private VotingActiveRecord voteRecord;
    
    /**
     * This counts all fanpages subscibed to the network.
     * @return The number of following user.
     */
    public static int countComments()
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(COUNT_ROWS);
                
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
     * This counts all comments of a post subscibed to the network.
     * @param postID The ID of the post.
     * @return The number of following user.
     */
    public static int countCommentsByPostID(int postID)
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(COUNT_ROWS + BY_POSTID);
                stmt.setInt(1, postID);
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
     * This function retrieves all comments matching the provided postID from the database.
     * @param postID The postID of the searched comments.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByPostID(int postID)
    {
        ArrayList<CommentActiveRecord> recs = new ArrayList<CommentActiveRecord>();
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
                    CommentActiveRecord e = createComment(rs);
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
     * This function retrieves all comments matching the provided postID from the database using the viewing user.
     * @param postID The postID of the searched comments.
     * @param user The viewing user.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByPostID(int postID, String user)
    {
        ArrayList<CommentActiveRecord> recs = new ArrayList<CommentActiveRecord>();
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
                    CommentActiveRecord e = createComment(rs, user);
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
     * This function retrieves all comments matching the provided commentID from the database using the viewing user.
     * @param commentID The commentID of the searched comments.
     * @param user The viewing user.
     * @return An array list with all comments matching the postID.
     */
    public static ArrayList<CommentActiveRecord> findCommentByCommentID(int commentID, String user)
    {
        ArrayList<CommentActiveRecord> recs = new ArrayList<CommentActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_ID);
                stmt.setInt(1, commentID);
                
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    CommentActiveRecord e = createComment(rs, user);
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
     * This function creates a new comment using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created comment.
     */
    protected static CommentActiveRecord createComment(ResultSet rs)
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
                d.setPublisherName(FanpageActiveRecord.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecord.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            d.setVoteRecord(null);
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
     * This function creates a new comment using the data from the current position of the result set and the viewing user.
     * @param rs The data source for the new fanpage.
     * @param userID The viewing user.
     * @return The new created post.
     */
    protected static CommentActiveRecord createComment(ResultSet rs, String userID)
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
                d.setPublisherName(FanpageActiveRecord.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecord.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            ArrayList<VotingActiveRecord> temp;
            if(userID.startsWith("u"))
            {
                temp = VotingActiveRecord.findVoteByUserAndComment(Integer.valueOf(userID.substring(1)), d.getCommentID());
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
                temp = VotingActiveRecord.findVoteByPageAndComment(Integer.valueOf(userID.substring(1)), d.getCommentID());
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
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
                stmt = con.prepareStatement(INSERT_INTO);
                
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
                success = true;
                ArrayList<VotingActiveRecord> votings = VotingActiveRecord.findVoteByComment(commentID);
                for(int i = 0; i<votings.size() && success; i++)
                {
                    success = votings.get(i).remove();
                }
                
                if(success)
                {
                    stmt = con.prepareStatement(DELETE + BY_ID);

                    stmt.setInt(1, commentID);

                    if(stmt.executeUpdate()==0)
                    {
                        success = false;
                    }

                    stmt.close();
                }
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
     * Updates the row with the commentID of the object with the data within this object.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
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
        ArrayList<VotingActiveRecord> votings = VotingActiveRecord.findVoteByComment(commentID);
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
