<%-- 
    Document   : getPageListItem
    Description: This JSP is called to create and return a list of fanpages within a list.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.FanpageActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:choose>
        <c:when test="${!requestScope.listPageArray.isEmpty()}">
            <cst:ArrayListJSP itemList="<%=request.getAttribute("listPageArray")%>" itemName="nextPage">
                <% 
                     activeRecord.FanpageActiveRecord fanpage = (activeRecord.FanpageActiveRecord)pageContext.getAttribute("nextPage");
                     request.setAttribute("nextPage", fanpage);
                %>
                <div id="f<%=fanpage.getPageID()%>" class="row">
                     <div class="col-lg-2 text-center">
                         <a href= "/page/<%=fanpage.getPageID()%>">
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
                                         alt="Profile Picture of <%=fanpage.getDisplayName() %>."
                                         width="50" 
                                         height="50">
                            </span>
                         </a>
                     </div>  
                     <div class="col-lg-4">
                         <div class="text-left"><p>Display Name: <a href= "/page/<%=fanpage.getPageID()%>"><%=fanpage.getDisplayName() %></a></p></div>
                     </div>
                     <div class="col-lg-6">
                         <div class="text-right">
                            <jsp:include page="/ajax_view/getPageListButtons.jsp"></jsp:include>
                         </div>
                     </div>
                </div>
                <hr>
            </cst:ArrayListJSP>
            <div id="EOD" class="text-center"><p><a onclick="morePages(10)">Load more fanpages...</a></p></div>
        </c:when>                
        <c:otherwise>
            <div class="alert alert-warning">There are no (more) fanpages to show.</div>
        </c:otherwise>
    </c:choose>