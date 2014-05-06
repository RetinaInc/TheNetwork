<%-- 
    Document   : getPageButtons
    Description: This JSP is called to adjust the buttons on a viewed fanpage based on the connection between the viewing user and the fanpage.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.FanpageActiveRecord"%>
<%
    String viewingUser = (String)request.getSession().getAttribute("userID");
    FanpageActiveRecord fanPage = (FanpageActiveRecord)request.getAttribute("page");
    int viewingUserID = Integer.valueOf(viewingUser.substring(1));
%>

<div id="pageButtons" class="row text-center">
    <%
        if(fanPage.getPageIDString().equals(viewingUser))
        {%> 
            <button type="button" onClick="location.href='/page/edit'" class="btn btn-primary">
                <span class="glyphicon glyphicon-edit"></span> Edit page
            </button>
        <%}
        else if(fanPage.isFollowedBy(viewingUserID))
        {%>
            <button type="button" onClick="unfollowPage(<%=fanPage.getPageID() %>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-remove"></span> Unfollow page
            </button>
        <%}
        else
        {%>
            <button type="button" onClick="followPage(<%=fanPage.getPageID() %>)" class="btn btn-primary">
                <span class="glyphicon glyphicon-plus"></span> Follow page
            </button>
        <%}
    %>
</div>