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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class represents a row of the NormalUser table providing functions to insert and update these rows.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class NormalUserActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into NormalUser(DisplayName, FirstName, LastName, DateOfBirth, RelationshipStatus, Gender, Email, Street, HouseNr, Town, Zip, Premium, Password, Salt)" +
            "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update NormalUser" + 
            " Set DisplayName = ?, FirstName = ?, LastName = ?, DateOfBirth = ?, RelationshipStatus = ?, Gender = ?, Email = ?, Street = ?, HouseNr = ?, Town = ?, Zip = ?, Premium = ?, Password = ?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a specific userID.
     */
    private final String BY_ID =
            " Where UserID=?";
    
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
     * This function inserts the object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(INSERT_INTO,  Statement.RETURN_GENERATED_KEYS);
                
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
                
            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
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
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE + BY_ID);
                
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
                
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs != null && rs.next())
                {
                    userID = rs.getInt(1);
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
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecordFactory.findFriendByBothUserStrict(userID, this.userID);
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
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecordFactory.findFriendByBothUserStrict(this.userID, userID);
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
        ArrayList<UisFriendWithUActiveRecord> friendRequest = UisFriendWithUActiveRecordFactory.findFriendByBothUser(this.userID, userID);
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