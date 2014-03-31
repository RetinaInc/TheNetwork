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

import activeRecord.PostActiveRecord;
import activeRecord.UfollowsPActiveRecord;
import activeRecord.FfollowsPActiveRecord;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Frank Steiler <frank@steiler.eu>
 */
public class NotificationsCommand implements Command{

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
    public NotificationsCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of creating a new fanpage. It validates the input and creates a new fanpage if the input is valid.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
       String viewPage = "/error.jsp";
       if(request.getSession().getAttribute("userID") != null)
       {
           viewPage = "/notificationPage.jsp";
           String user = (String)request.getSession().getAttribute("userID");
           int userID = Integer.valueOf(user.substring(1));
           ArrayList<PostActiveRecord> posts = null;
           if(user.startsWith("u"))
           {
               if(request.getRequestURI().endsWith("/dismiss"))
               {
                   String dismiss = request.getParameter("dismissPost");
                   int postID = Integer.valueOf(dismiss);
                   if(UfollowsPActiveRecord.setRead(postID, userID))
                   {
                       viewPage = "/assets/include_message.jsp";
                       request.setAttribute("message", "Notification successfully dismissed.");
                       request.setAttribute("messageSuccess", true);
                   }
                   else
                   {
                       viewPage = "/assets/include_message.jsp";
                       request.setAttribute("message", "Problem dismissing the notification");
                       request.setAttribute("messageError", true);
                   }
               }
               else
               {
                    posts = PostActiveRecord.findAllUnreadPostsByUserID(userID);
               }
           }
           else if(user.startsWith("f"))
           {
               if(request.getRequestURI().endsWith("/dismiss"))
               {
                   String dismiss = request.getParameter("dismissPost");
                   int postID = Integer.valueOf(dismiss);
                   if(FfollowsPActiveRecord.setRead(postID, userID))
                   {
                       viewPage = "/assets/include_message.jsp";
                       request.setAttribute("message", "Notification successfully dismissed.");
                       request.setAttribute("messageSuccess", true);
                   }
                   else
                   {
                       viewPage = "/assets/include_message.jsp";
                       request.setAttribute("message", "Problem dismissing the notification");
                       request.setAttribute("messageError", true);
                   }
               }
               else
               {
                    posts = PostActiveRecord.findAllUnreadPostsByPageID(userID);
               }
           }
           else
           {
               viewPage = "/error.jsp";
               request.setAttribute("errorCode", "Insufficient Rights to execute this command!");
           }
           request.setAttribute("notificationArray", posts);
       }
       else
       {
           request.setAttribute("errorCode", "Insufficient Rights to execute this command!");
       }
       return viewPage;
    }
    
}
