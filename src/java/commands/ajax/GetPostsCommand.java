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
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request to load additional posts.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class GetPostsCommand implements Command{

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
    public GetPostsCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of retrieving a set of posts. This set is going to be retrieved depending on the request and session.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage ="/ajax_view/getPosts.jsp";
        String user = (String)request.getSession().getAttribute("userID");
        String path = null;
        int pathID = 0;
        int userID = Integer.valueOf(user.substring(1));
        if(request.getParameter("path")!= null)
        {
            path = request.getParameter("path");
            try 
            {
                pathID = Integer.valueOf(path.substring(6));
            } 
            catch (StringIndexOutOfBoundsException | NumberFormatException e) 
            {
                path = null;
            }
        }
        if(request.getSession().getAttribute("lastItemTimestamp") != null || request.getSession().getAttribute("firstItemTimestamp") != null)
        {
            if(request.getParameter("older") != null)
            {
                ArrayList<PostActiveRecord> postArray = null;
                if(user.startsWith("u"))
                {
                    if(path != null )
                    {
                        if(path.startsWith("/user"))
                        {
                            postArray = PostActiveRecord.findOlderPostByUserIDAndAmountAndTime(pathID, (Timestamp)request.getSession().getAttribute("lastItemTimestamp"), Integer.valueOf(request.getParameter("amount")), user);
                        }
                        else if(path.startsWith("/page"))
                        {
                            postArray = PostActiveRecord.findOlderPostByPageIDAndAmountAndTime(pathID, (Timestamp)request.getSession().getAttribute("lastItemTimestamp"), Integer.valueOf(request.getParameter("amount")), user);
                        }
                        else
                        {
                            postArray = null;
                        }
                    }
                    else
                    {
                        postArray = PostActiveRecord.findOlderPostOfFriendsAndPagesByTimeAndAmount(userID, (Timestamp)request.getSession().getAttribute("lastItemTimestamp"), Integer.valueOf(request.getParameter("amount")));
                    }
                }
                else if(user.startsWith("f"))
                {
                    postArray = PostActiveRecord.findOlderPostByPageIDAndAmountAndTime(userID, (Timestamp)request.getSession().getAttribute("lastItemTimestamp"), Integer.valueOf(request.getParameter("amount")), user);
                }
                request.setAttribute("postArray", postArray);
                if(postArray!=null && !postArray.isEmpty())
                {
                    request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
                }
                else
                { 
                    request.getSession().removeAttribute("lastItemTimestamp");
                }
                request.setAttribute("older", true);
                request.removeAttribute("newer");
            }
            else if(request.getParameter("newer") != null)
             {
                if(user.startsWith("u"))
                {
                    ArrayList<PostActiveRecord> postArray = PostActiveRecord.findNewerPostOfFriendsAndPagesByTime(userID, (Timestamp)request.getSession().getAttribute("firstItemTimestamp"));
                    request.setAttribute("postArray", postArray);
                    if(!postArray.isEmpty())
                    {
                        request.getSession().setAttribute("firstItemTimestamp", postArray.get(0).getPostTimestamp());
                    }
                }
                request.setAttribute("newer", true);  
                request.removeAttribute("older");
            }
            else
            {
                request.getSession().removeAttribute("lastItemTimestamp");
            }
        }
        return viewPage;
    }
}
