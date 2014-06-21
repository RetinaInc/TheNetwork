/*
 * Copyright (C) 2014 Frank Steiler <frank.steiler@steilerdev.de>
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

import activeRecord.AllFriendsActiveRecordFactory;
import activeRecord.NormalUserActiveRecordFactory;
import activeRecord.UisFriendWithUActiveRecord;
import activeRecord.UisFriendWithUActiveRecordFactory;
import commands.Command;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class FriendManagementCommand implements Command {

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
    public FriendManagementCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of managing the friends.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        String viewPage;
        if(request.getSession().getAttribute("userID") != null && request.getParameter("friend") != null)
        {
            String user = (String)request.getSession().getAttribute("userID");
            int userID = Integer.valueOf(user.substring(1));
            int friendID = Integer.valueOf(request.getParameter("friend"));
            request.setAttribute("user", NormalUserActiveRecordFactory.findUserByID(friendID).get(0));
            if(user.startsWith("u"))
            {
                if(request.getRequestURI().endsWith("/add"))
                {
                    if(!AllFriendsActiveRecordFactory.hasRejectedRequest(userID, friendID) && !AllFriendsActiveRecordFactory.openFriendshipRequest(userID, friendID) && !AllFriendsActiveRecordFactory.isFriendWith(userID, friendID))
                    {
                        UisFriendWithUActiveRecord friendRequest = new UisFriendWithUActiveRecord();
                        friendRequest.setAccepted(false);
                        friendRequest.setNotified(false);
                        friendRequest.setRejected(false);
                        friendRequest.setRequestingUser(userID);
                        friendRequest.setRespondingUser(friendID);
                        if(friendRequest.insert())
                        {
                            if(request.getRequestURI().startsWith("/ajax/user/list"))
                            {
                                viewPage = "/assets/include_message.jsp";
                                request.setAttribute("message", "Friend successfully added!");
                                request.setAttribute("messageSuccess", true);
                            }
                            else
                            {
                                viewPage = "/ajax_view/getProfileButtons.jsp";
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Problem saving the new friend request.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to send friend request (Maybe the request was rejected earlier).");
                    }
                }
                else if(request.getRequestURI().endsWith("/remove"))
                {
                    if(AllFriendsActiveRecordFactory.isFriendWith(userID, friendID))
                    {
                        ArrayList<UisFriendWithUActiveRecord> removeFriend = UisFriendWithUActiveRecordFactory.findFriendByBothUser(userID, friendID);
                        if(removeFriend.size() == 1)
                        {
                            if(removeFriend.get(0).remove())
                            {
                                if(request.getRequestURI().startsWith("/ajax/user/list"))
                                {
                                    viewPage = "/assets/include_message.jsp";
                                    request.setAttribute("message", "Friend successfully removed!");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    viewPage = "/ajax_view/getProfileButtons.jsp";
                                }
                            }
                            else
                            {
                                viewPage = "/ajax_view/error.jsp";
                                request.setAttribute("errorCode", "Unable to remove friendship.");
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Unable to load friendship.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to remove friend.");
                    }
                }
                else if (request.getRequestURI().endsWith("/reject"))
                {
                    if(AllFriendsActiveRecordFactory.openFriendshipRequest(userID, friendID))
                    {
                        ArrayList<UisFriendWithUActiveRecord> rejectedRequest = UisFriendWithUActiveRecordFactory.findFriendByBothUser(userID, friendID);
                        if(rejectedRequest.size() == 1)
                        {
                            rejectedRequest.get(0).setRejected(true);
                            rejectedRequest.get(0).setAccepted(false);
                            if(rejectedRequest.get(0).update())
                            {
                                if(request.getRequestURI().startsWith("/ajax/user/list"))
                                {
                                    viewPage = "/assets/include_message.jsp";
                                    request.setAttribute("message", "Friendship request successfully rejected!");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    viewPage = "/ajax_view/getProfileButtons.jsp";
                                }
                            }
                            else
                            {
                                viewPage = "/ajax_view/error.jsp";
                                request.setAttribute("errorCode", "Unable to remove friendship.");
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Unable to load friendship.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to reject friend request.");
                    }
                }
                else if (request.getRequestURI().endsWith("/accept"))
                {
                    if(AllFriendsActiveRecordFactory.openFriendshipRequest(userID, friendID))
                    {
                        ArrayList<UisFriendWithUActiveRecord> acceptedRequest = UisFriendWithUActiveRecordFactory.findFriendByBothUser(userID, friendID);
                        if(acceptedRequest.size() == 1)
                        {
                            acceptedRequest.get(0).setAccepted(true);
                            acceptedRequest.get(0).setRejected(false);
                            if(acceptedRequest.get(0).update())
                            {
                                if(request.getRequestURI().startsWith("/ajax/user/list"))
                                {
                                    viewPage = "/assets/include_message.jsp";
                                    request.setAttribute("message", "Friend request successfully accepted!");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    viewPage = "/ajax_view/getProfileButtons.jsp";
                                }
                            }
                            else
                            {
                                viewPage = "/ajax_view/error.jsp";
                                request.setAttribute("errorCode", "Unable to remove friendship.");
                            }
                        }
                        else
                        {
                            viewPage = "/ajax_view/error.jsp";
                            request.setAttribute("errorCode", "Unable to load friendship.");
                        }
                    }
                    else
                    {
                        viewPage = "/ajax_view/error.jsp";
                        request.setAttribute("errorCode", "Unable to reject friend request.");
                    }
                }
                else if (request.getRequestURI().endsWith("/list"))
                {
                    viewPage = "/ajax_view/error.jsp";
                    request.setAttribute("errorCode", "Feature to be implemented");
                }
                else
                {
                    viewPage = "/ajax_view/error.jsp";
                    request.setAttribute("errorCode", "Unrecognized command.");
                }
            }
            else
            {
                viewPage = "/ajax_view/error.jsp";
                request.setAttribute("errorCode", "You have insufficient rights to execute this command!");
            }
        }
        else
        {
            viewPage = "/ajax_view/error.jsp";
            request.setAttribute("errorCode", "You have insufficient rights to execute this command!");
        }
        return viewPage;
    }
    
}
