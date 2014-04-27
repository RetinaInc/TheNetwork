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

import commands.ajax.GetPostsCommand;
import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request by the XML desktop application.
 * @author Frank Steiler <frank@steiler.eu>
 */
class XMLAppCommand implements Command {
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    /**
     * Create a new command.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public XMLAppCommand(HttpServletRequest request, HttpServletResponse response)
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
    public String execute() throws ServletException, IOException 
    {
        Command login = new LoginCommand(request, response);
        String loginString = login.execute();
        if(loginString.equals("/index"))
        {
            if(((String)request.getSession().getAttribute("userID")).startsWith("u"))
            {
                Command getPosts;
                if(request.getParameter("older") != null)
                {
                    request.getSession().setAttribute("lastItemTimestamp", new Timestamp(Long.parseLong(request.getParameter("older"))));
                    getPosts = new GetPostsCommand(request, response);
                }
                else if(request.getParameter("newer") != null)
                {
                    request.getSession().setAttribute("firstItemTimestamp", new Timestamp(Long.parseLong(request.getParameter("newer"))));
                    getPosts = new GetPostsCommand(request, response);
                }
                else
                {
                    getPosts = new HomepageCommand(request, response);
                }
                String postViewPage = getPosts.execute();
                if(!postViewPage.equals("/homepage.jsp") && !postViewPage.equals("/ajax_view/getPosts.jsp"))
                {
                    request.setAttribute("errorMessage", "The entered credentials are wrong.");
                }
            }
            else
            {
                request.setAttribute("errorMessage", "No normal user attached to this account.");
            }
        }
        else if(loginString.equals("/loginSecondStep.jsp"))
        {
            request.setAttribute("errorMessage", "The entered credentials are wrong, or no normal user attached to this account.");
        }
        else if(loginString.equals("/login"))
        {
            execute();
        }
        else
        {
            request.setAttribute("errorMessage", "The entered credentials are wrong.");
        }
        return "/xmlView.jsp";
    }
    
}
