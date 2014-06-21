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

package commands.ajax;

import activeRecord.FanpageActiveRecordFactory;
import activeRecord.UfollowsFActiveRecord;
import activeRecord.UfollowsFActiveRecordFactory;
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This command gathers the data for the page managing.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class PageManagementCommand implements Command {

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
    public PageManagementCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of managing a page.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage;
        if(request.getSession().getAttribute("userID") != null && request.getParameter("page") != null)
        {
            String user = (String)request.getSession().getAttribute("userID");
            int userID = Integer.valueOf(user.substring(1));
            int pageID = Integer.valueOf(request.getParameter("page"));
            request.setAttribute("page", FanpageActiveRecordFactory.findPageByID(pageID).get(0));
            if(user.startsWith("u"))
            {
                if(request.getRequestURI().endsWith("/follow"))
                {
                    if(!UfollowsFActiveRecordFactory.isFollowing(userID, pageID))
                    {
                        UfollowsFActiveRecord following = new UfollowsFActiveRecord();
                        following.setFollowedPage(pageID);
                        following.setFollowingUser(userID);
                        if(following.insert())
                        {
                            if(request.getRequestURI().startsWith("/ajax/page/list"))
                            {
                                viewPage = "/assets/include_message.jsp";
                                request.setAttribute("message", "Successfully following this page!");
                                request.setAttribute("messageSuccess", true);
                            }
                            else
                            {
                                viewPage = "/ajax_view/getPageButtons.jsp";
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Problem saving your following connection.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to send following request (Maybe you are allready following that page).");
                    }
                }
                else if(request.getRequestURI().endsWith("/unfollow"))
                {
                    if(UfollowsFActiveRecordFactory.isFollowing(userID, pageID))
                    {
                        ArrayList<UfollowsFActiveRecord> unfollow = UfollowsFActiveRecordFactory.findFollowingByUserIDAndPageID(userID, pageID);
                        if(unfollow.size() == 1)
                        {
                            if(unfollow.get(0).remove())
                            {
                                if(request.getRequestURI().startsWith("/ajax/page/list"))
                                {
                                    viewPage = "/assets/include_message.jsp";
                                    request.setAttribute("message", "Successfully stopped following this page!");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    viewPage = "/ajax_view/getPageButtons.jsp";
                                }
                            }
                            else
                            {
                                viewPage = "/ajax_view/error.jsp";
                                request.setAttribute("errorCode", "Unable to remove following connection.");
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Unable to load following connection.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to remove following connection (Maybe you are not following that page).");
                    }
                }
                else
                {
                    viewPage = "/ajax_view/error.jsp";
                    request.setAttribute("errorCode", "Unrecognized command.");
                }
            }
            else
            {
                viewPage = "/ajax_view/error.jsp";
                request.setAttribute("errorCode", "You have insufficient rights to execute this command!");
            }
        }
        else
        {
            viewPage = "/ajax_view/error.jsp";
            request.setAttribute("errorCode", "You have insufficient rights to execute this command!");
        }
        return viewPage;
    }
}
