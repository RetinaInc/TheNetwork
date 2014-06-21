<%-- 
    Document   : pageProfile
    Created on : 27.03.2014, 04:52:19
    Description: This page presents a fanpage with all his posts and information.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>
<%@page import="activeRecord.FanpageActiveRecord"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if(request.getAttribute("page") == null)
    {%>
    <jsp:include page="/error.jsp"></jsp:include>
    <%}
    else
    {
        FanpageActiveRecord fanPage = (FanpageActiveRecord)request.getAttribute("page");
%>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - <%=fanPage.getDisplayName() %></title>
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
                    <h1><%=fanPage.getDisplayName()%></h1>
                    <hr>
                    <div class="well">
                        <div class="row text-center">
                            <span><img  class="img-thumbnail" 
                                            src="
                                            <%
                                                 if(pageContext.getServletContext().getResource("/pictures/" + fanPage.getPageIDString() + ".jpg") != null)
                                                 {%>
                                                     /pictures/<%=fanPage.getPageIDString()%>.jpg

                                                 <%}
                                                 else
                                                 {%>
                                                     /pictures/default.jpg
                                                 <%}
                                             %>" 
                                             alt="Profile Picture of <%=fanPage.getDisplayName()%>."
                                             width="200" 
                                             height="200">
                                </span>
                        </div>                 
                        <%if(fanPage.getSubject() != null && !fanPage.getSubject().isEmpty()) 
                        {%>                   
                        <hr>                     
                        <div class="row">
                            <div class="col-lg-12">Subject:</div>
                        </div>
                        <div class ="row">
                            <div class ="col-lg-12"><%=fanPage.getSubject()%></div>
                        </div>
                        <%}%>
                        <hr>
                        <jsp:include page="/ajax_view/getPageButtons.jsp"></jsp:include>
                    </div>
                    <hr>
                    <h4 class="text-muted text-center"><%=fanPage.getDisplayName() %><%if(fanPage.getDisplayName().endsWith("s")){%>'<%}else{%>'s<%}%> posts:</h4>
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