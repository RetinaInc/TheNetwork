<%-- 
    Document   : listPages#
    Description: This page will present a list of all followed fanpages of the current user.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Fanpage list</title>
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
                    <form class="form-horizontal">
                        <div class="row">
                            <div class="col-lg-10">
                                <div class="form-group has-feedback">
                                    <input type="text" placeholder="Find page..." class="form-control"/>
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
                        <h4>All followed pages</h4>
                        <% 
                            request.setAttribute("listPageArray", request.getAttribute("pageArray"));
                        %>
                        <jsp:include page="/ajax_view/getPageListItem.jsp"></jsp:include>
                        <hr>
                    </div>  
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
