<%-- 
    Document   : include_message
    Created on : 24.03.2014, 12:46:35
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
    
        
