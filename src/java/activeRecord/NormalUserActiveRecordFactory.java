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
 * This class retrieves active records of the NormalUser table from the database.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class NormalUserActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            "Select * " +
            "from NormalUser";
    
    /**
     * This String contains the part of the SQL command selecting the columns provided by the normal user table from the table.
     */
    private static final String SELECT_ALL_USER =
            " Select NormalUser.*" +
            " from NormalUser";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all friends.
     */
    private static final String FROM_FRIENDS =
            " Inner Join AllFriends" +
            " On NormalUser.UserID = AllFriends.Friend" + 
            " Where AllFriends.CurrentUser = ? AND AllFriends.Accepted = True";
    
     /**
     * This String contains the part of the SQL command reducing the selection to all requesting users.
     */
    private static final String FROM_REQUESTING =
            " Inner Join AllFriends" +
            " On NormalUser.UserID = AllFriends.Friend" + 
            " Where AllFriends.CurrentUser = ? AND AllFriends.Accepted = False AND AllFriends.Rejected = False";
    
    /**
     * This String contains the part of the SQL command counting the number of rows.
     */
    private static final String COUNT_ROWS =
            " Select count(*) as Number" + 
            " From NormalUser";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all user after the specified user.
     */
    private static final String AND_AFTER_USER =
            " And NormalUser.DisplayName > ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to all user after the specified user.
     */
    private static final String AFTER_USER =
            " Where NormalUser.DisplayName > ?";
    
     /**
     * This String contains the part of the SQL command reducing the selection to a specific eMail.
     */
    private static final String BY_EMAIL =
            " Where Email=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ID =
            " Where UserID=?";
    
    /**
     * This String contains the part of the SQL command putting the result into ascending order according to the displaName.
     */
    private static final String ORDER_BY_DISPLAYNAME_ASC =
            " Order by DisplayName asc";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<NormalUserActiveRecord> executeQuery(PreparedStatement stmt)
    {
        return executeQuery(stmt, 0);
    }
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @param amount The maximum amount of items in the resulting array.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<NormalUserActiveRecord> executeQuery(PreparedStatement stmt, int amount)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs = null;

            try
            {               
                rs = stmt.executeQuery();
                
                if(amount == 0)
                {
                    while (rs.next())
                    {
                        NormalUserActiveRecord e = createNormalUser(rs);
                        recs.add(e);
                    }
                }
                else
                {
                    for(int i = 0; i < amount && rs.next(); i++)
                    {
                        NormalUserActiveRecord e = createNormalUser(rs);
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
     * This function counts all user subscibed to the network.
     * @return The number of user.
     */
    public static int countUser()
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
     * This function will count all open friend requests.
     * @param userID The ID of the viewing user.
     * @return The number of all open friend requests.
     */
    public static int countAllRequestingUser(int userID)
    {
        int result;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(COUNT_ROWS + FROM_REQUESTING);
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
     * This function will retrieve a list of all user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @return An ArrayList with all user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllUser(int amount)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + ORDER_BY_DISPLAYNAME_ASC);
            
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param lastUser The display name of the user where the selection will start.
     * @return An ArrayList with all user after a specific user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllUserAfter(int amount, String lastUser)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + AFTER_USER +  ORDER_BY_DISPLAYNAME_ASC);
            stmt.setString(1, lastUser);
            
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all friends of a user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @return An ArrayList with all friends of the user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllFriends(int userID, int amount)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL_USER + FROM_FRIENDS + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setInt(1, userID);
            
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all friends of a user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @param afterUser The display name of the user where the selection will start.
     * @return An ArrayList with all friends of the user after a specific user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllFriendsAfter(int userID, int amount, String afterUser)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL_USER + FROM_FRIENDS + AND_AFTER_USER + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setInt(1, userID);
            stmt.setString(2, afterUser);
            
            recs = executeQuery(stmt, amount);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function will retrieve a list of all users requesting a friendship with the user.
     * @param userID The ID of the viewing user.
     * @return An ArrayList with all friends of the user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllRequestingFriends(int userID)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL_USER + FROM_REQUESTING + ORDER_BY_DISPLAYNAME_ASC);
            stmt.setInt(1, userID);
            
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all normal users matching the provided eMail from the database.
     * @param eMail The Email of the searched normal user.
     * @return An ArrayList with all normal users matching the eMail.
     */
    public static ArrayList<NormalUserActiveRecord> findUserByEmail(String eMail)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_EMAIL);
            stmt.setString(1, eMail);
            
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves all users matching the provided UserID from the database.
     * @param userID The userID of the searched user.
     * @return An array list with all users matching the userID (Should only contain one element since UserID is primary key).
     */
    public static ArrayList<NormalUserActiveRecord> findUserByID(int userID)
    {
        ArrayList<NormalUserActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_ID);
            stmt.setInt(1, userID);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }

    /**
     * This function creates a new normal user active record using the data from the current position of the result set.
     * @param rs The data source for the new user.
     * @return The new created user.
     */
    protected static NormalUserActiveRecord createNormalUser(ResultSet rs)
    {
        NormalUserActiveRecord d = new NormalUserActiveRecord();
        try
        {
            d.setUserID(rs.getInt("UserID"));
            d.setDisplayName(rs.getString("DisplayName"));
            d.setFirstName(rs.getString("FirstName"));
            d.setLastName(rs.getString("LastName"));
            d.setDateOfBirth(rs.getDate("DateOfBirth"));
            d.setRelationshipStatus(rs.getString("RelationshipStatus"));
            d.setGender(rs.getString("Gender"));
            d.setEmail(rs.getString("Email"));
            d.setStreet(rs.getString("Street"));
            d.setHouseNr(rs.getInt("HouseNr"));
            d.setTown(rs.getString("Town"));
            d.setZip(rs.getString("Zip"));
            d.setPremium(rs.getBoolean("Premium"));
            d.setPassword(rs.getString("Password"));
            d.setSalt(rs.getString("Salt"));
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
