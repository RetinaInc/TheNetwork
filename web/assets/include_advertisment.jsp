<%-- 
    Document   : include_advertisment
    Description: This file is included at the positions where an advertisment needs to be shown.
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
    <%}
%>