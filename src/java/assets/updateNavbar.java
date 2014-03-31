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

package assets;

import activeRecord.FanpageActiveRecord;
import activeRecord.NormalUserActiveRecord;
import activeRecord.PostActiveRecord;
import activeRecord.SysAdminActiveRecord;
import activeRecord.UfollowsFActiveRecord;
import activeRecord.UfollowsPActiveRecord;
import activeRecord.UisFriendWithUActiveRecord;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This function updates the counter in the navbar and presents the right connected profiles.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class updateNavbar {
    
    /**
     * Executes the process of updating all counters of the Navigation bar.
     * @param request The servlet request.
     * @param response The servlet response.
     * @return 
     */
    public static boolean update(HttpServletRequest request, HttpServletResponse response)
    {
        boolean success = false;
        
        if(request.getSession().getAttribute("userID") != null)
        {
            String user = (String)request.getSession().getAttribute("userID");
            int userID = Integer.valueOf(user.substring(1));
            int administratingUser = 0;
            if(user.startsWith("u"))
            {
                int newNotification = PostActiveRecord.countAllUnreadPostsByUserID(userID);
                int newFriendRequest = NormalUserActiveRecord.countAllRequestingUser(userID);
                request.setAttribute("notificationCount", newNotification);
                request.setAttribute("requestCount", newFriendRequest);
                administratingUser = userID;
                NormalUserActiveRecord userRec = NormalUserActiveRecord.findUserByID(userID).get(0);
                request.setAttribute("currentUser", userRec.getFirstName() + " " + userRec.getLastName() + " (" + userRec.getDisplayName() + ")");
            }
            else if(user.startsWith("f"))
            {
                int newNotification = PostActiveRecord.countAllUnreadPostsByPageID(userID);
                int follower = UfollowsFActiveRecord.countFollowingByPageID(userID);
                request.setAttribute("notificationCount", newNotification);
                request.setAttribute("followerCount", follower);
                administratingUser = FanpageActiveRecord.findAdministratingUser(userID);
                request.setAttribute("currentUser", FanpageActiveRecord.findPageByID(userID).get(0).getDisplayName());
            }
            else if(user.startsWith("a"))
            {
                administratingUser = SysAdminActiveRecord.findAdministratingUser(userID);
                SysAdminActiveRecord admin = SysAdminActiveRecord.findAdminByID(userID).get(0);
                request.setAttribute("currentUser", admin.getAdminIDString()+ " - " + admin.getEmail());
            }
            
            if(administratingUser != 0)
            {
                request.setAttribute("userSet", NormalUserActiveRecord.findUserByID(administratingUser));
                request.setAttribute("fanpageSet", FanpageActiveRecord.findPagesByAdministratingUser(administratingUser));
                request.setAttribute("adminSet", SysAdminActiveRecord.findAdminsByAdministratingUser(administratingUser));
            }
        }
        return success;
    }
}
