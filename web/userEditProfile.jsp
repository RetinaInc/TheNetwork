<%-- 
    Document   : userEditProfile
    Created on : 27.03.2014, 04:52:19
    Description: This page provides the possibility for the user to edit his page.
    Author     : Frank Steiler <frank@steiler.eu>
--%>
<%@page import="activeRecord.FanpageActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@page import="activeRecord.SysAdminActiveRecord"%>
<%@page import="activeRecord.NormalUserActiveRecord"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
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
        <link rel="stylesheet" href="/css/datepicker.css" type="text/css"/>
        <script type="text/javascript" src="/javascript/bootstrap-datepicker.js"></script>
        <title>The Network - Edit user profile</title>
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
                    <p></p>
                    <div class="row text-center">
                        <button type="button" onClick="changeProfilePicture()" class="btn btn-primary">
                            <span class="glyphicon glyphicon-picture"></span> Change profile picture
                        </button>
                        <button type="button" onClick="deleteProfilePicture()" class="btn btn-primary">
                            <span class="glyphicon glyphicon-remove"></span> Delete profile picture
                        </button>
                    </div>
                    <p></p>
                    <div class="row text-center">
                        <button type="button" onClick="deleteProfile()" class="btn btn-danger">
                            <span class="glyphicon glyphicon-warning-sign"></span> Delete profile
                        </button>
                    </div>
                    </div>
                    
                    <div class="well bs-component">
                        <form class="form-horizontal" action="/user/submit" method="post">
                        <fieldset>
                          <legend>Change personal information</legend>
                          <div class="form-group <c:if test="${requestScope.emailError == true}">has-error</c:if>">
                            <label for="newEmail" class="col-lg-3 control-label">Email</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="newEmail" id="newEmail" placeholder="Email" value="<%=user.getEmail()%>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.firstNameError == true}">has-error</c:if>">
                            <label for="firstName" class="col-lg-3 control-label">First Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="firstName" id="firstName" placeholder="John" value="<%=user.getFirstName()%>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.lastNameError == true}">has-error</c:if>">
                            <label for="lastName" class="col-lg-3 control-label">Last Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="lastName" id="lastName" placeholder="Doe" value="<%=user.getLastName()%>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.displayNameError == true}">has-error</c:if>">
                            <label for="displayName" class="col-lg-3 control-label">Display Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="displayName" id="displayName" placeholder="John (Max. 8 character)" value="<%=user.getDisplayName() %>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.streetError == true || requestScope.streetNrError == true}">has-error</c:if>">
                            <label for="street" class="col-lg-3 control-label">Street & Nr.</label>
                            <div class="col-lg-7">
                              <input type="text" class="form-control" name="street" id="street" placeholder="Jumpstreet" value="<%=user.getStreet() %>">
                            </div>
                            <div class="col-lg-2">
                              <input type="text" class="form-control" name="streetNr" id="streetNr" placeholder="21" value="<%=user.getHouseNr() %>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.zipError == true || requestScope.townError == true}">has-error</c:if>">
                            <label for="zip" class="col-lg-3 control-label">Zip-Code & Town</label>
                            <div class="col-lg-3">
                              <input type="text" class="form-control" name="zip" id="zip" placeholder="1234" value="<%=user.getZip() %>">
                            </div>
                            <div class="col-lg-6">
                              <input type="text" class="form-control" name="town" id="town" placeholder="Boston" value="<%=user.getTown() %>">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.DoBError == true}">has-error</c:if>">
                              <label for="DoB" class="col-lg-3 control-label">Date of Birth</label>
                              <div class="input-append date" id="DoB" data-date="12-02-2012" data-date-format="dd-mm-yyyy">
                                  <div class="col-sm-7">
                                    <input class="form-control" name="DoB" type="text" placeholder="12-02-2012" value="<fmt:formatDate pattern="dd-MM-yyyy" value="<%=user.getDateOfBirth() %>" />">
                                  </div>
                                  <div class="col-sm-2 text-center">
                                    <span class="add-on"><button type="button" class="btn btn-primary"><span class="glyphicon glyphicon-calendar"></span></button></span>
                                  </div>
                              </div>
                          </div>
                          <div class="form-group">
                            <label for="gender" class="col-lg-3 control-label">Gender</label>
                            <div class="col-lg-9">
                                <select class="form-control" id="gender" name="gender">
                                    <option <c:if test="${empty user.getGender()}">selected="selected"</c:if>></option>
                                    <option <c:if test="${user.getGender() == 'Male'}">selected="selected"</c:if>>Male</option>
                                    <option <c:if test="${user.getGender() == 'Female'}">selected="selected"</c:if>>Female</option>
                                </select>
                            </div>
                          </div>  
                          <div class="form-group">
                            <label for="relationshipStatus" class="col-lg-3 control-label">Relationship Status</label>
                            <div class="col-lg-9">
                                <select class="form-control" id="relationshipStatus" name="relationshipStatus">
                                    <option <c:if test="${empty user.getRelationshipStatus()}">selected="selected"</c:if>></option>
                                    <option <c:if test="${user.getRelationshipStatus() == 'In a relationship'}">selected="selected"</c:if>>In a relationship</option>
                                    <option <c:if test="${user.getRelationshipStatus() == 'Single'}">selected="selected"</c:if>>Single</option>
                                    <option <c:if test="${user.getRelationshipStatus() == 'Married'}">selected="selected"</c:if>>Married</option>
                                </select>
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
                              <button type="button" class="btn btn-primary" onClick="location.href='/user/edit'">Discard</button>
                            </div>
                          </div>
                        </fieldset>
                      </form>
                    </div>
                    <%
                        ArrayList<SysAdminActiveRecord> admins = SysAdminActiveRecord.findAdminsByAdministratingUser(user.getUserID());
                        ArrayList<FanpageActiveRecord> pages = FanpageActiveRecord.findPagesByAdministratingUser(user.getUserID());
                        if(!pages.isEmpty() || !admins.isEmpty())
                        {%>
                            <div class="well bs-component">
                                <form class="form-horizontal" action="/page/connect" method="post">
                                    <fieldset>
                                      <legend>Connected profiles:</legend>
                                      <cst:ArrayListJSP itemList="<%=admins%>" itemName="nextItem">
                                        <% 
                                             SysAdminActiveRecord adminRec = (SysAdminActiveRecord)pageContext.getAttribute("nextItem");
                                        %>
                                        <div class="row text-left">
                                            <div class="col-lg-9">
                                                <%=adminRec.getAdminIDString() %> - <%=adminRec.getEmail() %>
                                            </div>
                                            <div class="col-lg-3">
                                                <button type="button" onClick="location.href='/user/connect/<%=adminRec.getAdminIDString()%>'" class="btn btn-sm btn-primary">
                                                    <span class="glyphicon glyphicon-remove"></span> Remove connection
                                                </button>
                                            </div>
                                        </div>
                                        <hr>
                                      </cst:ArrayListJSP>
                                      <cst:ArrayListJSP itemList="<%=pages%>" itemName="nextItem">
                                        <% 
                                             FanpageActiveRecord pagesRec = (FanpageActiveRecord)pageContext.getAttribute("nextItem");
                                        %>
                                        <div class="row text-left">
                                            <div class="col-lg-9">
                                                <%=pagesRec.getPageIDString()%> - <%=pagesRec.getDisplayName()%> 
                                            </div>
                                            <div class="col-lg-3">
                                                <button type="button" onClick="location.href='/user/connect/<%=pagesRec.getPageIDString()%>'" class="btn btn-sm btn-primary">
                                                    <span class="glyphicon glyphicon-remove"></span> Remove connection
                                                </button>
                                            </div>
                                        </div>
                                        <hr>
                                      </cst:ArrayListJSP>
                                    </fieldset>
                                </form>
                            </div>    
                        <%}
                    %>        
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
        <script>
            $(function(){ $('#DoB').datepicker();});
	</script>
    </body>
</html>
<%}
%>