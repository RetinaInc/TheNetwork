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

import commands.Command;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This command factory returns the appropriate command if an AJAX request was send.
 * @author Frank Steiler <frank@steiler.eu>
 */
public abstract class AjaxCommandFactory {

    /**
     * The servlet request.
     */
    private HttpServletRequest request;
    /**
     * The servlet response.
     */
    private HttpServletResponse response;
    
    /**
     * This function selects, based on the users request, the appropriate command.
     * @param request The servlet request.
     * @param response The servlet response
     * @return The command fitting to the request.
     */
    public static Command createCommand(HttpServletRequest request, HttpServletResponse response)
    {
        String uri = request.getRequestURI().substring(5);
        Command command;
        if(request.getSession().getAttribute("userID") != null)
        {
            if(uri.startsWith("/getPosts"))
            {
                command = new GetPostsCommand(request, response);
            }
            else if(uri.startsWith("/vote"))
            {
                command = new VotingCommand(request, response);
            }
            else if(uri.startsWith("/removePost"))
            {
                command = new RemovePostCommand(request, response);
            }
            else if(uri.startsWith("/removeComment"))
            {
                command = new RemoveCommentCommand(request, response);
            }
            else if(uri.startsWith("/user"))
            {
                command = new FriendManagementCommand(request, response);
            }
            else if(uri.startsWith("/page"))
            {
                command = new PageManagementCommand(request, response);
            } 
            else if(uri.startsWith("/getFriends"))
            {
                command = new GetFriendsCommand(request, response);
            } 
            else if(uri.startsWith("/getPages"))
            {
                command = new GetPagesCommand(request, response);
            }
            else
            {
                command = new ErrorCommand(request, response);
            }
        }
        else
        {
            command = new ErrorCommand(request, response);
        }
        return command;
    }
}
