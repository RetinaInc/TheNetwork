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

import activeRecord.CommentActiveRecord;
import activeRecord.CommentActiveRecordFactory;
import commands.Command;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import activeRecord.PostActiveRecord;
import activeRecord.PostActiveRecordFactory;
import java.util.ArrayList;

/**
 * This command processes the AJAX request voting for a comment of post.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class VotingCommand implements Command{
    
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
    public VotingCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of voting on a post.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
        String viewPage = "/ajax_view/doVote.jsp";
        boolean success = false;
        ArrayList<PostActiveRecord> posts = null;
        ArrayList<CommentActiveRecord> comments = null;
        String uri = request.getRequestURI().substring(10);
        if(request.getParameter("post") != null)
        {
            int postID = Integer.valueOf(request.getParameter("post"));
            String user = (String)request.getSession().getAttribute("userID");
            posts = PostActiveRecordFactory.findPostByID(postID, user);
            if(uri.startsWith("/up"))
            {
                success = posts.get(0).upvote(user);
            }
            else if(uri.startsWith("/down"))
            {
                success = posts.get(0).downvote(user);
            }
            else if(uri.startsWith("/remove"))
            {
                success = posts.get(0).removeVote(user);
            }
            else
            {
                success = false;
            }
            if(success)
            {
                request.setAttribute("updatedPost", posts.get(0));
            }
        }
        else if(request.getParameter("comment") != null)
        {
            int commentID = Integer.valueOf(request.getParameter("comment"));
            String user = (String)request.getSession().getAttribute("userID");
            comments = CommentActiveRecordFactory.findCommentByCommentID(commentID, user);
            if(uri.startsWith("/up"))
            {
                success = comments.get(0).upvote(user);
            }
            else if(uri.startsWith("/down"))
            {
                success = comments.get(0).downvote(user);
            }
            else if(uri.startsWith("/remove"))
            {
                success = comments.get(0).removeVote(user);
            }
            else
            {
                success = false;
            }
            if(success)
            {
                request.setAttribute("updatedComment", comments.get(0));
            }
        }
        
        return viewPage;
    }
}
