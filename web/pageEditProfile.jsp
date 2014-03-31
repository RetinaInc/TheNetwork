<%-- 
    Document   : userEditProfile
    Created on : 27.03.2014, 04:52:19
    Description: This page provides the possibility for the user to edit his page.
    Author     : Frank Steiler <frank@steiler.eu>
--%>
<%@page import="activeRecord.FanpageActiveRecord"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if(request.getAttribute("page") == null)
    {
        request.setAttribute("errorCode", "Could not find the data for the view.");
    %>
    <jsp:include page="/error.jsp"></jsp:include>
    <%}
    else
    {
        FanpageActiveRecord fanpage = (FanpageActiveRecord)request.getAttribute("page");
%>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Edit fanpage information</title>
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
                    <h1><%=fanpage.getDisplayName()%></h1>
                    <hr>
                    <div class="well">
                    <div class="row text-center">
                        <span><img  class="img-thumbnail" 
                                        src="
                                        <%
                                             if(pageContext.getServletContext().getResource("/pictures/" + fanpage.getPageIDString()+ ".jpg") != null)
                                             {%>
                                                 /pictures/<%=fanpage.getPageIDString()%>.jpg

                                             <%}
                                             else
                                             {%>
                                                 /pictures/default.jpg
                                             <%}
                                         %>" 
                                         alt="Page Picture of <%=fanpage.getDisplayName()%>."
                                         width="200" 
                                         height="200">
                            </span>
                    </div>
                    <p></p>
                    <div class="row text-center">
                        <button type="button" onClick="changePagePicture()" class="btn btn-primary">
                            <span class="glyphicon glyphicon-picture"></span> Change fanpage picture.
                        </button>
                        <button type="button" onClick="deletePagePicture()" class="btn btn-primary">
                            <span class="glyphicon glyphicon-remove"></span> Delete fanpage picture.
                        </button>
                    </div>
                    <p></p>
                    <div class="row text-center">
                        <button type="button" onClick="deleteFanpage()" class="btn btn-danger">
                            <span class="glyphicon glyphicon-warning-sign"></span> Delete fanpage
                        </button>
                    </div>
                    </div>
                    
                    <div class="well bs-component">
                        <form class="form-horizontal" action="/page/submit" method="post">
                        <fieldset>
                          <legend>Change personal information</legend>
                          <div class="form-group <c:if test="${requestScope.emailErrorPage == true}">has-error</c:if>">
                            <label for="newEmail" class="col-lg-3 control-label">Email</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="newEmailPage" id="newtEmail" placeholder="Email" value="<%=fanpage.getEmail() %>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.fanpageNameError == true}">has-error</c:if>">
                            <label for="fanpageName" class="col-lg-3 control-label">Fanpage Name</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="fanpageName" id="fanpageName" placeholder="Fanpage Name" value="<%=fanpage.getPageName() %>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.displayNameErrorPage == true}">has-error</c:if>">
                            <label for="displayNamePage" class="col-lg-3 control-label">Display Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="displayNamePage" id="displayNamePage" placeholder="Page (Max. 8 character)" value="<%=fanpage.getDisplayName() %>">
                            </div>
                          </div> 
                          <div class="form-group <c:if test="${requestScope.subjectErrorPage == true}">has-error</c:if>">
                            <label for="subjectPage" class="col-lg-3 control-label">Subject/Short Description of the fanpage</label>
                            <div class="col-lg-9">
                                <textarea class="form-control" rows="3" id="subjectPage" name="subjectPage"><%if(fanpage.getSubject()!= null){%><%=fanpage.getSubject() %><%}%></textarea>
                            </div>
                          </div>   
                          <div class="form-group <c:if test="${requestScope.inputPasswordError == true}">has-error</c:if>">
                            <label for="oldPassword" class="col-lg-3 control-label">Old Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="oldPassword" id="oldPassword" placeholder="Old Password">
                            </div>
                            <label for="newPassword" class="col-lg-3 control-label">New Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="New Password">
                            </div>
                            <label for="newPasswordRe" class="col-lg-3 control-label">Retype Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="newPasswordRe" id="newPasswordRe" placeholder="Retype Password">
                            </div>
                          </div>
                          <div class="form-group">
                            <div class="col-lg-12 text-center">
                              <button type="submit" class="btn btn-primary">Submit</button>
                              <button type="button" class="btn btn-primary" onClick="location.href='/page/edit'">Discard</button>
                            </div>
                          </div>
                        </fieldset>
                      </form>
                    </div>
                    <div class="well bs-component">
                        <form class="form-horizontal" action="/page/connect" method="post">
                            <fieldset>
                              <legend>Connect to an user</legend>
                              <div class="form-group <c:if test="${requestScope.ConnectionError == true}">has-error</c:if>">
                                <label for="AdministratingUser" class="col-lg-3 control-label">Administrating User</label>
                                <div class="col-lg-9">
                                    <input type="text" class="form-control" name="AdministratingUser" id="AdministratingUser" placeholder="Enter the userID of the user" <%if(fanpage.getAdministratingUser()!= 0){%>value="u<%=fanpage.getAdministratingUser()%>" disabled<%}%>>
                                </div>
                              </div>       
                              <div class="form-group">
                                <div class="col-lg-12 text-center">
                                  <button type="submit" class="btn btn-primary <%if(fanpage.getAdministratingUser()!= 0){%>disabled<%}%>">Submit</button>
                                </div>
                              </div>
                            </fieldset>
                        </form>
                    </div>
                    <hr>
                    <h4 class="text-muted text-center">Your posts:</h4>
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
<%}%>