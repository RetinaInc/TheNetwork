<%-- 
    Document   : getComment
    Description: This JSP is called to create and return a list of comments.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cst:ArrayListJSP itemList="<%=request.getAttribute("commentArray")%>" itemName="nextComment">
    <% activeRecord.CommentActiveRecord comment = (activeRecord.CommentActiveRecord)pageContext.getAttribute("nextComment");%>

    <div id="c<%=comment.getCommentID()%>" class="row">
        <div class="col-xs-2">
            <a href=
                    <%if(comment.getPublishingUser() != 0)
                    {%>
                        "/user/<%=comment.getPublishingUser()%>">
                    <%}
                    else if(comment.getPublishingPage() != 0)
                    {%>
                        "/fanpage/<%=comment.getPublishingPage()%>">
                    <%}%>
                    <span><img  class="img-thumbnail" 
                                src="
                                <%
                                     if(pageContext.getServletContext().getResource("/pictures/u" + comment.getPublishingUser() + ".jpg") != null)
                                     {%>
                                         /pictures/u<%=comment.getPublishingUser()%>.jpg

                                     <%}
                                     else if(pageContext.getServletContext().getResource("/pictures/f" + comment.getPublishingPage()+ ".jpg") != null)
                                     {%>
                                         /pictures/f<%=comment.getPublishingPage()%>.jpg

                                     <%}
                                     else
                                     {%>
                                         /pictures/default.jpg
                                     <%}
                                 %>" 
                                 alt="Picture of the publishing user."
                                 width="50" 
                                 height="50">
                    </span>
                    <div class="text-center"><%=comment.getPublisherName()%></div>
            </a>
        </div>
        <div class="col-xs-10">
             <div class="clearfix text-justify">
                 <%
                    String tempCompare = " ";
                    if(comment.getPublishingPage() != 0)
                    {
                        tempCompare = "f" + comment.getPublishingPage();
                    }
                    else if (comment.getPublishingUser() != 0)
                    {
                        tempCompare = "u" + comment.getPublishingUser();
                    }
                    if(tempCompare.equals(request.getSession().getAttribute("userID")))
                    {%>
                        <button type="button" class="close" onClick="removeComment(<%=comment.getCommentID()%>)">×</button>
                    <%}
                %>
                <p><%=comment.getContent()%></p>
                <p id="cv<%=comment.getCommentID()%>">              
                    Karma: <%=comment.getKarma()%> 
                    - <%if(comment.isVoted())
                        {
                            if(comment.isUpvote())
                            {%>
                                <a onclick="unvoteComment('<%=comment.getCommentID()%>')">Undo Up-vote</a>
                            <%}
                            else
                            {%>
                                <a onclick="unvoteComment('<%=comment.getCommentID()%>')">Undo Down-vote</a>
                            <%}
                        }
                        else
                        {%>
                            <a onclick="upvoteComment('<%=comment.getCommentID()%>')">Up-vote</a> - <a onclick="downvoteComment('<%=comment.getCommentID()%>')">Down-vote</a>
                        <%}%>
                    - Posted on <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="<%=comment.getCommentTimestamp()%>" />
                 </p>
            </div>
        </div>
    </div>
    <hr>
</cst:ArrayListJSP>
                            
                            