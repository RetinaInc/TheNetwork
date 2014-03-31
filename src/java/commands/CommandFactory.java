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

import commands.ajax.AjaxCommandFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class creates the appropriate command depending on the user request.
 * @author Frank Steiler <frank@steiler.eu>
 */
public abstract class CommandFactory
{
    /**
     * This function selects, based on the users request, the appropriate command.
     * @param request The servlet request.
     * @param response The servlet response
     * @return The command fitting to the request.
     */
    public static Command createCommand(HttpServletRequest request, HttpServletResponse response)
    {
        Command command = null;
        
        if(request.getSession().getAttribute("userID") != null)
        {
            if(request.getRequestURI().startsWith("/ajax"))
            {
                command = AjaxCommandFactory.createCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/changeProfile"))
            {
                command = new ChangeProfileCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/postStatus"))
            {
                command = new PostStatusCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/postComment"))
            {
                command = new PostCommentCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/post"))
            {
                command = new PostPageCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/user"))
            {
                command = new UserCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/page"))
            {
                command = new PageCommand(request, response);
            }
            else if(request.getRequestURI().startsWith("/notifications"))
            {
                command = new NotificationsCommand(request, response);
            }
            else
            {
                command = new HomepageCommand(request, response);
            }
        } 
        else
        {
            if(request.getRequestURI().startsWith("/signup/user"))
            {
                command = new UserSignUpCommand(request, response);
            } 
            else if(request.getRequestURI().startsWith("/signup/page"))
            {
                command = new PageSignUpCommand(request, response);
            } 
            else if(request.getRequestURI().startsWith("/login"))
            {
                command = new LoginCommand(request, response);
            } 
            else if(request.getRequestURI().startsWith("/ajax"))
            {
                command = AjaxCommandFactory.createCommand(request, response);
            }
            else
            {
                command = new WelcomeCommand();
            }
        }
        return command;
    }
}
