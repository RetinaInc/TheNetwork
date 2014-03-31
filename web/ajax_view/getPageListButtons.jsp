<%-- 
    Document   : getProfileButtons
    Created on : 28.03.2014, 01:28:28
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.FanpageActiveRecord"%>
<%    
    String viewingUser = (String)request.getSession().getAttribute("userID");
    FanpageActiveRecord fanPage = (FanpageActiveRecord)request.getAttribute("nextPage");
    int viewingUserID = Integer.valueOf(viewingUser.substring(1));
%>

<div class="row text-center">
    <%
        
        if(fanPage.getPageIDString().equals(viewingUser))
        {%> 
            <button type="button" onClick="location.href='/page/edit'" class="btn btn-primary">
                <span class="glyphicon glyphicon-edit"></span> Edit profile
            </button>
        <%}
        else if(viewingUser.startsWith("a"))
        {%>
            <p>
                <button type="button" onClick="resetPassword()" class="btn btn-primary">
                    <span class="glyphicon glyphicon-refresh"></span> Reset password
                </button>
                <button type="button" onClick="deleteFanpage()" class="btn btn-primary">
                    <span class="glyphicon glyphicon-remove"></span> Delete fanpage
                </button>
            </p>
        <%}
        else if(fanPage.isFollowedBy(viewingUserID))
        {%>
            <button type="button" onClick="unfollowPageList(<%=fanPage.getPageID() %>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-remove"></span> Unfollow Page
            </button>
        <%}
        else
        {%>
            <button type="button" onClick="followPageList(<%=fanPage.getPageID() %>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-plus"></span> Follow page
            </button>
        <%}%>
</div>