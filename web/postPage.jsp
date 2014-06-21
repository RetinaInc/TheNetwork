<%-- 
    Document   : postPage
    Description: This JSP shows a single post with all his comments.
    Author     : Frank Steiler <frank.steiler@steilerdev.de>
--%>

<%@page import="activeRecord.PostActiveRecord"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/assets/include_headerfiles.jsp"></jsp:include>
        <title>The Network - Post</title>
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
                        <jsp:include page="/ajax_view/getPosts.jsp"></jsp:include>
                    </div>
                    <div class="row">
                        <div class="col-lg-1 hidden-xs ">  </div>
                        <div class="col-lg-11 ">
                            <jsp:include page="/ajax_view/getComment.jsp"></jsp:include>
                            <div class="row">
                                <form action="/postComment/<%=((ArrayList<PostActiveRecord>)request.getAttribute("postArray")).get(0).getPostID() %>" method="post">
                                    <div class="col-lg-10">
                                       <input type="text" class="form-control" name="newComment" placeholder="Your comment...">
                                    </div>
                                    <div class="col-lg-2">
                                       <button type="submit" class="btn btn-primary">Comment</button>
                                    </div>
                               </form>
                            </div>
                            <hr>
                        </div>
                    </div>
                </div>
                <div class="col-sm-2 col-md-2 col-lg-2 hidden-xs"><jsp:include page="/assets/include_advertisment.jsp"></jsp:include></div>
            </div>
        </div>
        <jsp:include page="/assets/include_footer.jsp"></jsp:include>
    </body>
</html>
