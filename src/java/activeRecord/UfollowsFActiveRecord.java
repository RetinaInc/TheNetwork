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
import java.sql.SQLException;

/**
 * This class represents a row of the UfollowsF table and provides functions to check if a user is following a page and insert data in the table.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class UfollowsFActiveRecord extends DatabaseUtility {
    
    /**
     * This String contains the SQL command to insert data into the database.
     */
    private final String INSERT_INTO =
            "Insert into UfollowsF(FollowingUser, FollowedFanpage)" +
            "Values (?, ?) ";
    
    /**
     * This String contains the part of the SQL command reducing the selection to a page and a user.
     */
    private final String BY_USERID_PAGEID =
            " Where FollowingUser = ?" + 
            " And FollowedFanpage = ?";
    
    /**
     * This String contains the part of the SQL command deleting the row.
     */
    private final String DELETE =
            " Delete" +
            " from UfollowsF";
    
    private int followingUser;
    private int followedPage;
    
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
            stmt.setInt(1, followingUser);
            stmt.setInt(2, followedPage);
                
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
        boolean success = false;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(DELETE + BY_USERID_PAGEID);
            stmt.setInt(1, followingUser);
            stmt.setInt(2, followedPage);

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
     * @return The followingUser.
     */
    public int getFollowingUser() {
        return followingUser;
    }

    /**
     * @param followingUser The followingUser to set.
     */
    public void setFollowingUser(int followingUser) {
        this.followingUser = followingUser;
    }

    /**
     * @return The followedPage.
     */
    public int getFollowedPage() {
        return followedPage;
    }

    /**
     * @param followedPage The followedPage to set.
     */
    public void setFollowedPage(int followedPage) {
        this.followedPage = followedPage;
    }
}
