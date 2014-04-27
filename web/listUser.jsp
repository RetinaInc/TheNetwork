<%-- 
    Document   : listFriends
    Description: This page will present a list of all user.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - User list</title>
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
                    <form class="form-horizontal">
                        <div class="row">
                            <div class="col-lg-10">
                                <div class="form-group has-feedback">
                                    <input id="searchField" type="text" placeholder="Find user..." class="form-control"/>
                                    <span class="glyphicon glyphicon-search form-control-feedback"></span>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                <button type="submit" class="btn btn-primary">Search</button>
                            </div>
                        </div>
                    </form>
                    <div class="row">
                        <hr>
                        <h4> All user</h4>
                        <hr>
                        <% 
                                request.setAttribute("listArray", request.getAttribute("userArray"));
                        %>
                        <jsp:include page="/ajax_view/getUserListItem.jsp"></jsp:include>
                    </div>  
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"> </div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
