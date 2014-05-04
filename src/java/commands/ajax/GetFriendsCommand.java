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

package commands.ajax;

import activeRecord.NormalUserActiveRecord;
import activeRecord.NormalUserActiveRecordFactory;
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request to load additional friends.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class GetFriendsCommand implements Command{

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
    public GetFriendsCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of retrieving a set of friends. This set is going to be retrieved depending on the request and session.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        
        String viewPage = "/ajax_view/getUserListItem.jsp";
        String user = (String)request.getSession().getAttribute("userID");
        int userID = Integer.valueOf(user.substring(1));
        if(request.getSession().getAttribute("lastFriend") != null || request.getSession().getAttribute("lastUser") != null)
        {
            ArrayList<NormalUserActiveRecord> friendArray = null;
            if(user.startsWith("u"))
            {
                String lastFriend = (String)request.getSession().getAttribute("lastFriend");
                friendArray = NormalUserActiveRecordFactory.findAllFriendsAfter(userID, Integer.valueOf(request.getParameter("amount")), lastFriend);
                if(friendArray != null && !friendArray.isEmpty())
                {
                    request.getSession().setAttribute("lastFriend", friendArray.get(friendArray.size()-1).getDisplayName());
                }
                else
                {
                    request.getSession().removeAttribute("lastFriend");
                }
            }
            else if(user.startsWith("a"))
            {
                String lastUser = (String)request.getSession().getAttribute("lastUser");
                friendArray = NormalUserActiveRecordFactory.findAllUserAfter(Integer.valueOf(request.getParameter("amount")), lastUser);
                if(friendArray != null && !friendArray.isEmpty())
                {
                    request.getSession().setAttribute("lastUser", friendArray.get(friendArray.size()-1).getDisplayName());
                }
                else
                {
                    request.getSession().removeAttribute("lastUser");
                }
            }
            else
            {
                viewPage = "/ajax_view/error.jsp";
                request.setAttribute("errorCode", "You have insufficient rights to execute this command");
            }
            request.setAttribute("listArray", friendArray);
        }
        else
        {
            viewPage = "/ajax_view/error.jsp";
            request.setAttribute("errorCode", "Error while processing your request");
        }
        return viewPage;
    }
}
