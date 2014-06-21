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
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class retrieves active records of the Post table from the database.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class PostActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the one part of the SQL command to specify a set of unioned SELECT satements.
     */
    private static final String SELECT =
            " Select *" +
            " From (";
    
    /**
     * This String contains the other part of the SQL command to specify a set of unioned SELECT satements.
     */
    private static final String AS =
            " ) As tmp";
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            " Select *" +
            " from Post";
    
    /**
     * This String contains the part of the SQL command selecting the columns provided by the post table from the table.
     */
    private static final String SELECT_ALL_POST =
            " Select Post.*" +
            " from Post";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all posts provided by friends.
     */
    private static final String FROM_FRIENDS =
            " Inner Join AllFriends" +
            " On Post.PublishingUser = AllFriends.Friend" + 
            " Where AllFriends.CurrentUser = ? AND AllFriends.Accepted = True";
    
    /**
     * This String contains the part of the SQL command expanding the selection to the current user's posts.
     */
    private static final String AND_CURRENTUSER =
            " Union" +
            " Select * from Post" +
            " Where PublishingUser = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all posts provided by followed fanpages.
     */
    private static final String FROM_PAGES =
            " Inner Join UfollowsF" +
            " On Post.PublishingPage = UfollowsF.FollowedFanpage" + 
            " Where UfollowsF.FollowingUser = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all followed posts of a user which are unread.
     */
    private static final String FROM_UNREAD_USER =
            " Inner Join UfollowsP" +
            " On Post.PostID = UfollowsP.FollowedPost" + 
            " Where UfollowsP.FollowingUser = ? And PostRead = false";
    
    /**
     * This String contains the part of the SQL command counting the number of rows.
     */
    private static final String COUNT_ROWS =
            " Select count(*) as Number" + 
            " From Post";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all followed posts of a user which are unread.
     */
    private static final String FROM_UNREAD_PAGE =
            " Inner Join FfollowsP" +
            " On Post.PostID = FfollowsP.FollowedPost" + 
            " Where FfollowsP.FollowingFanpage = ? And PostRead = false";
    
    /**
     * This String provides the part of the SQL command performaing an union.
     */
    private static final String UNION =
            " Union All";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific timeframe older than the given time.
     */
    private static final String BY_TIME_OLDER =
            " Where PostTimestamp<?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific timeframe newer than the given time.
     */
    private static final String BY_TIME_NEWER =
            " Where PostTimestamp>?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific postID.
     */
    private static final String BY_ID =
            " Where PostID=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pageID.
     */
    private static final String BY_PAGEID =
            " Where PublishingPage=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_USERID =
            " Where PublishingUser=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pageID and timeframe.
     */
    private static final String BY_PAGEID_TIME_OLDER =
            " Where PublishingPage=?" +
            " And PostTimestamp<?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID and timeframe.
     */
    private static final String BY_USERID_TIME_OLDER =
            " Where PublishingUser=?" +
            " And PostTimestamp<?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all public posts.
     */
    private static final String AND_PUBLIC =
            " And PostPublic = true";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the Timestamp.
     */
    private static final String ORDER_BY_TIME_DESC =
            " Order by PostTimestamp desc";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<PostActiveRecord> executeQuery(PreparedStatement stmt, String viewingUser)
    {
        return executeQuery(stmt, 0, viewingUser);
    }
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @param amount The maximum amount of items in the resulting array, 0 if amount is infinite.
     * @param viewingUser The viewing user, null if the user is undefined.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<PostActiveRecord> executeQuery(PreparedStatement stmt, int amount, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
                rs = stmt.executeQuery();
                
                if(amount == 0)
                {
                    while (rs.next())
                    {
                        PostActiveRecord e = createPost(rs, viewingUser);
                        recs.add(e);
                    }
                }
                else
                {
                    for(int i = 0; i < amount && rs.next(); i++)
                    {
                        PostActiveRecord e = createPost(rs, viewingUser);
                        recs.add(e);
                    }
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
     * This function counts all posts published on the network.
     * @return The number of published posts.
     */
    public static int countPosts()
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
     * This function counts all followed and unread posts of a user.
     * @param userID The userID of the user.
     * @return The number of unread and followed post.
     */
    public static int countAllUnreadPostsByUserID(int userID)
    {
        int result;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(COUNT_ROWS + FROM_UNREAD_USER);
            stmt.setInt(1, userID);
                
            result = executeCount(stmt);
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This function retrieves all followed and unread posts of a page.
     * @param pageID The pageID of the fanpage.
     * @return The number of unread and followed post.
     */
    public static int countAllUnreadPostsByPageID(int pageID)
    {
        int result;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(COUNT_ROWS + FROM_UNREAD_PAGE);
            stmt.setInt(1, pageID);
                
            result = executeCount(stmt);
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This function retrieves a post using its ID.
     * @param postID The postID of the Post.
     * @param userID The userID of the viewing user.
     * @return An ArrayList with all posts matching the ID (should only one element in there since PostID is primary key).
     */
    public static ArrayList<PostActiveRecord> findPostByID(int postID, String userID)
    {
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_ID);
            stmt.setInt(1, postID);
                
            recs = executeQuery(stmt, userID);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all followed and unread posts of a user.
     * @param userID The userID of the user.
     * @return An ArrayList with all unread and followed posts.
     */
    public static ArrayList<PostActiveRecord> findAllUnreadPostsByUserID(int userID)
    {
        String viewingUser = "u" + userID;
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + FROM_UNREAD_USER);
            stmt.setInt(1, userID);
                
            recs = executeQuery(stmt, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all followed and unread posts of a page.
     * @param pageID The pageID of the fanpage.
     * @return An ArrayList with all unread and followed posts.
     */
    public static ArrayList<PostActiveRecord> findAllUnreadPostsByPageID(int pageID)
    {
        String viewingUser = "f" + pageID;
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + FROM_UNREAD_PAGE);
            stmt.setInt(1, pageID);
                
            recs = executeQuery(stmt, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all posts posted by a specific user. Checking if the current user is allowed to view the posts and filter according to the restrictions.
     * @param userID The userID of the user.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user who is viewing the posts.
     * @return An ArrayList with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostByUserIDAndAmount(int userID, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt;

            if(viewingUser.startsWith("u"))
            {
                if(AllFriendsActiveRecordFactory.isFriendWith(userID, Integer.valueOf(viewingUser.substring(1))))
                {
                    stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID + ORDER_BY_TIME_DESC);
                }
                else
                {   
                    stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID + AND_PUBLIC + ORDER_BY_TIME_DESC);
                }
                stmt.setInt(1, userID);

                recs = executeQuery(stmt, amountOfPost, viewingUser);
            }
            else
            {
                recs = new ArrayList<>();
            }
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all posts posted by a specific user older than a specific time.
     * @param userID The userID of the fanpage.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the posts.
     * @return An ArrayList with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findOlderPostByUserIDAndAmountAndTime(int userID, Timestamp time, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt;
            
            if(viewingUser.startsWith("u"))
            {
                if(AllFriendsActiveRecordFactory.isFriendWith(userID, Integer.valueOf(viewingUser.substring(1))))
                {
                    stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID_TIME_OLDER + ORDER_BY_TIME_DESC);
                }
                else
                {
                    stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_USERID_TIME_OLDER + AND_PUBLIC + ORDER_BY_TIME_DESC);
                }
                stmt.setInt(1, userID);
                stmt.setTimestamp(2, time);

                recs = executeQuery(stmt, amountOfPost, viewingUser);
            }
            else
            {
                recs = new ArrayList<>();
            }
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all posts posted by a specific fanpage.
     * @param pageID The pageID of the fanpage.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the post.
     * @return An ArrayList with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostByPageIDAndAmount(int pageID, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt  = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_PAGEID + ORDER_BY_TIME_DESC);
            stmt.setInt(1, pageID);
                
            recs = executeQuery(stmt, amountOfPost, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all posts posted by a specific fanpage older than a specific time.
     * @param pageID The pageID of the fanpage.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the post.
     * @return An ArrayList with all posts published by the fanpage after the given time, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findOlderPostByPageIDAndAmountAndTime(int pageID, Timestamp time, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_PAGEID_TIME_OLDER + ORDER_BY_TIME_DESC);
            stmt.setInt(1, pageID);
            stmt.setTimestamp(2, time);
                
            recs = executeQuery(stmt, amountOfPost, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all posts posted by friends and followed pages.
     * @param userID The userID of the current user.
     * @param amountOfPost Reduces the set to the given amount.
     * @return An ArrayList with all posts published by friends and followed fanpages, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostOfFriendsAndPagesByAmount(int userID, int amountOfPost)
    {
        String viewingUser = "u" + userID;
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement("(" + SELECT_ALL_POST + FROM_FRIENDS + AND_CURRENTUSER + UNION + SELECT_ALL_POST + FROM_PAGES + ")" + ORDER_BY_TIME_DESC);
            stmt.setInt(1, userID);
            stmt.setInt(2, userID);
            stmt.setInt(3, userID);
                
            recs = executeQuery(stmt, amountOfPost, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrives a specific amount of posts older than a specific date from all friends and followed pages of a specific user.
     * @param userID The userID of the viewing user.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost The amount of posts the result is reduced to.
     * @return The number of posts defined by a number older than the defined time from all friends and followed pages of a defined user.
     */
    public static ArrayList<PostActiveRecord> findOlderPostOfFriendsAndPagesByTimeAndAmount(int userID, Timestamp time, int amountOfPost)
    {
        String viewingUser = "u" + userID;
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT + SELECT_ALL_POST + FROM_FRIENDS + AND_CURRENTUSER + UNION + SELECT_ALL_POST + FROM_PAGES + AS + BY_TIME_OLDER + ORDER_BY_TIME_DESC);
            stmt.setInt(1, userID);
            stmt.setInt(2, userID);
            stmt.setInt(3, userID);
            stmt.setTimestamp(4, time);
            
            recs = executeQuery(stmt, amountOfPost, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrives a specific amount of posts newer than a specific date from all friends and followed pages of a specific user.
     * @param userID The userID of the viewing user.
     * @param time The time which is going to compared ot all items.
     * @return The number of posts defined by a number newer than the defined time from all friends and followed pages of a defined user.
     */
    public static ArrayList<PostActiveRecord> findNewerPostOfFriendsAndPagesByTime(int userID, Timestamp time)
    {
        String viewingUser = "u" + userID;
        ArrayList<PostActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT + SELECT_ALL_POST + FROM_FRIENDS + UNION + SELECT_ALL_POST + FROM_PAGES + AS + BY_TIME_NEWER + ORDER_BY_TIME_DESC);
            stmt.setInt(1, userID);
            stmt.setInt(2, userID);
            stmt.setTimestamp(3, time);
            
            recs = executeQuery(stmt, userID, viewingUser);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function creates a new post using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created post.
     */
    protected static PostActiveRecord createPost(ResultSet rs)
    {
        return createPost(rs, null);
    }  
    
     /**
     * This function creates a new post using the data from the current position of the result set and the viewing user.
     * @param rs The data source for the new fanpage.
     * @param userID The viewing user.
     * @return The new created post.
     */
    private static PostActiveRecord createPost(ResultSet rs, String userID)
    {
        PostActiveRecord d = new PostActiveRecord();
        try
        {   
            d.setPostID(rs.getInt("PostID"));
            d.setPostTimestamp(rs.getTimestamp("PostTimestamp"));
            d.setPostPublic(rs.getBoolean("PostPublic"));
            d.setContent(rs.getString("Content"));
            d.setPublishingUser(rs.getInt("PublishingUser"));
            d.setPublishingPage(rs.getInt("PublishingPage"));
            if(d.getPublishingPage()!=0)
            {
                d.setPublisherName(FanpageActiveRecordFactory.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecordFactory.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            d.setCommentCount(CommentActiveRecordFactory.findCommentByPostID(d.getPostID()).size());
            
            if(userID == null)
            {
                d.setVoteRecord(null);
            }
            else
            {
                ArrayList<VotingActiveRecord> temp;
                if(userID.startsWith("u"))
                {
                    temp = VotingActiveRecordFactory.findVoteByUserAndPost(Integer.valueOf(userID.substring(1)), d.getPostID());
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
                    temp = VotingActiveRecordFactory.findVoteByPageAndPost(Integer.valueOf(userID.substring(1)), d.getPostID());
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
