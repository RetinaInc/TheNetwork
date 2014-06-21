<%-- 
    Document   : doVote
    Description: This JSP is returned after a comment or post was voted using an AJAX command.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>

<%@page import="activeRecord.CommentActiveRecord"%>
<%@page import="activeRecord.PostActiveRecord"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if(request.getAttribute("updatedPost") != null)
    {
        PostActiveRecord post = (PostActiveRecord)request.getAttribute("updatedPost");
        %>
            <p id="pv<%=post.getPostID()%>">
            <a href="/post/<%=post.getPostID()%>"><%=post.getCommentCount()%> Comment(s)</a>
            - Karma: <%=post.getKarma()%> 
            -
        <%
        if(post.isVoted())
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
    <%}
    else if(request.getAttribute("updatedComment") != null)
    {
        CommentActiveRecord comment = (CommentActiveRecord)request.getAttribute("updatedComment");
        %>
            <p id="cv<%=comment.getCommentID()%>">
            Karma: <%=comment.getKarma()%> 
            -
        <%
        if(comment.isVoted())
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
    <%}
    else
    {%>
        <p>Unable to perfom vote. Please refresh page and try again.</p>
    <%}
%>