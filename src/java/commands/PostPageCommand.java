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

import activeRecord.AllFriendsActiveRecord;
import activeRecord.CommentActiveRecord;
import activeRecord.FfollowsPActiveRecord;
import activeRecord.CommentActiveRecordFactory;
import activeRecord.FfollowsPActiveRecordFactory;
import activeRecord.PostActiveRecord;
import activeRecord.PostActiveRecordFactory;
import activeRecord.UfollowsPActiveRecord;
import activeRecord.UfollowsPActiveRecordFactory;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class manages the displaying and editing of a post.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class PostPageCommand implements Command{
    
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
    public PostPageCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    /*
     * This function executes the process of creating a new post. It validates the input and creates a new post if the input is valid.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage ="/postPage.jsp";
        try
        {
            if(request.getSession().getAttribute("userID") != null)
            {
                String user = (String)request.getSession().getAttribute("userID");
                int userID = Integer.valueOf(user.substring(1));
                String uri = request.getRequestURI();
                boolean edit = false;
                //Checks if user sent edit command.
                if(uri.endsWith("edit"))
                {
                    uri = uri.substring(0, uri.length()-5);
                    edit = true;
                }
                int postID = Integer.valueOf(uri.substring(6));
                ArrayList<PostActiveRecord> posts = PostActiveRecordFactory.findPostByID(postID, user);
                PostActiveRecord post;
                if(posts.size() != 1)
                {
                    throw new Exception("Post size not 1, but " + posts.size());
                }
                else
                {
                    post = posts.get(0);
                }
                
                if(edit && isAllowedToEdit(user, post))
                {
                    if(request.getParameter("updatedPost") != null)
                    {
                        String updatedContent = request.getParameter("updatedPost");
                        if(!updatedContent.isEmpty() && updatedContent.length() < 1000)
                        {
                            post.setContent(updatedContent);
                            if(post.update(true))
                            {
                                viewPage="/postPage.jsp";
                                request.setAttribute("message", "Successfully updated your post.");
                                request.setAttribute("messageSuccess", true);
                            }
                            else
                            {
                                viewPage="/postPageEdit.jsp";
                                request.setAttribute("message", "Problem saving your new post.");
                                request.setAttribute("messageError", true);
                            }
                        }
                        else
                        {
                            viewPage="/postPageEdit.jsp";
                            request.setAttribute("message", "You provided an invalid input (Empty or too long post.");
                            request.setAttribute("messageError", true);
                        }
                    }
                    else
                    {
                        viewPage="/postPageEdit.jsp";
                    }
                }
                else if (isAllowedToEdit(user, post) || isAllowedToView(user, post))
                {
                    if(user.startsWith("u"))
                    {
                        UfollowsPActiveRecordFactory.setRead(postID, userID);
                    }
                    else if(user.startsWith("f"))
                    {
                        FfollowsPActiveRecordFactory.setRead(postID, userID);
                    }
                    viewPage="/postPage.jsp";
                }
                else
                {
                    request.setAttribute("errorCode", "You have insufficient rights to view/edit this post.");
                    throw new Exception();
                }
                if(viewPage.equals("/postPage.jsp"))
                {
                    request.setAttribute("commentArray", CommentActiveRecordFactory.findCommentByPostID(postID, user));
                }
                request.setAttribute("postArray", posts);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            viewPage = "/error.jsp";
        }
        return viewPage;
    }
    
    /**
     * Checks if a specific user is allowed to read a post.
     * @param viewingUser The user who wants to read the post.
     * @param post The post which is going to be read.
     * @return True if the user is allowed, false otherwise.
     */
    private boolean isAllowedToView(String viewingUser, PostActiveRecord post)
    {
        boolean allowed = false;
        int userID = Integer.valueOf(viewingUser.substring(1));
        if(viewingUser.startsWith("f"))
        {
            allowed = (post.getPublishingPage()==userID);
        }
        else if(viewingUser.startsWith("u"))
        {
            if(post.isPostPublic())
            {
                allowed = true;
            }   
            else
            {
                allowed = AllFriendsActiveRecord.isFriendWith(userID, post.getPublishingUser());
            }
        }
        return allowed;
    }
    
    /**
     * Checks if a specific user is allowed to edit a post.
     * @param editingUser The user who wants to edit the post.
     * @param post The post which is going to be edited.
     * @return True if the user is allowed, false otherwise.
     */
    private boolean isAllowedToEdit(String editingUser, PostActiveRecord post)
    {
        boolean allowed = false;
        int userID = Integer.valueOf(editingUser.substring(1));
        if(editingUser.startsWith("u"))
        {
            allowed = (userID == post.getPublishingUser());
        }
        else if(editingUser.startsWith("f"))
        {
            allowed = (userID == post.getPublishingPage());
        }
        return allowed;
    }
    
}
