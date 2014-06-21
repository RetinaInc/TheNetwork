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

package commands;

import java.io.IOException;
import activeRecord.PostActiveRecord;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request to publish a new post.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class PostStatusCommand implements Command{
    
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
    public PostStatusCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    /**
     * This function executes the process of creating a new post. It validates the input and creates a new post if the input is valid.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage = "/error.jsp";
        if((String)request.getSession().getAttribute("userID") != null)
        {
            String user = (String)request.getSession().getAttribute("userID");
            int userID = Integer.valueOf(user.substring(1));
            if(request.getParameter("newStatus") != null)
            {
                String status = request.getParameter("newStatus");
                if(!status.isEmpty())
                { 
                    if(status.length() <= 1000)
                    {
                        if(user.startsWith("f"))
                        {
                            PostActiveRecord post = new PostActiveRecord();
                            post.setContent(status);
                            post.setPostPublic(true);
                            post.setPublishingPage(userID);
                            if(post.insert())
                            {
                                request.setAttribute("message", "Status successfully updated.");
                                request.setAttribute("messageSuccess", true);
                                viewPage = "/index";
                            }
                            else
                            {
                                request.setAttribute("message", "There has been a problem updating your status, please try again.");
                                request.setAttribute("messageError", true);
                                viewPage = "/index";
                            }
                        } 
                        else if(user.startsWith("u"))
                        {
                            PostActiveRecord post = new PostActiveRecord();
                            post.setContent(status);
                            if(request.getParameter("postPublic") != null && request.getParameter("postPublic").equals("Private"))
                            {
                                post.setPostPublic(false);
                            } 
                            else if (request.getParameter("postPublic") == null || request.getParameter("postPublic").equals("Public"))
                            {
                                post.setPostPublic(true);
                            }
                            post.setPublishingUser(userID);
                            if(post.insert())
                            {
                                request.setAttribute("message", "Status successfully updated.");
                                request.setAttribute("messageSuccess", true);
                                viewPage = "/index";
                            }
                            else
                            {
                                request.setAttribute("message", "There has been a problem updating your status, please try again.");
                                request.setAttribute("messageError", true);
                                viewPage = "/index";
                            }
                        }
                    }
                    else
                    {
                        request.setAttribute("message", "<strong>Could not update your status</strong>, because it is too long.");
                        request.setAttribute("messageError", true);
                        viewPage = "/index";
                    }
                }
                else
                {
                    request.setAttribute("message", "<strong>Could not update your status</strong>, because it is empty.");
                    request.setAttribute("messageError", true);
                    viewPage = "/index";
                }
            }
        }
        return viewPage;
    }
}
