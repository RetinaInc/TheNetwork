<%-- 
    Document   : postPageEdit
    Description: This JSP provides the possibility to edit a specific post.
    Author     : Frank Steiler <frank@steiler.eu>
--%>

<%@page import="activeRecord.PostActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Edit Post</title>
    </head>
    <body>
        <div id="wrap">
            <jsp:include page="/assets/include_navbar.jsp"></jsp:include>
            <div class="container">
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
                <div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 center-block text-center">
                    <div class="row">
                        <jsp:include page="/assets/include_message.jsp"></jsp:include>
                    </div>

                    <div class="row">
                        <div class="well bs-component">
                            <form class="form-horizontal" action="/post/<%=((ArrayList<PostActiveRecord>)request.getAttribute("postArray")).get(0).getPostID()%>/edit" method="post">
                                <fieldset>
                                <legend>Update your post.</legend>
                                <div class="row center-block text-center">
                                <div class="col-lg-12">
                                    <textarea class="form-control" rows="3" id="textArea" name="updatedPost" placeholder="Updated Post"><%=((ArrayList<PostActiveRecord>)request.getAttribute("postArray")).get(0).getContent()%></textarea>
                                </div>
                                </div>
                                <p></p>
                                <div class="row center-block text-center">
                                <div class="col-lg-10"></div>
                                <div class="col-lg-2 text-center">
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">Submit</button>
                                    </div>
                                </div>
                                </div>
                               </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
        <script type="text/javascript">
            $(document).ready(function(){
                $(".dropdown-toggle").dropdown();
            });
        </script>
    </body>
</html>
