<%-- 
    Document   : getNotificationItem
    Description: This JSP is called to create and return a list of notifications.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>

<%@page import="activeRecord.FanpageActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:choose>
        <c:when test="${!requestScope.notificationArray.isEmpty()}">
            <cst:ArrayListJSP itemList="<%=request.getAttribute("notificationArray")%>" itemName="nextPost">
                <% 
                     activeRecord.PostActiveRecord post = (activeRecord.PostActiveRecord)pageContext.getAttribute("nextPost");
                %>
                <div id="<%= post.getPostID()%>" class="row">
                     <div class="col-lg-2 text-center">
                            <a href=
                                    <%
                                        if(post.getPublishingUser() != 0)
                                        {%>
                                            "/user/<%=post.getPublishingUser()%>">
                                        <%}
                                        else if(post.getPublishingPage() != 0)
                                        {%>
                                            "/page/<%=post.getPublishingPage()%>">
                                        <%}
                                    %>
                                    <span><img  class="img-thumbnail" 
                                                src="
                                                <%
                                                     if(pageContext.getServletContext().getResource("/pictures/u" + post.getPublishingUser() + ".jpg") != null)
                                                     {%>
                                                         /pictures/u<%=post.getPublishingUser()%>.jpg

                                                     <%}
                                                     else if(pageContext.getServletContext().getResource("/pictures/f" + post.getPublishingPage()+ ".jpg") != null)
                                                     {%>
                                                         /pictures/f<%=post.getPublishingPage()%>.jpg

                                                     <%}
                                                     else
                                                     {%>
                                                         /pictures/default.jpg
                                                     <%}
                                                 %>" 
                                                 alt="Picture of the publishing user."
                                                 width="75" 
                                                 height="75">
                                    </span>
                            </a>
                        </div>
                     <div class="col-lg-10">
                         <button type="button" class="close" onClick="dismissNotification(<%=post.getPostID()%>)">×</button>
                         <div class="text-left">
                             <p>New activity on a <a href="/post/<%=post.getPostID() %>"> post</a> by <a href="<%=post.getPublishingUser() != 0 ? "/user/" + post.getPublishingUser() : "/page/" + post.getPublishingPage()%>"><%=post.getPublisherName() %></a></p>
                             <p>Post Content: <em><%=post.getContent().length() > 50 ? post.getContent().substring(0, 50) + " [...]" : post.getContent() %></em></p>
                             <p><a href="/post/<%=post.getPostID()%>"><%=post.getCommentCount()%> Comment(s)</a> - Posted on <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="<%=post.getPostTimestamp()%>" /></p>
                         </div>
                     </div>
                 </div>
                <hr>
            </cst:ArrayListJSP>
        </c:when>      
        <c:otherwise>
            <div class="alert alert-warning">There are no new activities on any post you follow.</div>
            <hr>
        </c:otherwise>
    </c:choose>