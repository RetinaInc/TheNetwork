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
import activeRecord.FfollowsPActiveRecordFactory;
import activeRecord.PostActiveRecordFactory;
import activeRecord.UfollowsPActiveRecordFactory;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request of displaying all notifications for the current user.
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
     * This function executes the process of collecting the notifications.
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
           String user = (String)request.getSession().getAttribute("userID");
           int userID = Integer.valueOf(user.substring(1));
           
           if(user.startsWith("u"))
           {
               if(request.getRequestURI().endsWith("/dismiss"))
               {
                   String dismiss = request.getParameter("dismissPost");
                   int postID = Integer.valueOf(dismiss);
                   if(UfollowsPActiveRecordFactory.setRead(postID, userID))
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
                    viewPage = "/notificationPage.jsp";
                    ArrayList<PostActiveRecord> posts = PostActiveRecordFactory.findAllUnreadPostsByUserID(userID);
                    request.setAttribute("notificationArray", posts);
               }
           }
           else if(user.startsWith("f"))
           {
               if(request.getRequestURI().endsWith("/dismiss"))
               {
                   String dismiss = request.getParameter("dismissPost");
                   int postID = Integer.valueOf(dismiss);
                   if(FfollowsPActiveRecordFactory.setRead(postID, userID))
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
                    viewPage = "/notificationPage.jsp";
                    ArrayList<PostActiveRecord> posts = PostActiveRecordFactory.findAllUnreadPostsByPageID(userID);
                    request.setAttribute("notificationArray", posts);
               }
           }
           else
           {
               viewPage = "/error.jsp";
               request.setAttribute("errorCode", "Insufficient Rights to execute this command!");
           }
       }
       else
       {
           request.setAttribute("errorCode", "Insufficient Rights to execute this command!");
       }
       return viewPage;
    }
}
