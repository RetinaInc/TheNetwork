<%-- 
    Document   : include_navbar
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
    Description: This page is included at the top of all other pages to add the correct navbar, depending on the user who is logged in.
--%>
<%@page import="activeRecord.NormalUserActiveRecord"%>
<%@page import="activeRecord.FanpageActiveRecord"%>
<%@page import="activeRecord.SysAdminActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%
    String user = (String)session.getAttribute("userID");
    int notification = 0;
    int requestCount = 0;
    int follower = 0;
    ArrayList<SysAdminActiveRecord> adminSet= null;
    ArrayList<FanpageActiveRecord> fanpageSet = null;
    ArrayList<NormalUserActiveRecord> userSet = null;
    String currentUser = user;
    if(request.getAttribute("notificationCount") != null)
    {
        notification = (Integer)request.getAttribute("notificationCount");
    }
    if(request.getAttribute("requestCount") != null)
    {
        requestCount = (Integer)request.getAttribute("requestCount");
    }
    if(request.getAttribute("followerCount") != null)
    {
        follower = (Integer)request.getAttribute("followerCount");
    }
    if(request.getAttribute("currentUser") != null)
    {
        currentUser = (String)request.getAttribute("currentUser");
    }
    if(request.getAttribute("fanpageSet") != null && request.getAttribute("adminSet") != null && request.getAttribute("userSet") != null)
    {
        adminSet= (ArrayList<SysAdminActiveRecord>)request.getAttribute("adminSet");
        fanpageSet = (ArrayList<FanpageActiveRecord>)request.getAttribute("fanpageSet");
        userSet = (ArrayList<NormalUserActiveRecord>)request.getAttribute("userSet");
    }
%>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
          <a href="/" class="navbar-brand">The Network</a>
          <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
        </div>
        <div class="navbar-collapse collapse" id="navbar-main">
          <ul class="nav navbar-nav">
              <%
                  //This function detects which usertype is currently logged in and presents the items in the navigation bar.
                  if(user != null)
                  {
                      if(user.startsWith("u"))
                      {%>
                            <li>
                                <a href="/notifications">Notifications <%=notification>0 ? "<span class=\"badge-inverse\">" + notification + "</span>" : "" %></a>
                            </li>
                            <li>
                                <a href="/user">Friends <%=requestCount>0 ? "<span class=\"badge-inverse\">" + requestCount + "</span>" : "" %></a>
                            </li>
                            <li>
                                <a href="/page">Fanpages</a>
                            </li>
                      <%} 
                      else if (user.startsWith("f"))
                      {%>
                            <li>
                                <div class="navbar-text">Your fanpage has <%=follower>0 ? follower : "no" %> followers</div>
                            </li>
                            <li>
                                <a href="/notifications">Notifications <%=notification>0 ? "<span class=\"badge-inverse\">" + notification + "</span>" : "" %></a>
                            </li>
                      <%} 
                      else if (user.startsWith("a"))
                      {%>
                            <li>
                                <a href="/user">Manage user</a>
                            </li>
                            <li>
                                <a href="/page">Manage fanpages</a>
                            </li>
                      <%}
                  }%>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <%
                  if(user != null)
                  {%>
                      <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#" id="profile-selector"><%=currentUser %><span class="caret"></span></a>
                            <ul class="dropdown-menu text-left">
                            <% 
                                if(adminSet != null && fanpageSet != null && userSet != null)
                                {%>
                                    <%if(!userSet.isEmpty())
                                    {%>
                                        <cst:ArrayListJSP itemList="<%=userSet%>" itemName="nextItem">
                                        <% 
                                             NormalUserActiveRecord userRec = (NormalUserActiveRecord)pageContext.getAttribute("nextItem");
                                        %>

                                        <li><a href="/changeProfile/<%=userRec.getUserIDString() %>"><%=userRec.getFirstName() + " " + userRec.getLastName() + " (" + userRec.getDisplayName() + ")"%></a></li>

                                        </cst:ArrayListJSP>
                                        <li class="divider"></li>
                                    <%}
                                    if(!fanpageSet.isEmpty())
                                    {%>
                                        <cst:ArrayListJSP itemList="<%=fanpageSet%>" itemName="nextItem">
                                        <% 
                                             FanpageActiveRecord pageRec = (FanpageActiveRecord)pageContext.getAttribute("nextItem");
                                        %>

                                        <li><a href="/changeProfile/<%=pageRec.getPageIDString()%>"><%=pageRec.getDisplayName()%></a></li>

                                        </cst:ArrayListJSP>
                                        <li class="divider"></li>
                                    <%}
                                    if(!adminSet.isEmpty())
                                    {%>
                                        <cst:ArrayListJSP itemList="<%=adminSet%>" itemName="nextItem">
                                        <% 
                                             SysAdminActiveRecord adminRec = (SysAdminActiveRecord)pageContext.getAttribute("nextItem");
                                        %>

                                        <li><a href="/changeProfile/<%=adminRec.getAdminIDString()%>"><%=adminRec.getAdminIDString()+ " - " + adminRec.getEmail()%></a></li>

                                        </cst:ArrayListJSP>
                                        <li class="divider"></li>
                                    <%}}%>
                                <li><a href="/changeProfile/logout">Logout</a></li>
                            </ul>
                    </li>
                    <%
                        if(user.startsWith("u"))
                        {%>
                        <li><a href="/user/edit">Edit profile</a></li>
                        <%}
                        else if(user.startsWith("f"))
                        {%>
                        <li><a href="/page/edit">Edit page</a></li>
                        <%}%>
                  <%}
                  else
                  {%>
                    <li>
                        <form class="navbar-form" action="/login" method="post">
                            <div class="form-group">
                                <input type="text" class="form-control" name="userLogin" placeholder="E-Mail or UserID">
                                <input type="password" class="form-control" name="passwordLogin" placeholder="Password">
                            </div>
                            <button type="submit" class="btn btn-primary">Login</button>
                        </form>
                   </li>
                  <%}
            %>
          </ul>
        </div>
      </div>
</div>
