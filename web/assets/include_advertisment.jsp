<%-- 
    Document   : include_advertisment
    Description: This file will hold the advertisment shown on the left and right of the page of a free user.
    Created on : 29.03.2014, 18:01:55
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%  if(request.getSession().getAttribute("userID") != null)
    {
        if( !((String)request.getSession().getAttribute("userID")).startsWith("a"))
        {%>
            <div class="affix text-center">Advertisment</div>
        <%}   
    }
    else
    {%>
            <div class="affix text-center"><p>Not logged in!</p></div>
    <%}%>