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
import java.util.ArrayList;

/**
 * This class retrieves active records of the UisFriendWithU table from the database.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class UisFriendWithUActiveRecordFactory extends DatabaseUtility {
    
    /**
     * This String contains the part of the SQL command selecting the complete row from the table.
     */
    private static final String SELECT_ALL =
            "Select * " +
            "from UisFriendWithU";
    
    /**
     * This String contains the part of the SQL command reducing the selection to both user, checking strictly if user1 isthe requesting user.
     */
    private static final String BY_BOTH_USER_STRICT =
            " Where RequestingUser=?" + 
            " And RespondingUser=?";
    
    /**
     * This String contains the part of the SQL command reducing the selection to both user.
     */
    private static final String BY_BOTH_USER =
            " Where RequestingUser=?" + 
            " And RespondingUser=?" + 
            " Or RespondingUser=?" + 
            " And RequestingUser=?";
    
    /**
     * This function executes the query for a given prepared statement.
     * @param stmt The prepared statement which needs to be executed.
     * @return An ArrayList containing the results of the query.
     */
    private static ArrayList<UisFriendWithUActiveRecord> executeQuery(PreparedStatement stmt)
    {
        ArrayList<UisFriendWithUActiveRecord> recs = new ArrayList<>();
        try
        {
            Connection con = stmt.getConnection();
            ResultSet rs;

            try
            {               
                rs = stmt.executeQuery();
                
                while (rs.next())
                {
                    UisFriendWithUActiveRecord e = createUisFriendWithU(rs);
                    recs.add(e);
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
     * This function retrieves the row representing the friendship between the two user.
     * @param requestingUser The requesting user's id who is checked
     * @param respondingUser The respondings user's id who is checked
     * @return An ArraList with all rows fitting the both id's
     */
    public static ArrayList<UisFriendWithUActiveRecord> findFriendByBothUser(int requestingUser, int respondingUser)
    {
        ArrayList<UisFriendWithUActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_BOTH_USER);
            stmt.setInt(1, requestingUser);
            stmt.setInt(2, respondingUser);
            stmt.setInt(3, requestingUser);
            stmt.setInt(4, respondingUser);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function retrieves the row representing the friendship between the two user. This function takes care that the requesting user is only the requesting user and not also the responding user.
     * @param requestingUser The requesting user's id who is checked
     * @param respondingUser The respondings user's id who is checked
     * @return An array list with all rows fitting the both id's
     */
    public static ArrayList<UisFriendWithUActiveRecord> findFriendByBothUserStrict(int requestingUser, int respondingUser)
    {
        ArrayList<UisFriendWithUActiveRecord> recs;
        try
        {
            PreparedStatement stmt = getDatabaseConnection().prepareStatement(SELECT_ALL + BY_BOTH_USER_STRICT);
            stmt.setInt(1, requestingUser);
            stmt.setInt(2, respondingUser);
                
            recs = executeQuery(stmt);
        }
        catch (Exception e)
        {
            recs = null;
        }
        return recs;
    }
    
    /**
     * This function creates a new friends active record using the data from the current position of the result set.
     * @param rs The data source for the new active record.
     * @return The new created active record.
     */
    private static UisFriendWithUActiveRecord createUisFriendWithU(ResultSet rs)
    {
        UisFriendWithUActiveRecord d = new UisFriendWithUActiveRecord();
        try
        {
            d.setRequestingUser(rs.getInt("RequestingUser"));
            d.setRespondingUser(rs.getInt("RespondingUser"));
            d.setAccepted(rs.getBoolean("Accepted"));
            d.setNotified(rs.getBoolean("Notified"));
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
}
