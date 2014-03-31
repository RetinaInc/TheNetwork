<%-- 
    Document   : homepage
    Created on : 23.03.2014, 22:55:29
    Description: This page provides the homepage of every user, presenting the most recent posts of friends.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
                <div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 center-block text-center">
                    <div class="row">
                        <jsp:include page="/assets/include_message.jsp"></jsp:include>
                    </div>
                    <div id="updateStatus" class="row">
                        <div class="well bs-component">
                            <form class="form-horizontal" action="/postStatus" method="post">
                                <fieldset>
                                <legend>Update your status here</legend>
                                <div class="row center-block text-center">
                                <div class="col-lg-12">
                                    <textarea class="form-control" rows="3" id="textArea" name ="newStatus" placeholder="Status"></textarea>
                                </div>
                                </div>
                                <p></p>
                                <div class="row center-block text-center">
                                <div class="col-lg-8"></div>
                                <div class="col-lg-2 text-center">
                                    <div class="form-group">
                                        <select name="postPublic" class="form-control" id="select">
                                            <% if(((String)request.getSession().getAttribute("userID")).startsWith("u")){%> <option>Private</option> <%}%>
                                          <option>Public</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-lg-2 text-center">
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">Submit</button>
                                    </div>
                                </div>
                                </div>
                               </fieldset>
                            </form>
                        </div>
                    </div>
                    <jsp:include page="/ajax_view/getPosts.jsp"></jsp:include>             
                    <hr>
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
        <% if(((String)request.getSession().getAttribute("userID")).startsWith("u"))
        {%>
        <script>
            $(document).ready(
                    setInterval(function(){getNewPosts();},10000)
                    );
        </script>
        <%}%>
    </body>
</html>
