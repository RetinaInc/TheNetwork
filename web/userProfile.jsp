<%-- 
    Document   : userEditProfile
    Created on : 27.03.2014, 04:52:19
    Description: This page provides the possibility for the user to edit his page.
    Author     : Frank Steiler <frank@steiler.eu>
--%>
<%@page import="activeRecord.NormalUserActiveRecord"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if(request.getAttribute("user") == null)
    {%>
    <jsp:include page="/error.jsp"></jsp:include>
    <%}
    else
    {
        NormalUserActiveRecord user = (NormalUserActiveRecord)request.getAttribute("user");
%>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - <%=user.getDisplayName() %></title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
                <div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 center-block text-center">
                    <div class="row">
                        <jsp:include page="/assets/include_message.jsp"></jsp:include>
                    </div>
                    <h1><%=user.getDisplayName()%></h1>
                    <hr>
                    <div class="well">
                        <div class="row text-center">
                            <span><img  class="img-thumbnail" 
                                            src="
                                            <%
                                                 if(pageContext.getServletContext().getResource("/pictures/" + user.getUserIDString() + ".jpg") != null)
                                                 {%>
                                                     /pictures/<%=user.getUserIDString()%>.jpg

                                                 <%}
                                                 else
                                                 {%>
                                                     /pictures/default.jpg
                                                 <%}
                                             %>" 
                                             alt="Profile Picture of <%=user.getDisplayName()%>."
                                             width="200" 
                                             height="200">
                                </span>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-lg-6">Full name</div><div class ="col-lg-6"><%=user.getFirstName() %> <%=user.getLastName() %></div>
                        </div>                     
                        <c:if test="${not empty user.getGender()}">                     
                        <hr>                     
                        <div class="row">
                            <div class="col-lg-6">Gender</div><div class ="col-lg-6"><%=user.getGender() %></div>
                        </div>
                        </c:if>
                        <hr>
                        <div class="row">
                            <div class="col-lg-6">Date of Birth</div><div class ="col-lg-6"><fmt:formatDate pattern="dd.MM.yyyy" value="<%=user.getDateOfBirth() %>"></fmt:formatDate></div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-lg-6">Address</div><div class ="col-lg-6"><%=user.getStreet()%> <%=user.getHouseNr() %> <%= user.getZip()%> <%=user.getTown() %></div>
                        </div>
                        <c:if test="${not empty user.getGender()}">  
                        <hr>
                        <div class="row">
                            <div class="col-lg-6">Relationship status</div><div class ="col-lg-6"><%=user.getRelationshipStatus()%></div>
                        </div>
                        </c:if>
                        <hr>
                        <jsp:include page="/ajax_view/getProfileButtons.jsp"></jsp:include>
                    </div>
                    <hr>
                    <h4 class="text-muted text-center"><%=user.getDisplayName() %><%if(user.getDisplayName().endsWith("s")){%>'<%}else{%>'s<%}%> posts:</h4>
                    <hr>
                    <jsp:include page="/ajax_view/getPosts.jsp"></jsp:include>
                    <hr>
                  </div>
                  <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
<%}
%>