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
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class represents a row of the Post table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class PostActiveRecord extends DatabaseUtility{
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into Post(PostTimestamp, PostPublic, Content, PublishingUser, PublishingPage)" +
            " Values (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private static final String UPDATE =
            " Update Post" + 
            " Set PostTimestamp = ?, PostPublic = ?, Content = ?, PublishingUser = ?, PublishingPage = ?";
    
    /**
     * This String contains the part of the SQL command selecting the PostID from the table.
     */
    private static final String SELECT_ID =
            " Select PostID" +
            " from Post";
    
    /**
     * This String contains the one part of the SQL command to specify a set of unioned SELECT satements.
     */
    private static final String SELECT =
            " Select *" +
            " From (";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private static final String DELETE =
            " Delete" +
            " from Post";
    
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
     * This String contains the part of the SQL command reducing the selection to a specific postID.
     */
    private static final String BY_ID =
            " Where PostID=?";
    
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
     * This String contains the part of the SQL command reducing the selection to a specific pageID.
     */
    private static final String BY_PAGEID_TIME_OLDER =
            " Where PublishingPage=?" +
            " And PostTimestamp<?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pageID.
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
     * This String contains the part of the SQL command putting the result into descending order according to the PostID.
     */
    private static final String ORDER_BY_ID_DESC =
            " Order by PostID desc";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the Timestamp.
     */
    private static final String ORDER_BY_TIME_DESC =
            " Order by PostTimestamp desc";
    
    
    private int postID;
    private Timestamp postTimestamp;
    private boolean postPublic;
    private String content;
    private int publishingUser = 0;
    private int publishingPage = 0;
    private String publisherName;
    private int commentCount;
    /**
     * Upvote shows if the current user has upvoted (true), downvoted (false).
     */
    private VotingActiveRecord voteRecord;
    
    public PostActiveRecord()
    {
        commentCount = 0;
    }
    
    /**
     * This counts all posts published on the network.
     * @return The number of following user.
     */
    public static int countPosts()
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
     * This function retrieves a post using its ID.
     * @param postID The postID of the Post.
     * @param userID The userID of the viewing user.
     * @return An array list with all posts matching the ID (should only one element in there since PostID is primary key).
     */
    public static ArrayList<PostActiveRecord> findPostByID(int postID, String userID)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_ID);
                stmt.setInt(1, postID);
                
                rs = stmt.executeQuery();
                while (rs.next())
                {
                    PostActiveRecord e;
                    if(userID != null)
                    {
                        e = createPost(rs, userID);
                    }
                    else
                    {
                        e = createPost(rs);
                    }
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
     * This function retrieves all followed and unread posts of a user.
     * @param userID The userID of the user.
     * @return An array list with all unread and followed posts.
     */
    public static ArrayList<PostActiveRecord> findAllUnreadPostsByUserID(int userID)
    {
        String viewingUser = "u" + userID;
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try
            {
                stmt = con.prepareStatement(SELECT_ALL + FROM_UNREAD_USER);
                stmt.setInt(1, userID);
                
                rs = stmt.executeQuery();
                while(rs.next())
                {
                    PostActiveRecord e = createPost(rs, viewingUser);
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
     * This function retrieves all followed and unread posts of a page.
     * @param pageID The pageID of the user.
     * @return An array list with all unread and followed posts.
     */
    public static ArrayList<PostActiveRecord> findAllUnreadPostsByPageID(int pageID)
    {
        String viewingUser = "f" + pageID;
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try
            {
                stmt = con.prepareStatement(SELECT_ALL + FROM_UNREAD_PAGE);
                stmt.setInt(1, pageID);
                
                rs = stmt.executeQuery();
                while(rs.next())
                {
                    PostActiveRecord e = createPost(rs, viewingUser);
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
     * This function counts all followed and unread posts of a user.
     * @param userID The userID of the user.
     * @return An array list with all unread and followed posts.
     */
    public static int countAllUnreadPostsByUserID(int userID)
    {
        String viewingUser = "u" + userID;
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try
            {
                stmt = con.prepareStatement(COUNT_ROWS + FROM_UNREAD_USER);
                stmt.setInt(1, userID);
                
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
     * This function retrieves all followed and unread posts of a page.
     * @param pageID The pageID of the user.
     * @return An array list with all unread and followed posts.
     */
    public static int countAllUnreadPostsByPageID(int pageID)
    {
        String viewingUser = "f" + pageID;
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            
            try
            {
                stmt = con.prepareStatement(COUNT_ROWS + FROM_UNREAD_PAGE);
                stmt.setInt(1, pageID);
                
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
     * This function retrieves all posts posted by a specific user. Checking if the current user is allowed to view the posts and filter according to the restrictions.
     * @param userID The userID of the user.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user who is viewing the posts.
     * @return An array list with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostByUserIDAndAmount(int userID, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                if(viewingUser.startsWith("u"))
                {
                    if(AllFriendsActiveRecord.isFriendWith(userID, Integer.valueOf(viewingUser.substring(1))))
                    {
                        stmt = con.prepareStatement(SELECT_ALL + BY_USERID + ORDER_BY_TIME_DESC);
                    }
                    else
                    {   
                        stmt = con.prepareStatement(SELECT_ALL + BY_USERID + AND_PUBLIC + ORDER_BY_TIME_DESC);
                    }
                    stmt.setInt(1, userID);

                    rs = stmt.executeQuery();
                    int i = 0;
                    while (rs.next() && i<amountOfPost)
                    {
                        PostActiveRecord e = createPost(rs, viewingUser);
                        recs.add(e);
                        i++;
                    }

                    rs.close();
                    stmt.close();
                }
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
     * This function retrieves all posts posted by a specific user older than a specific time.
     * @param userID The userID of the fanpage.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the posts.
     * @return An array list with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findOlderPostByUserIDAndAmountAndTime(int userID, Timestamp time, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                if(viewingUser.startsWith("u"))
                {
                    if(AllFriendsActiveRecord.isFriendWith(userID, Integer.valueOf(viewingUser.substring(1))))
                    {
                        stmt = con.prepareStatement(SELECT_ALL + BY_USERID_TIME_OLDER + ORDER_BY_TIME_DESC);
                    }
                    else
                    {
                        stmt = con.prepareStatement(SELECT_ALL + BY_USERID_TIME_OLDER + AND_PUBLIC + ORDER_BY_TIME_DESC);
                    }
                    stmt.setInt(1, userID);
                    stmt.setTimestamp(2, time);

                    rs = stmt.executeQuery();
                    int i = 0;
                    while (rs.next() && i<amountOfPost)
                    {
                        PostActiveRecord e = createPost(rs, viewingUser);
                        recs.add(e);
                        i++;
                    }

                    rs.close();
                    stmt.close();
                }
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
     * This function retrieves all posts posted by a specific fanpage.
     * @param pageID The pageID of the fanpage.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the post.
     * @return An array list with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostByPageIDAndAmount(int pageID, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_PAGEID + ORDER_BY_TIME_DESC);
                stmt.setInt(1, pageID);
                
                rs = stmt.executeQuery();
                int i = 0;
                while (rs.next() && i<amountOfPost)
                {
                    PostActiveRecord e = createPost(rs, viewingUser);
                    recs.add(e);
                    i++;
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
     * This function retrieves all posts posted by a specific fanpage older than a specific time.
     * @param pageID The pageID of the fanpage.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost Reduces the set to the given amount.
     * @param viewingUser The user viewing the post.
     * @return An array list with all posts published by the fanpage, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findOlderPostByPageIDAndAmountAndTime(int pageID, Timestamp time, int amountOfPost, String viewingUser)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT_ALL + BY_PAGEID_TIME_OLDER + ORDER_BY_TIME_DESC);
                stmt.setInt(1, pageID);
                stmt.setTimestamp(2, time);
                
                rs = stmt.executeQuery();
                int i = 0;
                while (rs.next() && i<amountOfPost)
                {
                    PostActiveRecord e = createPost(rs, viewingUser);
                    recs.add(e);
                    i++;
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
     * This function retrieves all posts posted by friends and followed pages.
     * @param userID The userID of the current user.
     * @param amountOfPost Reduces the set to the given amount.
     * @return An array list with all posts published by friends and followed fanpages, sorted by their timestamp.
     */
    public static ArrayList<PostActiveRecord> findAllPostOfFriendsAndPagesByAmount(int userID, int amountOfPost)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement("(" + SELECT_ALL_POST + FROM_FRIENDS + AND_CURRENTUSER + UNION + SELECT_ALL_POST + FROM_PAGES + ")" + ORDER_BY_TIME_DESC);
                stmt.setInt(1, userID);
                stmt.setInt(2, userID);
                stmt.setInt(3, userID);
                
                rs = stmt.executeQuery();
                int i = 0;
                while (rs.next() && i<amountOfPost)
                {
                    PostActiveRecord e;
                    if(userID != 0)
                    {
                        e = createPost(rs, "u" + userID);
                    }
                    else
                    {
                        e = createPost(rs);
                    }
                    recs.add(e);
                    i++;
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
     * This function retrives a specific amount of posts older than a specific date from all friends and followed pages of a specific user.
     * @param userID The userID of the viewing user.
     * @param time The time which is going to compared ot all items.
     * @param amountOfPost The amount of posts the result is reduced to.
     * @return The number of posts defined by a number older than the defined time from all friends and followed pages of a defined user.
     */
    public static ArrayList<PostActiveRecord> findOlderPostOfFriendsAndPagesByTimeAndAmount(int userID, Timestamp time, int amountOfPost)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT + SELECT_ALL_POST + FROM_FRIENDS + AND_CURRENTUSER + UNION + SELECT_ALL_POST + FROM_PAGES + AS + BY_TIME_OLDER + ORDER_BY_TIME_DESC);

                stmt.setInt(1, userID);
                stmt.setInt(2, userID);
                stmt.setInt(3, userID);
                stmt.setTimestamp(4, time);
                rs = stmt.executeQuery();
                int i = 0;
                while (rs.next() && i<amountOfPost)
                {
                    PostActiveRecord e;
                    if(userID != 0)
                    {
                        e = createPost(rs, "u" + userID);
                    }
                    else
                    {
                        e = createPost(rs);
                    }
                    recs.add(e);
                    i++;
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
     * This function retrives a specific amount of posts newer than a specific date from all friends and followed pages of a specific user.
     * @param userID The userID of the viewing user.
     * @param time The time which is going to compared ot all items.
     * @return The number of posts defined by a number older than the defined time from all friends and followed pages of a defined user.
     */
    public static ArrayList<PostActiveRecord> findNewerPostOfFriendsAndPagesByTime(int userID, Timestamp time)
    {
        ArrayList<PostActiveRecord> recs = new ArrayList<PostActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try
            {
                stmt = con.prepareStatement(SELECT + SELECT_ALL_POST + FROM_FRIENDS + UNION + SELECT_ALL_POST + FROM_PAGES + AS + BY_TIME_NEWER + ORDER_BY_TIME_DESC);

                stmt.setInt(1, userID);
                stmt.setInt(2, userID);
                stmt.setTimestamp(3, time);
                rs = stmt.executeQuery();
                while (rs.next())
                {
                    PostActiveRecord e;
                    if(userID != 0)
                    {
                        e = createPost(rs, "f" + userID);
                    }
                    else
                    {
                        e = createPost(rs);
                    }
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
     * This function creates a new post using the data from the current position of the result set.
     * @param rs The data source for the new fanpage.
     * @return The new created post.
     */
    protected static PostActiveRecord createPost(ResultSet rs)
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
                d.setPublisherName(FanpageActiveRecord.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecord.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            d.setCommentCount(CommentActiveRecord.countCommentsByPostID(d.getPostID()));
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
     * This function creates a new post using the data from the current position of the result set and the viewing user.
     * @param rs The data source for the new fanpage.
     * @param userID The viewing user.
     * @return The new created post.
     */
    protected static PostActiveRecord createPost(ResultSet rs, String userID)
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
                d.setPublisherName(FanpageActiveRecord.findPageByID(d.getPublishingPage()).get(0).getDisplayName());
            }
            else if(d.getPublishingUser()!=0)
            {
                d.setPublisherName(NormalUserActiveRecord.findUserByID(d.getPublishingUser()).get(0).getDisplayName());
            }
            d.setCommentCount(CommentActiveRecord.findCommentByPostID(d.getPostID()).size());
            ArrayList<VotingActiveRecord> temp;
            if(userID.startsWith("u"))
            {
                temp = VotingActiveRecord.findVoteByUserAndPost(Integer.valueOf(userID.substring(1)), d.getPostID());
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
                temp = VotingActiveRecord.findVoteByPageAndPost(Integer.valueOf(userID.substring(1)), d.getPostID());
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
        boolean success = false;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try
            {
                stmt = con.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS);
                
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
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                rs = stmt.getGeneratedKeys();
                rs.next();
                postID = rs.getInt(1);
                stmt.close();
                
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
     * Updates the row with the postID of the object with the data within this object.
     * @param notify True if the following user should be notified about the change.
     * @return True if update was successful, false otherwise.
     */
    public boolean update(boolean notify)
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
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                stmt.close();
                
                if(notify)
                {
                    FfollowsPActiveRecord.notify(postID);
                    UfollowsPActiveRecord.notify(postID);
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
                ArrayList<CommentActiveRecord> comments = CommentActiveRecord.findCommentByPostID(postID);
                for(int i = 0; i<comments.size() && success; i++)
                {
                    success = comments.get(i).remove();
                }
                ArrayList<VotingActiveRecord> votings = VotingActiveRecord.findVoteByPost(postID);
                for(int i = 0; i<votings.size() && success; i++)
                {
                    success = votings.get(i).remove();
                }  
                ArrayList<UfollowsPActiveRecord> uFollowsP = UfollowsPActiveRecord.findUfollowsPByPostID(postID);
                for(int i = 0; i<uFollowsP.size() && success; i++)
                {
                    success = uFollowsP.get(i).remove();
                }
                ArrayList<FfollowsPActiveRecord> fFollowsP = FfollowsPActiveRecord.findFfollowsPByPostID(postID);
                for(int i = 0; i<fFollowsP.size() && success; i++)
                {
                    success = fFollowsP.get(i).remove();
                }
                
                if(success)
                {
                    stmt = con.prepareStatement(DELETE + BY_ID);

                    stmt.setInt(1, postID);

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
        ArrayList<VotingActiveRecord> votings = VotingActiveRecord.findVoteByPost(postID);
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
