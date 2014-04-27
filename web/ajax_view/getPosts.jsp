<%-- 
    Document   : getPosts
    Description: This JSP is called to create and return a list of posts.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.PostActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:choose>
        <c:when test="${!requestScope.postArray.isEmpty()}">
            <cst:ArrayListJSP itemList="<%=request.getAttribute("postArray")%>" itemName="nextPost">
                <% activeRecord.PostActiveRecord post = (activeRecord.PostActiveRecord)pageContext.getAttribute("nextPost");%>
                <div id="p<%=post.getPostID()%>" class="row">
                    <div class="col-lg-2 text-center">
                        <a href=
                                <%if(post.getPublishingUser() != 0)
                                {%>
                                    "/user/<%=post.getPublishingUser()%>">
                                <%}
                                else if(post.getPublishingPage() != 0)
                                {%>
                                    "/page/<%=post.getPublishingPage()%>">
                                <%}%>

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
                                         alt="Profile/Page Picture of the publishing user."
                                         width="75" 
                                         height="75">
                            </span>
                            <div class="text-center"><%=post.getPublisherName()%></div>
                        </a>
                        <%
                            String tempCompare = " ";
                            if(post.getPublishingPage() != 0)
                            {
                                tempCompare = "f" + post.getPublishingPage();
                            }
                            else if (post.getPublishingUser() != 0)
                            {
                                tempCompare = "u" + post.getPublishingUser();
                            }
                            if(tempCompare.equals(request.getSession().getAttribute("userID")))
                            {
                                %>
                                <div class="text-center">
                                    <button type="button" onClick="location.href='/post/<%=post.getPostID()%>/edit'" class="btn btn-primary btn-xs">
                                            <span class="glyphicon glyphicon-edit"></span> Edit
                                    </button>
                                    <button type="button" onClick="removePost(<%=post.getPostID()%>)" class="btn btn-primary btn-xs">
                                            <span class="glyphicon glyphicon-remove"></span> Remove
                                    </button>
                                </div>
                            <%}
                        %>
                    </div>
                    <div class="col-lg-10">
                         <div class="clearfix text-justify">
                            <p> <%=post.getContent()%> </p>
                            <p id="pv<%=post.getPostID()%>">
                                <a href="/post/<%=post.getPostID()%>"><%=post.getCommentCount()%> Comment(s)</a>
                                - Karma: <%=post.getKarma()%> 
                                - <%if(post.isVoted())
                                    {
                                        if(post.isUpvote())
                                        {%>
                                            <a onclick="unvotePost('<%=post.getPostID()%>')">Undo Up-vote</a>
                                        <%}
                                        else
                                        {%>
                                            <a onclick="unvotePost('<%=post.getPostID()%>')">Undo Down-vote</a>
                                        <%}
                                    }
                                    else
                                    {%>
                                        <a onclick="upvotePost('<%=post.getPostID()%>')">Up-vote</a> - <a onclick="downvotePost('<%=post.getPostID()%>')">Down-vote</a>
                                    <%}%>
                                - Posted on <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="<%=post.getPostTimestamp()%>" />
                            </p>
                        </div>
                    </div>
                </div>
                <hr>        
            </cst:ArrayListJSP>
            <c:if test="${requestScope.older == true}"><div id="EOD" class="text-center"><p><a onclick="moreOlderPosts(15)">Load more posts...</a></p></div></c:if>
        </c:when>              
        <c:otherwise>
            <c:if test="${requestScope.older == true}"><div class="alert alert-warning">There are no (more) posts to show.</div></c:if>
        </c:otherwise>
    </c:choose>