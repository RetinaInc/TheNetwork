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

package commands;

import activeRecord.FanpageActiveRecordFactory;
import activeRecord.SysAdminActiveRecordFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request to change to a connected profile or log out of the system.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class ChangeProfileCommand implements Command {
    
    /**
     * The servlet request.
     */
    private HttpServletRequest request;
    /**
     * The servlet response.
     */
    private HttpServletResponse response;
    
    /**
     * Create a new command.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public ChangeProfileCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    /**
     * This function executes the process of logging a user into the system.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage = "/error.jsp";
        
        if(request.getSession().getAttribute("userID") != null)
        {
            String currentUser = (String)request.getSession().getAttribute("userID");
            int currentUserID = Integer.valueOf(currentUser.substring(1));
            if(request.getRequestURI().endsWith("/logout"))
            {
                request.getSession().removeAttribute("userID");
                viewPage = "/index";
            }
            else
            {
                boolean success = false;
                String otherUser = request.getRequestURI().substring(15);
                try
                {
                    int otherUserID = Integer.valueOf(otherUser.substring(1));
                    if(currentUser.startsWith("u"))
                    {
                        if(otherUser.startsWith("f"))
                        {
                            success = currentUserID == FanpageActiveRecordFactory.findAdministratingUser(otherUserID);
                        }
                        else if(otherUser.startsWith("a"))
                        {
                            success = currentUserID == SysAdminActiveRecordFactory.findAdministratingUser(otherUserID);
                        }
                        else if(otherUser.startsWith("u"))
                        {
                            success = currentUserID == otherUserID;
                        }
                        else
                        {
                            success = false;
                        }
                    }
                    else if(currentUser.startsWith("f"))
                    {
                        if(otherUser.startsWith("u"))
                        {
                            success = otherUserID == FanpageActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                        else if(otherUser.startsWith("f"))
                        {
                            success = FanpageActiveRecordFactory.findAdministratingUser(otherUserID) == FanpageActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                        else if(otherUser.startsWith("a"))
                        {
                            success = SysAdminActiveRecordFactory.findAdministratingUser(otherUserID) == FanpageActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                    }
                    else if(currentUser.startsWith("a"))
                    {
                        if(otherUser.startsWith("u"))
                        {
                            success = otherUserID == SysAdminActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                        else if(otherUser.startsWith("f"))
                        {
                            success = FanpageActiveRecordFactory.findAdministratingUser(otherUserID) == SysAdminActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                        else if(otherUser.startsWith("a"))
                        {
                            success = SysAdminActiveRecordFactory.findAdministratingUser(otherUserID) == SysAdminActiveRecordFactory.findAdministratingUser(currentUserID);
                        }
                    }
                    
                    if(success)
                    {
                        request.getSession().removeAttribute("userID");
                        request.getSession().setAttribute("userID", otherUser);
                        viewPage = "/index";
                    }
                    else
                    {
                        request.setAttribute("errorCode", "You are not allowed to change to that user.");
                        viewPage = "/error.jsp";
                    }
                }
                catch(NumberFormatException e)
                {
                    request.setAttribute("errorCode", "The provided user is invalid.");
                    viewPage = "/error.jsp";
                }
            }
        }
        return viewPage;
    }
}
