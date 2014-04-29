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
import activeRecord.FanpageActiveRecord;
import activeRecord.CommentActiveRecordFactory;
import activeRecord.FanpageActiveRecordFactory;
import activeRecord.NormalUserActiveRecord;
import activeRecord.NormalUserActiveRecordFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import activeRecord.PostActiveRecord;
import activeRecord.PostActiveRecordFactory;
import java.util.ArrayList;

/**
 * This class processes the request to show the homepage of a user after the log-in.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class HomepageCommand implements Command{

    private HttpServletRequest request;
    private HttpServletResponse response;
    
    /**
     * Create a new command.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public HomepageCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of loading the needed data for the homepage of the user.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
        String user = (String)request.getSession().getAttribute("userID");
        int userID = Integer.valueOf(user.substring(1));
        String viewPage = "/homepage.jsp";
        if(user.startsWith("u"))
        {
            //Collect data for a normal user
            ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostOfFriendsAndPagesByAmount(userID, 15);
            request.setAttribute("postArray", postArray);
            if(!postArray.isEmpty())
            {
                request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
                request.getSession().setAttribute("firstItemTimestamp", postArray.get(0).getPostTimestamp());
            }
            else
            {
                request.getSession().removeAttribute("lastItemTimestamp");
            }
        } 
        else if (user.startsWith("f"))
        {
            //Collect data for a fanpage admin
            ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostByPageIDAndAmount(userID, 15, user);
            request.setAttribute("postArray", postArray);
            if(!postArray.isEmpty())
            {
                request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
            }
            else
            {
                request.getSession().removeAttribute("lastItemTimestamp");
            }
        }
        else if (user.startsWith("a"))
        {
            //Collect data for a system administrator.
            int numberOfFanpages = FanpageActiveRecordFactory.countFanpages();
            int numberOfPosts = PostActiveRecordFactory.countPosts();
            int numberOfComments = CommentActiveRecordFactory.countComments();
            int numberOfUser = NormalUserActiveRecordFactory.countUser();
            request.setAttribute("NoF", numberOfFanpages);
            request.setAttribute("NoP", numberOfPosts);
            request.setAttribute("NoC", numberOfComments);
            request.setAttribute("NoU", numberOfUser);
            viewPage="/homepage_admin.jsp";
        }
        else
        {
            throw new ServletException();
        }
        //to display the "load more post" button on the homepage
        request.setAttribute("older", true);
        return viewPage;
    }
    
}
