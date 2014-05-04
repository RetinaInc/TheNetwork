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

package activeRecord;

/**
 * This class represents a row of the AllFriends view and provides functions to check if two users are friends or not.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class AllFriendsActiveRecord extends DatabaseUtility {
    
    private int currentUser;
    private int friend;
    private boolean accepted;
    private boolean rejected;
    
    /**
     * @return The currentUser.
     */
    public int getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser The currentUser to set.
     */
    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return The friend.
     */
    public int getFriend() {
        return friend;
    }

    /**
     * @param friend The friend to set.
     */
    public void setFriend(int friend) {
        this.friend = friend;
    }

    /**
     * @return The accepted flag.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * @param accepted The accepted flag to set.
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    /**
     * @return The rejected flag.
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * @param rejected The rejected flag to set.
     */
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }
}
