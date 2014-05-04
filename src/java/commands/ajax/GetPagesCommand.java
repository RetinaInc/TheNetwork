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

import activeRecord.FanpageActiveRecord;
import activeRecord.FanpageActiveRecordFactory;
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request to load additional pages.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class GetPagesCommand implements Command{

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
    public GetPagesCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of retrieving a set of pages. This set is going to be retrieved depending on the request and session.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage ="/ajax_view/getPageListItem.jsp";
        String user = (String)request.getSession().getAttribute("userID");
        int userID = Integer.valueOf(user.substring(1));
        
        if(request.getSession().getAttribute("lastPage") != null)
        {
            String lastPage = (String)request.getSession().getAttribute("lastPage");
            ArrayList<FanpageActiveRecord> pageArray = null;
            if(user.startsWith("u"))
            {
                pageArray = FanpageActiveRecordFactory.findAllFollowingPagesAfterPage(userID, Integer.valueOf(request.getParameter("amount")), lastPage);
            }
            else if(user.startsWith("a"))
            {
                pageArray = FanpageActiveRecordFactory.findAllPagesAfterPage(Integer.valueOf(request.getParameter("amount")), lastPage);
            }
            else
            {
                viewPage = "/ajax_view/error.jsp";
                request.setAttribute("errorCode", "You have insufficient rights to execute this command");
            }
            request.setAttribute("listPageArray", pageArray);
            if(pageArray != null && !pageArray.isEmpty())
            {
                request.getSession().setAttribute("lastPage", pageArray.get(pageArray.size()-1).getDisplayName());
            }
            else
            {
                request.getSession().removeAttribute("lastPage");
            }
        }
        else
        {
            viewPage = "/ajax_view/error.jsp";
            request.setAttribute("errorCode", "Error while processing your request");
        }
        return viewPage;
    }
}
