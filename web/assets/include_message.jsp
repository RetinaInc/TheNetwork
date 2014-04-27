<%-- 
    Document   : include_message
    Description: This page is included at the top of several JSP to communicate with the user by providing a dismissable message box.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

        <%if(request.getAttribute("message") != null) 
        {%>
            <div class="alert alert-dismissable
            <%if(request.getAttribute("messageError") != null)
            {%>
                alert-danger
            <%}
            else if(request.getAttribute("messageSuccess") != null)
            {%>
                alert-success
            <%}
            else
            {%>
                alert-info
            <%}%>
            ">
                <button type="button" class="close" data-dismiss="alert">×</button>
                <%=(String)request.getAttribute("message")%>
            </div>
        <%}%>
    
        
