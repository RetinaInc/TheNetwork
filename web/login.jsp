<%-- 
    Document   : login
    Description: This JSP is the welcome page for every user. Unregistered user can subscribe, existing user can log into their account.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <link rel="stylesheet" href="/css/datepicker.css" type="text/css"/>
        <script type="text/javascript" src="/javascript/bootstrap-datepicker.js"></script>
        <title>The Network - Login</title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <jsp:include page="/assets/include_message.jsp"></jsp:include>
                <div class="col-lg-6">
                    <div class="well bs-component">
                        <form class="form-horizontal" action="/signup/user" method="post">
                        <fieldset>
                          <legend>Sign up as normal user</legend>
                          <div class="form-group <c:if test="${requestScope.emailError == true}">has-error</c:if>">
                            <label for="inputEmail" class="col-lg-3 control-label">Email</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="inputEmail" id="inputEmail" placeholder="Email" <c:if test="${not empty param.inputEmail}">value="<%=request.getParameter("inputEmail")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.firstNameError == true}">has-error </c:if>">
                            <label for="firstName" class="col-lg-3 control-label">First Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="firstName" id="firstName" placeholder="John" <c:if test="${not empty param.firstName}">value="<%=request.getParameter("firstName")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.lastNameError == true}">has-error</c:if>">
                            <label for="lastName" class="col-lg-3 control-label">Last Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="lastName" id="lastName" placeholder="Doe" <c:if test="${not empty param.lastName}">value="<%=request.getParameter("lastName")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.displayNameError == true}">has-error</c:if>">
                            <label for="displayName" class="col-lg-3 control-label">Display Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="displayName" id="displayName" placeholder="John (Max. 8 character)" <c:if test="${not empty param.displayName}">value="<%=request.getParameter("displayName")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.streetError == true || requestScope.streetNrError == true}">has-error</c:if>">
                            <label for="street" class="col-lg-3 control-label">Street & Nr.</label>
                            <div class="col-lg-7">
                              <input type="text" class="form-control" name="street" id="street" placeholder="Jumpstreet" <c:if test="${not empty param.street}">value="<%=request.getParameter("street")%>"</c:if>>
                            </div>
                            <div class="col-lg-2">
                              <input type="text" class="form-control" name="streetNr" id="streetNr" placeholder="21" <c:if test="${not empty param.streetNr}">value="<%=request.getParameter("streetNr")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.zipError == true || requestScope.townError == true}">has-error</c:if>">
                            <label for="zip" class="col-lg-3 control-label">Zip-Code & Town</label>
                            <div class="col-lg-3">
                              <input type="text" class="form-control" name="zip" id="zip" placeholder="1234" <c:if test="${not empty param.zip}">value="<%=request.getParameter("zip")%>"</c:if>>
                            </div>
                            <div class="col-lg-6">
                              <input type="text" class="form-control" name="town" id="town" placeholder="Boston" <c:if test="${not empty param.town}">value="<%=request.getParameter("town")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.DoBError == true}">has-error</c:if>">
                              <label for="DoB" class="col-lg-3 control-label">Date of Birth</label>
                              <div class="input-append date" id="DoB" data-date="12-02-2012" data-date-format="dd-mm-yyyy">
                                  <div class="col-sm-7">
                                    <input class="form-control" name="DoB" type="text" placeholder="12-02-2012" <c:if test="${not empty param.DoB}">value="<%=request.getParameter("DoB")%>"</c:if>>
                                  </div>
                                  <div class="col-sm-2 text-center">
                                    <span class="add-on"><button type="button" class="btn btn-primary"><span class="glyphicon glyphicon-calendar"></span></button></span>
                                  </div>
                              </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.inputPasswordError == true}">has-error</c:if>">
                            <label for="inputPassword" class="col-lg-3 control-label">Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="inputPassword" id="inputPassword" placeholder="Password">
                            </div>
                            <label for="inputPasswordRe" class="col-lg-3 control-label">Retype Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="inputPasswordRe" id="inputPasswordRe" placeholder="Password">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.gbtError == true}">has-error</c:if>">
                            <label for="inputPassword" class="col-lg-3 control-label">  </label>
                            <div class="col-lg-9">
                                <div class="checkbox">
                                    <label>
                                      <input type="checkbox" name="gbt" id="Accept"> Accept General business terms
                                    </label>
                                </div>
                            </div>
                          </div>
                          <div class="form-group">
                            <div class="col-lg-12 text-center">
                              <button type="submit" class="btn btn-primary">Submit</button>
                            </div>
                          </div>
                        </fieldset>
                      </form>
                    </div>
                  </div>

                <div class="col-lg-6">
                    <div class="well bs-component">
                        <form class="form-horizontal" action="/signup/page" method="post">
                        <fieldset>
                          <legend>Sign up as fanpage admin</legend>
                          <div class="form-group <c:if test="${requestScope.emailErrorPage == true}">has-error</c:if>">
                            <label for="inputEmail" class="col-lg-3 control-label">Email</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="inputEmailPage" id="inputEmail" placeholder="Email" <c:if test="${not empty param.inputEmailPage}">value="<%=request.getParameter("inputEmailPage")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.fanpageNameError == true}">has-error</c:if>">
                            <label for="fanpageName" class="col-lg-3 control-label">Fanpage Name</label>
                            <div class="col-lg-9">
                                <input type="text" class="form-control" name="fanpageName" id="fanpageName" placeholder="Fanpage Name" <c:if test="${not empty param.fanpageName}">value="<%=request.getParameter("fanpageName")%>"</c:if>>
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.displayNameErrorPage == true}">has-error</c:if>">
                            <label for="displayNamePage" class="col-lg-3 control-label">Display Name</label>
                            <div class="col-lg-9">
                              <input type="text" class="form-control" name="displayNamePage" id="displayNamePage" placeholder="Page (Max. 8 character)" <c:if test="${not empty param.displayNamePage}">value="<%=request.getParameter("displayNamePage")%>"</c:if>>
                            </div>
                          </div> 
                          <div class="form-group <c:if test="${requestScope.inputPasswordErrorPage == true}">has-error</c:if>">
                            <label for="inputPasswordPage" class="col-lg-3 control-label">Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="inputPasswordPage" id="inputPassword" placeholder="Password">
                            </div>
                            <label for="inputPasswordRePage" class="col-lg-3 control-label">Retype Password</label>
                            <div class="col-lg-9">
                              <input type="password" class="form-control" name="inputPasswordRePage" id="inputPasswordRe" placeholder="Password">
                            </div>
                          </div>
                          <div class="form-group <c:if test="${requestScope.gbtErrorPage == true}">has-error</c:if>">
                            <label for="AcceptPage" class="col-lg-3 control-label">  </label>
                            <div class="col-lg-9">
                                <div class="checkbox">
                                    <label>
                                      <input type="checkbox" name="gbtPage" id="AcceptPage"> Accept General business terms
                                    </label>
                                </div>
                            </div>
                          </div>  
                          <div class="form-group">
                            <div class="col-lg-12 text-center">
                              <button type="submit" class="btn btn-primary">Submit</button>
                            </div>
                          </div>
                        </fieldset>
                      </form>
                    </div>
                  </div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
        <script>
            $(function(){ $('#DoB').datepicker();});
	</script>
    </body>
</html>
