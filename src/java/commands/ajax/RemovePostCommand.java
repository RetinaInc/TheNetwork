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

import activeRecord.PostActiveRecord;
import activeRecord.PostActiveRecordFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;

/**
 * This command provides the execution of the deletion of a specific post.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class RemovePostCommand implements Command{
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
    public RemovePostCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of removing a post.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage;
        ArrayList<PostActiveRecord> posts;
        if(request.getParameter("post") != null)
        {
            int postID = Integer.valueOf(request.getParameter("post"));
            String user = (String)request.getSession().getAttribute("userID");
            posts = PostActiveRecordFactory.findPostByID(postID, user);
            if(posts.size() == 1)
            {
                if(posts.get(0).getPublishingUser() == Integer.valueOf(user.substring(1)) || posts.get(0).getPublishingPage() == Integer.valueOf(user.substring(1)))
                {
                    if(posts.get(0).remove())
                    {
                        viewPage = "/assets/include_message.jsp";
                        request.setAttribute("messageSuccess", true);
                        request.setAttribute("message", "Post deleted successfully!");
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to delete the post, please try again.");
                    }
                }
                else
                {
                    viewPage = "/ajax_view/error.jsp";
                    request.setAttribute("errorCode", "You don't have the rights to delete this post.");
                }
            }
            else
            {
                viewPage = "/ajax_view/error.jsp";
                request.setAttribute("errorCode", "Unable to delete the post, please try again.");
            }
        }
        else
        {
            viewPage = "/ajax_view/error.jsp";
            request.setAttribute("errorCode", "Error while processing your request, please try again.");
        }
        return viewPage;
    }
}
