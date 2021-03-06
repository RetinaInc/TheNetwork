<%-- 
    Document   : getProfileButtons
    Description: This JSP is called to adjust the buttons on a viewed profile based on the connection between the viewing user and the other user.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>

<%@page import="activeRecord.NormalUserActiveRecord"%>
<%
    String viewingUser = (String)request.getSession().getAttribute("userID");
    NormalUserActiveRecord user = (NormalUserActiveRecord)request.getAttribute("user");
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
        else if(user.isFriendWith(viewingUserID))
        {%>
            <button type="button" onClick="removeFriend(<%=user.getUserID()%>)" class="btn btn-primary">
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
            <button type="button" onClick="acceptFriend(<%=user.getUserID()%>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-ok"></span> Accept friend request
            </button>
            <button type="button" onClick="rejectFriend(<%=user.getUserID()%>)" class="btn btn-primary">
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
        <%}
    %>
</div>