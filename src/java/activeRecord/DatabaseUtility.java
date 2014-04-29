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
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class functions as superclass for every active record, providing the database connection.
 * @author Frank Steiler <frank@steiler.eu>
 */
public abstract class DatabaseUtility
{
    /**
     * The URL to the database.
     */
    protected static String DBASE_URL = "jdbc:derby://localhost:1527/TheNetwork";

    /**
     * The username for the database.
     */
    protected static final String databaseUsername = "db_user";
    
    /**
     * The password for the database.
     */
    protected static final String databasePassword = "Password1!";
    
    /**
     * This function establishes the connection to the database.
     * @return The opened database connection.
     * @throws Exception If there are problems connecting to the database.
     */
    protected static Connection getDatabaseConnection() throws Exception
    {
        Connection con = null;

        try
        {
            con = DriverManager.getConnection(DBASE_URL, databaseUsername, databasePassword);
        }
        catch (SQLException sqle)
        {
            String msg = "Cannot establish a connection to the database";
            throw new Exception(msg, sqle);
        }
        finally
        {
            return con;
        }
    }

    /**
     * This function closes the connection to the database.
     * @param con The connection to the database which needs to be closed.
     * @throws Exception If there are problems closing the connection.
     */
    protected static void closeDatabaseConnection(Connection con) throws Exception
    {
        try
        {
            con.close();
        }
        catch (SQLException sqle)
        {
            String msg = "Problem closing the connection to the database";
            throw new Exception(msg, sqle);
        }
    }
}
