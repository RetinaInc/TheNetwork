<%-- 
    Document   : error
    Created on : 26.03.2014, 00:01:14
    Description: This JSP is called if an AJAX command produces an error.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<% if(request.getAttribute("errorCode") != null)
{%>
    <p><%=(String)request.getAttribute("errorCode")%></p>
<%}
else if(request.getAttribute("empty") != null)
{%>
    
<%}
else
{%>
    <p>This error happened unexpect.</p>
<%}%>
