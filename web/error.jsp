<%-- 
    Document   : error
    Author     : Frank Steiler <frank@steiler.eu>
    Description: This page gets called whenever an unexpected error happens. It tries to provide an error description.
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Error</title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <div class="col-lg-12">
                        <div class="jumbotron">
                          <h1>An error occured.</h1>
                            <c:if test="${not empty requestScope.errorCode}"><h2>Description: <%=request.getAttribute("errorCode")%></h2></c:if>
                            <p>If this error occurs repeatedly, please contact <a href="mailto:frank@steiler.eu">Frank Steiler</a>.</p>
                        </div>
                </div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
