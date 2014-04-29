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
 * This class represents a row of the UfollowsF table and provides functions to insert, remove and update the data in the table.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UisFriendWithUActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into UisFriendWithU(RequestingUser, RespondingUser, Accepted, Rejected, Notified)" +
            "Values (?, ?, ?, ?, ?) ";
    
    /**
     * This String contains the part of the SQL command reducing the selection to both user.
     */
    private final String BY_BOTH_USER =
            " Where RequestingUser=?" + 
            " And RespondingUser=?" + 
            " Or RespondingUser=?" + 
            " And RequestingUser=?";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private final String DELETE =
            " Delete" +
            " from UisFriendWithU";
    
    /**
     * This String cointains the SQL command to update data in the database.
     */
    private final String UPDATE =
            " Update UisFriendWithU" + 
            " Set Accepted = ?, Rejected = ?, Notified = ?";
    
    private int requestingUser;
    private int respondingUser;
    private boolean rejected;
    private boolean accepted;
    private boolean notified;
    
    /**
     * This function inserts the object into the database.
     * @return True if insert was successfull, false otherwise.
     */
    public boolean insert()
    {
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(INSERT_INTO);
            stmt.setInt(1, getRequestingUser());
            stmt.setInt(2, getRespondingUser());
            stmt.setBoolean(3, isAccepted());
            stmt.setBoolean(4, isRejected());
            stmt.setBoolean(5, isNotified());
            
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
        boolean success;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(DELETE + BY_BOTH_USER);
            stmt.setInt(1, requestingUser);
            stmt.setInt(2, respondingUser);
            stmt.setInt(3, requestingUser);
            stmt.setInt(4, respondingUser);

            success = executeUpdate(stmt);
        }
        catch (Exception e)
        {
            success = false;
        }
        return success;
    }
    
    /**
     * Updates the row with the both userID's.
     * @return True if update was successful, false otherwise.
     */
    public boolean update()
    {
        boolean success = false;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(UPDATE + BY_BOTH_USER);
            stmt.setBoolean(1, accepted);
            stmt.setBoolean(2, rejected);
            stmt.setBoolean(3, notified);

            stmt.setInt(4, requestingUser);
            stmt.setInt(5, respondingUser);
            stmt.setInt(6, requestingUser);
            stmt.setInt(7, respondingUser);
                
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
     * @return The requestingUser.
     */
    public int getRequestingUser() {
        return requestingUser;
    }

    /**
     * @param requestingUser The requestingUser to set.
     */
    public void setRequestingUser(int requestingUser) {
        this.requestingUser = requestingUser;
    }

    /**
     * @return The respondingUser.
     */
    public int getRespondingUser() {
        return respondingUser;
    }

    /**
     * @param respondingUser The respondingUser to set.
     */
    public void setRespondingUser(int respondingUser) {
        this.respondingUser = respondingUser;
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
     * @return The notified flag.
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * @param notified The notified flag to set.
     */
    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
