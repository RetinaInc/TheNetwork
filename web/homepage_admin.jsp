<%-- 
    Document   : homepage_admin
    Description: This page provides the homepage of an administrator.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    int numberOfFanpages = (Integer)request.getAttribute("NoF");
    int numberOfPosts = (Integer)request.getAttribute("NoP");
    int numberOfComments = (Integer)request.getAttribute("NoC");
    int numberOfUser = (Integer)request.getAttribute("NoU");
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network</title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"> </div>
                <div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 center-block text-center">
                    <div class="row">
                        <jsp:include page="/assets/include_message.jsp"></jsp:include>
                    </div>
                    <div class="row">
                        <h1>Statistics about <em>The Network</em></h1>
                        <hr>
                    </div>
                    <div class="row well text-center">
                        <h3>User</h3>
                        <p>There are currently <%=numberOfUser %> user using <em>The Network</em>.</p>
                    </div>
                    <hr>
                    <div class="row well text-center">
                        <h3>Fanpages</h3>
                        <p>There are currently <%=numberOfFanpages %> fanpages subscribed to <em>The Network</em>.</p>
                    </div>
                    <hr>
                    <div class="row well text-center">
                        <h3>Posts</h3>
                        <p>There have been <%=numberOfPosts %> posts published on <em>The Network</em> so far.</p>
                    </div>
                    <hr>
                    <div class="row well text-center">
                        <h3>Comments</h3>
                        <p>User published <%=numberOfComments %> comments on <em>The Network</em>.</p>
                    </div>
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"> </div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
