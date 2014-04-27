<%-- 
    Document   : notificationPage
    Description: This page will display all notifications of a user.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Notifications</title>
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
                    <div class="row">
                        <hr>
                        <h4>New activities on posts you follow:</h4>
                        <hr>
                        <jsp:include page="/ajax_view/getNotificationItem.jsp"></jsp:include>
                    </div>  
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>

