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
import java.sql.Date;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class represents a row of the NormalUser table providing functions to find, insert, update and delete these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class NormalUserActiveRecord extends DatabaseUtility{
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private static final String INSERT_INTO =
            "Insert into NormalUser(DisplayName, FirstName, LastName, DateOfBirth, RelationshipStatus, Gender, Email, Street, HouseNr, Town, Zip, Premium, Password, Salt)" +
            "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    /**
     * This String contains the part of the SQL command selecting the UserID from the table.
     */
    private static final String SELECT_ID =
            "Select UserID " +
            "from NormalUser";
    
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
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private static final String BY_ID =
            " Where UserID=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific eMail.
     */
    private static final String BY_EMAIL =
            " Where Email=?";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the UserID.
     */
    private static final String ORDER_BY_ID_DESC =
            " Order by UserID desc";
    
    /**
     * This String contains the part of the SQL command putting the result into descending order according to the displaName.
     */
    private static final String ORDER_BY_DISPLAYNAME_ASC =
            " Order by DisplayName asc";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private static final String UPDATE =
            " Update NormalUser" + 
            " Set DisplayName = ?, FirstName = ?, LastName = ?, DateOfBirth = ?, RelationshipStatus = ?, Gender = ?, Email = ?, Street = ?, HouseNr = ?, Town = ?, Zip = ?, Premium = ?, Password = ?";
    
    private int userID;
    private String displayName;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String relationshipStatus;
    private String gender;
    private String eMail;
    private String street;
    private int houseNr;
    private String town;
    private String zip;
    private boolean premium;
    private String password;
    private String salt;
    
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
     * @param amount The maximum amount of items in the resulting Array.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<NormalUserActiveRecord> executeQuery(PreparedStatement stmt, int amount)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
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
     * This counts all user subscibed to the network.
     * @return The number of user.
     */
    public static int countUser()
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
     * This function will retrieve a list of all friends of a user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @return An ArrayList with all friends of the user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllFriends(int userID, int amount)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL_USER + FROM_FRIENDS + ORDER_BY_DISPLAYNAME_ASC);
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
     * This function will retrieve a list of all user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @return An ArrayList with all user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllUser(int amount)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL + ORDER_BY_DISPLAYNAME_ASC);
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
     * @return An ArrayList with all user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllUserAfter(int amount, String lastUser)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL + AFTER_USER +  ORDER_BY_DISPLAYNAME_ASC);
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
     * This function will count all friends of a user.
     * @param userID The ID of the viewing user.
     * @return The number of all friends of the user.
     */
    public static int countAllRequestingUser(int userID)
    {
        int result = 0;
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(COUNT_ROWS + FROM_REQUESTING);
            stmt.setInt(1, userID);
            
            ResultSet rs = stmt.executeQuery();
                
            if(rs.next())
            {
                result = rs.getInt("Number");
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * This function will retrieve a list of all friends of a user.
     * @param amount Reduces the amount of results to the maximum of this amount.
     * @param userID The ID of the viewing user.
     * @param afterUser The display name of the user determing the first user of the result set.
     * @return An ArrayList with all friends of the user.
     */
    public static ArrayList<NormalUserActiveRecord> findAllFriendsAfterUser(int userID, int amount, String afterUser)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL_USER + FROM_FRIENDS + AND_AFTER_USER + ORDER_BY_DISPLAYNAME_ASC);
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
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(SELECT_ALL_USER + FROM_REQUESTING + ORDER_BY_DISPLAYNAME_ASC);
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
     * @return An array list with all normal users matching the eMail.
     */
    public static ArrayList<NormalUserActiveRecord> findUserByEmail(String eMail)
    {
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(SELECT_ALL + BY_EMAIL);
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
        ArrayList<NormalUserActiveRecord> recs = new ArrayList<NormalUserActiveRecord>();
        try
        {
            Connection con = getDatabaseConnection();
            PreparedStatement stmt = null;
            
            stmt = con.prepareStatement(SELECT_ALL + BY_ID);
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
     * This function creates a new normal user using the data from the current position of the result set.
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
                stmt = con.prepareStatement(INSERT_INTO,  Statement.RETURN_GENERATED_KEYS);
                
                stmt.setString(1, displayName);
                stmt.setString(2, firstName);
                stmt.setString(3, lastName);
                stmt.setDate(4, dateOfBirth);
                stmt.setString(5, relationshipStatus);
                stmt.setString(6, gender);
                stmt.setString(7, eMail);
                stmt.setString(8, street);
                stmt.setInt(9, houseNr);
                stmt.setString(10, town);
                stmt.setString(11, zip);
                stmt.setBoolean(12, premium);
                stmt.setString(13, password);
                stmt.setString(14, salt);
                
                if(stmt.executeUpdate()>0)
                {
                    success = true;
                }
                
                rs = stmt.getGeneratedKeys();
                rs.next();
                userID = rs.getInt(1);
                
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
     * Updates the row with the userID of the object with the data within this object.
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
                
                stmt.setString(1, displayName);
                stmt.setString(2, firstName);
                stmt.setString(3, lastName);
                stmt.setDate(4, dateOfBirth);
                stmt.setString(5, relationshipStatus);
                stmt.setString(6, gender);
                stmt.setString(7, eMail);
                stmt.setString(8, street);
                stmt.setInt(9, houseNr);
                stmt.setString(10, town);
                stmt.setString(11, zip);
                stmt.setBoolean(12, premium);
                stmt.setString(13, password);
               
                stmt.setInt(14, userID);
                
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
     * @return The userID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID The userID to set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return The displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return The firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The dateOfBirth.
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth The dateOfBirth to set.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return The relationshipStatus.
     */
    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    /**
     * @param relationshipStatus The relationshipStatus to set.
     */
    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    /**
     * @return The gender.
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender The gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The eMail.
     */
    public String getEmail() {
        return eMail;
    }

    /**
     * @param eMail The eMail to set.
     */
    public void setEmail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * @return The Street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param Street The Street to set.
     */
    public void setStreet(String Street) {
        this.street = Street;
    }

    /**
     * @return The houseNr.
     */
    public int getHouseNr() {
        return houseNr;
    }

    /**
     * @param houseNr The houseNr to set.
     */
    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    /**
     * @return The town.
     */
    public String getTown() {
        return town;
    }

    /**
     * @param town The town to set.
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * @return The zip.
     */
    public String getZip() {
        return zip;
    }

    /**
     * @param zip The zip to set.
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * @return The premium flag.
     */
    public boolean isPremium() {
        return premium;
    }

    /**
     * @param premium The premium flag to set.
     */
    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    /**
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return The salt.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt The salt to set.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    /**
     * 
     * @return The userID as string.
     */
    public String getUserIDString()
    {
        return "u" + userID;
    }
    
    /**
     * 
     * @param userID The user id as a String
     */
    public void setUserIDString(String userID)
    {
        if(userID.startsWith("u"))
        {
            this.userID = Integer.valueOf(userID.substring(1));
        }
    }
    
    /**
     * Checks if the current user is a friend of a given user.
     * @param userID The user who may be a friend.
     * @return True if they are friends, false otherwise.
     */
    public boolean isFriendWith(int userID)
    {
        return AllFriendsActiveRecord.isFriendWith(this.userID, userID);
    }
    
    /**
     * Checks if the given user has a send a friend request to this user.
     * @param userID The user who may send a pending friend request.
     * @return True if there is a pending friend request, false otherwise.
     */
    public boolean sendOpenFriendshipRequest(int userID)
    {
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecord.findFriendByBothUserStrict(userID, this.userID);
        if(friendRequest.isEmpty())
        {
            return false;
        }
        else
        {
            return (!friendRequest.get(0).isAccepted() && !friendRequest.get(0).isRejected());
        }
    }
    
    /**
     * Checks if the given user has received a friend request by this user.
     * @param userID The user who may received a pending friend request.
     * @return True if there is a pending friend request, false otherwise.
     */
    public boolean receivedOpenFriendshipRequest(int userID)
    {
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecord.findFriendByBothUserStrict(this.userID, userID);
        if(friendRequest.isEmpty())
        {
            return false;
        }
        else
        {
            return (!friendRequest.get(0).isAccepted() && !friendRequest.get(0).isRejected());
        }
    }
    
    /**
     * Checks if the given user has rejected a friend request by this user or the other way around.
     * @param userID The user who may received a pending friend request.
     * @return True if there is a pending friend request, false otherwise.
     */
    public boolean rejectedFriendshipRequest(int userID)
    {
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecord.findFriendByBothUser(this.userID, userID);
        if(friendRequest.isEmpty())
        {
            return false;
        }
        else
        {
            return friendRequest.get(0).isRejected();
        }
    }
}