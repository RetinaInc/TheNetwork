<%-- 
    Document   : xmlView
    Created on : 08.04.2014, 13:28:57
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page contentType="text/xml" pageEncoding="UTF-8"%>
<%@taglib prefix="cst" uri="/WEB-INF/tlds/TheNetwork_tl.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    if(request.getAttribute("errorMessage") == null)
    {%>
        <!DOCTYPE messageResponse
        [
        <!ELEMENT messageResponse (post+)>
        <!ELEMENT post (postID, publisherName, content)>
        <!ELEMENT postID (#PCDATA)>
        <!ELEMENT publisherName (#PCDATA)>
        <!ELEMENT content (#PCDATA)>
        <!ELEMENT postTimeStamp (#PCDATA)>
        ]>
        <messageResponse>
            <cst:ArrayListJSP itemList="<%=request.getAttribute("postArray")%>" itemName="nextPost">
                <% activeRecord.PostActiveRecord post = (activeRecord.PostActiveRecord)pageContext.getAttribute("nextPost");%>
                    <post>
                        <postID><%=post.getPostID() %></postID>
                        <publisherName><%=post.getPublisherName() %></publisherName>
                        <content><%=post.getContent() %></content>
                        <postTimeStamp><%=post.getPostTimestamp().getTime() %></postTimeStamp>
                    </post>
            </cst:ArrayListJSP>
        </messageResponse>
    <%}
    else
    {%>
        <!DOCTYPE messageResponse
        [
        <!ELEMENT messageResponse (errorMessage)>
        <!ELEMENT errorMessage (#PCDATA)>
        ]>
        <messageResponse>
            <errorMessage><%=(String)request.getAttribute("errorMessage") %></errorMessage>
        </messageResponse>
    <%}
%>