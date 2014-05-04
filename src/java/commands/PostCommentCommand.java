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

import activeRecord.CommentActiveRecord;
import activeRecord.FfollowsPActiveRecord;
import activeRecord.FfollowsPActiveRecordFactory;
import activeRecord.UfollowsPActiveRecord;
import activeRecord.UfollowsPActiveRecordFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ArrayList;

/**
 * This class processes the request to publish a new comment.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class PostCommentCommand implements Command{
    
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
    public PostCommentCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    /**
     * This function executes the process of creating a new comment. It validates the input and creates a new post if the input is valid.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
        String viewPage = "/error.jsp";
        boolean success = false;
        
        if((String)request.getSession().getAttribute("userID") != null)
        {
            String user = (String)request.getSession().getAttribute("userID");
            int userID = Integer.valueOf(user.substring(1));
            int postID = Integer.valueOf(request.getRequestURI().substring(13));
            if(request.getParameter("newComment") != null)
            {
                String comment = request.getParameter("newComment");
                if(!comment.isEmpty())
                { 
                    if(comment.length() <= 1000)
                    {
                        //insert comment as a fanpage
                        if(user.startsWith("f"))
                        {
                            CommentActiveRecord commentRec = new CommentActiveRecord();
                            commentRec.setContent(comment);
                            commentRec.setPublishingPage(userID);
                            commentRec.setRelatedPost(postID);
                            if(commentRec.insert())
                            {
                                //Check if the user followed tha post. Otherwise let him start following the post.
                                ArrayList<FfollowsPActiveRecord> following = FfollowsPActiveRecordFactory.findFfollowsPByPostIDPageID(postID, userID);
                                if(following.isEmpty())
                                {
                                    FfollowsPActiveRecord follows = new FfollowsPActiveRecord();
                                    follows.setFollowedPost(postID);
                                    follows.setFollowingFanpage(userID);
                                    success = follows.insert();
                                }
                                else
                                {
                                    success = true;
                                }
                            }
                            else
                            {
                                success = false;
                            }
                        } 
                        else if(user.startsWith("u"))
                        {
                            CommentActiveRecord commentRec = new CommentActiveRecord();
                            commentRec.setContent(comment);
                            commentRec.setPublishingUser(userID);
                            commentRec.setRelatedPost(postID);
                            if(commentRec.insert())
                            {
                                ArrayList<UfollowsPActiveRecord> following = UfollowsPActiveRecordFactory.findUfollowsPByPostIDUserID(postID, userID);
                                if(following.isEmpty())
                                {
                                    UfollowsPActiveRecord follows = new UfollowsPActiveRecord();
                                    follows.setFollowedPost(postID);
                                    follows.setFollowingUser(userID);
                                    success = follows.insert();
                                }
                                else
                                {
                                    success = true;
                                }
                            }
                            else
                            {
                                success = false;
                            }
                        }
                        
                        if(success)
                        {
                            request.setAttribute("message", "Comment successfully added.");
                            request.setAttribute("messageSuccess", true);
                            viewPage = "/post/" + postID;
                            FfollowsPActiveRecordFactory.notify(postID);
                            UfollowsPActiveRecordFactory.notify(postID);
                        }
                        else
                        {
                            request.setAttribute("message", "There has been a problem commenting on this post, please try again.");
                            request.setAttribute("messageError", true);
                            viewPage = "/post/" + postID;
                        }
                    }
                    else
                    {
                        request.setAttribute("message", "<strong>Could not comment on this post</strong>, because the comment is too long.");
                        request.setAttribute("messageError", true);
                        viewPage = "/post/" + postID;
                    }
                }
                else
                {
                    request.setAttribute("message", "<strong>Could not comment on this post</strong>, because the comment is empty.");
                    request.setAttribute("messageError", true);
                    viewPage = "/post/" + postID;
                }
            }
        }
        return viewPage;
    }
}
