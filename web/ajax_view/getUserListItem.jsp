<%-- 
    Document   : getUserListItem
    Created on : 29.03.2014, 20:41:26
    Description: This page is returned after an ajax command to provide users within a user list.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.NormalUserActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:choose>
        <c:when test="${!requestScope.listArray.isEmpty()}">
            <cst:ArrayListJSP itemList="<%=request.getAttribute("listArray")%>" itemName="nextUser">
                <% 
                     activeRecord.NormalUserActiveRecord user = (activeRecord.NormalUserActiveRecord)pageContext.getAttribute("nextUser");
                     request.setAttribute("nextUser", user);
                %>
                <div id="u<%=user.getUserID()%>" class="row">

                     <div class="col-lg-2 text-center">
                         <a href= "/user/<%=user.getUserID()%>">
                         <span><img  class="img-thumbnail" 
                                     src="
                                     <%
                                          if(pageContext.getServletContext().getResource("/pictures/" + user.getUserIDString()+ ".jpg") != null)
                                          {%>
                                              /pictures/<%=user.getUserIDString()%>.jpg

                                          <%}
                                          else
                                          {%>
                                              /pictures/default.jpg
                                          <%}
                                      %>" 
                                      alt="Profile Picture of <%=user.getDisplayName() %>."
                                      width="50" 
                                      height="50">
                         </span>
                         </a>

                     </div>  
                     <div class="col-lg-4">
                         <div class="text-left"><p>Display Name: <a href= "/user/<%=user.getUserID()%>"><%=user.getDisplayName() %></a></p><p>Real name: <a href= "/user/<%=user.getUserID()%>"><%=user.getFirstName() %> <%=user.getLastName() %></a></p></div>
                     </div>
                     <div class="col-lg-6">
                         <div class="text-right">
                            <jsp:include page="/ajax_view/getProfileListButtons.jsp"></jsp:include>
                         </div>
                     </div>
                 </div>
                 <hr>
            </cst:ArrayListJSP>
                <c:if test="${requestScope.requests != true}"><div id="EOD" class="text-center"><p><a onclick="moreUser(10)">Load more user...</a></p></div></c:if>
        </c:when>
                                
        <c:otherwise>
            <c:choose>
                <c:when test="${requestScope.requests == true}">
                    <div class="alert alert-warning">There are no (more) friend requests to show.</div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning">There are no (more) user to show.</div>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>