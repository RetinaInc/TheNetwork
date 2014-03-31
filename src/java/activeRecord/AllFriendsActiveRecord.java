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
import java.util.ArrayList;

/**
 * This class represents a row of the AllFriends view and provides functions to check if two users are friends or not.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class AllFriendsActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the view.
     */
    private static String SELECT_ALL =
            " Select * " +
            " From AllFriends ";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific pair of users.
     */
    private static String BY_USERID_FRIENDID =
            " Where CurrentUser = ?" + 
            " And Friend = ?";
    
    private int currentUser;
    private int friend;
    private boolean accepted;
    private boolean rejected;
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<AllFriendsActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<AllFriendsActiveRecord> recs = new ArrayList<AllFriendsActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            ResultSet rs = null;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    AllFriendsActiveRecord e = createFriendsRecord(rs);
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
     * This function retrieves the row representing the friendship between two user.
     * @param currentUserID The userID of one of the users.
     * @param friend The userID of one of the users.
     * @return An array list with connections between the two users.
     */
    public static ArrayList<AllFriendsActiveRecord> findAllFriendsByIDOfFriendAndUser(int currentUserID, int friend)
    {
        ArrayList<AllFriendsActiveRecord> recs = new ArrayList<AllFriendsActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(SELECT_ALL + BY_USERID_FRIENDID);
            stmt.setInt(1, currentUserID);
            stmt.setInt(2, friend);
            
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**^
     * This function creates a new active record representing a row of the view set using the data from the current position of the result set.
     * @param rs The data source for the active record.
     * @return The new created active record.
     */
    protected static AllFriendsActiveRecord createFriendsRecord(ResultSet rs)
    {
        AllFriendsActiveRecord d = new AllFriendsActiveRecord();
        try
        {
            d.setAccepted(rs.getBoolean("Accepted"));
            d.setFriend(rs.getInt("Friend"));
            d.setCurrentUser(rs.getInt("CurrentUser"));
            d.setRejected(rs.getBoolean("Rejected"));
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
     * @return The currentUser.
     */
    public int getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser The currentUser to set.
     */
    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return The friend.
     */
    public int getFriend() {
        return friend;
    }

    /**
     * @param friend The friend to set.
     */
    public void setFriend(int friend) {
        this.friend = friend;
    }

    /**
     * @return The accepted flag.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * @param accepted The accepted flag to set.
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    /**
     * @return The rejected flag.
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * @param rejected The rejected flag to set.
     */
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }
    
    /**
     * This function checks if two user are friends.
     * @param user The current user.
     * @param friend The user which is checked to be a friend.
     * @return True if the two users are friends.
     */
    public static boolean isFriendWith(int user, int friend)
    {
        ArrayList<AllFriendsActiveRecord> list = findAllFriendsByIDOfFriendAndUser(user, friend);
        if(list.size() == 1)
        {
            return list.get(0).accepted;
        }
        else
        {
            return (user == friend);
        }
    }
    
    /**
     * This function checks if there is a rejected friend request between two user .
     * @param user The current user.
     * @param friend The other user.
     * @return True if the two users have a rejected friend request.
     */
    public static boolean hasRejectedRequest(int user, int friend)
    {
        ArrayList<AllFriendsActiveRecord> list = findAllFriendsByIDOfFriendAndUser(user, friend);
        if(list.size() == 1)
        {
            return list.get(0).rejected;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * This function checks if there is an open friend request between two users.
     * @param user The current user.
     * @param friend The user which is checked to be a friend.
     * @return True if the two users are friends.
     */
    public static boolean openFriendshipRequest(int user, int friend)
    {
        ArrayList<AllFriendsActiveRecord> list = findAllFriendsByIDOfFriendAndUser(user, friend);
        if(list.size() == 1)
        {
            return ((list.get(0).accepted ==false)&&(list.get(0).rejected == false));
        }
        else
        {
            return false;
        }
    }
}
