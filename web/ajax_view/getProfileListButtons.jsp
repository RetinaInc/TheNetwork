<%-- 
    Document   : getProfileButtons
    Description: This JSP is called to adjust the buttons of a user within a list, based on the connection between the viewing user and the other user.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.SysAdminActiveRecord"%>
<%@page import="activeRecord.NormalUserActiveRecord"%>
<%
    String viewingUser = (String)request.getSession().getAttribute("userID");
    NormalUserActiveRecord user = (activeRecord.NormalUserActiveRecord)request.getAttribute("nextUser");
    int viewingUserID = Integer.valueOf(viewingUser.substring(1));
%>

<div id="profileButtons" class="row text-center">
    <%
        
        if(user.getUserIDString().equals(viewingUser))
        {%> 
            <button type="button" onClick="location.href='/user/edit'" class="btn btn-primary">
                <span class="glyphicon glyphicon-edit"></span> Edit profile
            </button>
        <%}
        else if(viewingUser.startsWith("a"))
        {%>
            <p>
                <button type="button" onClick="resetPassword()" class="btn btn-sm btn-primary">
                    <span class="glyphicon glyphicon-refresh"></span> Reset password
                </button>
                <button type="button" onClick="deleteProfile()" class="btn btn-sm btn-primary">
                    <span class="glyphicon glyphicon-remove"></span> Delete user
                </button>
            </p>
            <% 
                SysAdminActiveRecord admin = SysAdminActiveRecord.findAdminByID(viewingUserID).get(0);
                if(admin.getConnectedUser() == user.getUserID())
                {%>
                    <button type="button" class="btn btn-sm btn-primary disabled">
                        <span class="glyphicon glyphicon-ok"></span> User connected to account
                    </button>
                <%} else if (admin.getConnectedUser() == 0)
                {%>
                    <button type="button" onClick="location.href='/user/connect/<%=user.getUserIDString()%>'" class="btn btn-sm btn-primary">
                        <span class="glyphicon glyphicon-plus"></span> Connect user to account
                    </button>
                <%}
            %>
        <%}
        else if(user.isFriendWith(viewingUserID))
        {%>
            <button type="button" onClick="removeFriendList(<%=user.getUserID()%>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-remove"></span> Delete as friend
            </button>
        <%}
        else if(user.sendOpenFriendshipRequest(viewingUserID))
        {%>
            <button type="button" class="btn btn-primary disabled">
                <span class="glyphicon glyphicon-time"></span> Friend request pending
            </button>
        <%}
        else if(user.receivedOpenFriendshipRequest(viewingUserID))
        {%>
            <button type="button" onClick="acceptFriendList(<%=user.getUserID()%>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-ok"></span> Accept friend request
            </button>
            <button type="button" onClick="rejectFriendList(<%=user.getUserID()%>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-remove"></span> Reject friend request
            </button>
        <%}
        else if(user.rejectedFriendshipRequest(viewingUserID))
        {%>
            <button type="button" class="btn btn-primary disabled">
                <span class="glyphicon glyphicon-time"></span> Friend request rejected
            </button>
        <%}
        else
        {%>
            <button type="button" onClick="addFriend(<%=user.getUserID()%>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-user"></span> Add as friend
            </button>
        <%}%>
</div>