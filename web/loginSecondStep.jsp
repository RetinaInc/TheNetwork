<%-- 
    Document   : loginSecondStep
    Description: This server page is called if the log in failed because the password was incorrect or the email address is used by several accounts.
    Created on : 22.03.2014, 20:53:17
    Author     : Frank Steiler <frank@steiler.eu>
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Login</title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <div class="col-lg-12 center-block text-center">
                    <div class="row">
                        <div class="jumbotron">
                            <c:choose>
                                <c:when test="${not empty requestScope.failure}">
                                    <h2>Your provided credentials have been wrong.</h2>
                                    <p>Please re-enter them.</p>
                                </c:when>

                                <c:otherwise>
                                    <h2>The system detected several accounts attached to your E-Mail</h2>
                                    <p>Please select one account and re-enter your password.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="row">
                        <c:choose>
                            <c:when test="${requestScope.failure == false}">
                                <div class="well bs-component">
                                    <div class="row">
                                        <form class="form-horizontal" action="/login" method="post">
                                            <div class="form-group">
                                                <label for="userLogin" class="col-lg-3 control-label">E-Mail or UserID</label>
                                                <div class="col-lg-6">
                                                    <input type="text" class="form-control" id="userLogin" name="userLogin" placeholder="E-Mail or UserID" <c:if test="${not empty param.userLogin}"> value="<%=request.getParameter("userLogin")%>" </c:if>>
                                                </div>
                                                <div class="col-lg-3">
                                                    <p></p>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label for="passwordLogin" class="col-lg-3 control-label">Password</label>
                                                <div class="col-lg-6">
                                                    <input type="password" class="form-control" id="passwordLogin" name="passwordLogin" placeholder="Password">
                                                </div>
                                                <div class="col-lg-3 text-left">
                                                    <button type="submit" class="btn btn-primary">Login</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <c:if test="${not empty requestScope.admin}">
                                    <div class="well bs-component">
                                    <h4>System Administrator:</h4>
                                    <cst:ArrayListJSP itemList="<%=request.getAttribute("admin")%>" itemName="nextUser">
                                        <% activeRecord.SysAdminActiveRecord userAdmin = (activeRecord.SysAdminActiveRecord)pageContext.getAttribute("nextUser");%>
                                        <hr>
                                        <div class="row">
                                            <div class="col-lg-2">
                                                <span><img class="img-thumbnail" 
                                                           src="
                                                           <%
                                                                if(pageContext.getServletContext().getResource("/pictures/" + userAdmin.getAdminIDString()+ ".jpg") == null)
                                                                {%>
                                                                    /pictures/default.jpg
                                                                <%}
                                                                else
                                                                {%>
                                                                    /pictures/<%=userAdmin.getAdminIDString()%>.jpg
                                                                <%}
                                                            %>"
                                                           width="75" 
                                                           height="75">
                                                </span>
                                            </div>
                                            <div class="col-lg-3">
                                                 <div class="clearfix text-center">
                                                     <p>AdminID: <%=userAdmin.getAdminIDString()%></p> 
                                                     <p>E-Mail: <%=userAdmin.getEmail()%></p>
                                                </div>
                                            </div>
                                            <div class="col-lg-7">
                                                <form class="form-horizontal" action="/login" method="post">
                                                    <div class="form-group">
                                                        <label for="password" class="col-lg-3 control-label">Password</label>
                                                        <div class="col-lg-6">
                                                            <input type="password" class="form-control" id="password" name="passwordLogin" placeholder="Password">
                                                            <input type="hidden" name="userLogin" value="<%=userAdmin.getAdminIDString()%>">
                                                        </div>
                                                        <div class="col-lg-3">
                                                            <button type="submit" class="btn btn-primary">Login</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </cst:ArrayListJSP>
                                    </div>
                                </c:if>

                                <c:if test="${not empty requestScope.user}">
                                    <div class="well bs-component">
                                    <h4>Normal User:</h4>
                                    <cst:ArrayListJSP itemList="<%=request.getAttribute("user")%>" itemName="nextUser">
                                        <% activeRecord.NormalUserActiveRecord userNormal = (activeRecord.NormalUserActiveRecord)pageContext.getAttribute("nextUser");%>
                                        <hr>
                                        <div class="row">
                                            <div class="col-lg-2">
                                                <span><img class="img-thumbnail" 
                                                           src="
                                                           <%
                                                                if(pageContext.getServletContext().getResource("/pictures/" + userNormal.getUserIDString()+ ".jpg") == null)
                                                                {%>
                                                                    /pictures/default.jpg
                                                                <%}
                                                                else
                                                                {%>
                                                                /pictures/<%=userNormal.getUserIDString()%>.jpg
                                                                <%}
                                                            %>"
                                                           width="75" 
                                                           height="75">
                                                </span>
                                            </div>
                                            <div class="col-lg-3">
                                                 <div class="clearfix text-center">
                                                     <p>Display Name: <%=userNormal.getDisplayName()%></p>
                                                     <p>Real Name: <%=userNormal.getFirstName()%> <%=userNormal.getLastName()%></p>
                                                </div>
                                            </div>
                                            <div class="col-lg-7">
                                                <form class="form-horizontal" action="/login" method="post">
                                                    <div class="form-group">
                                                        <label for="password" class="col-lg-3 control-label">Password</label>
                                                        <div class="col-lg-6">
                                                            <input type="password" class="form-control" id="password" name="passwordLogin" placeholder="Password">
                                                            <input type="hidden" name="userLogin" value="<%=userNormal.getUserIDString()%>">
                                                        </div>
                                                        <div class="col-lg-3">
                                                            <button type="submit" class="btn btn-primary">Login</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </cst:ArrayListJSP>
                                    </div>
                                </c:if>

                                <c:if test="${not empty requestScope.page}">
                                    <div class="well bs-component">
                                    <h4>Fanpage:</h4>
                                    <cst:ArrayListJSP itemList="<%=request.getAttribute("page") %>" itemName="nextUser">
                                        <% activeRecord.FanpageActiveRecord userPage = (activeRecord.FanpageActiveRecord)pageContext.getAttribute("nextUser");%>
                                        <hr>
                                        <div class="row">
                                            <div class="col-lg-2">
                                                <span><img class="img-thumbnail" 
                                                           src="
                                                           <%
                                                                if(pageContext.getServletContext().getResource("/pictures/" + userPage.getPageIDString()+ ".jpg") == null)
                                                                {%>
                                                                    /pictures/default.jpg
                                                                <%}
                                                                else
                                                                {%>
                                                                /pictures/<%=userPage.getPageIDString()%>.jpg
                                                                <%}
                                                            %>"
                                                           width="75" 
                                                           height="75">
                                                </span>
                                            </div>
                                            <div class="col-lg-3">
                                                 <div class="clearfix text-center">
                                                     <p>Display Name: <%=userPage.getDisplayName()%></p>
                                                     <p>Page Name: <%=userPage.getPageName()%></p>
                                                </div>
                                            </div>
                                            <div class="col-lg-7">
                                                <form class="form-horizontal" action="/login" method="post">
                                                    <div class="form-group">
                                                        <label for="password" class="col-lg-3 control-label">Password</label>
                                                        <div class="col-lg-6">
                                                            <input type="password" class="form-control" id="password" name="passwordLogin" placeholder="Password">
                                                            <input type="hidden" name="userLogin" value="<%=userPage.getPageIDString()%>">
                                                        </div>
                                                        <div class="col-lg-3">
                                                            <button type="submit" class="btn btn-primary">Login</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </cst:ArrayListJSP>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
