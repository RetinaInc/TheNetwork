/* 
 * Copyright (C) 2014 Frank Steiler <frank.steiler@steilerdev.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This function retrieves an amount of new posts, depending on the oldest post currently loaded.
 * @param {int} amountPosts The amount of posts to load.
 * @returns {undefined}
 */
function moreOlderPosts(amountPosts)
{
    $.post("/ajax/getPosts",
    {
      path: window.location.pathname,
      amount: amountPosts,
      older: true
    },
    function(data,status){
        $('#EOD').replaceWith($(data).fadeIn("slow"));
    });
};

/**
 * This function checks if there are any new posts available.
 * @returns {undefined}
 */
function getNewPosts()
{
    $.post("/ajax/getPosts",
    {
      newer: true
    },
    function(data,status){
      $(data).fadeIn("slow").insertAfter($("#updateStatus"));
    });
};

/**
 * This function upvotes a specific post and reloads the updated karma counter.
 * @param {int} postid The ID of the post which needs to be upvoted.
 * @returns {undefined}
 */
function upvotePost(postid)
{
    var postDiv = $("#pv" + postid);
    $.post("/ajax/vote/up",
    {
        post: postid
    },
        replaceContent(postDiv)
    );
}

/**
 * This function downvotes a specific post and reloads the updated karma counter.
 * @param {int} postid The ID of the post which needs to be downvoted.
 * @returns {undefined}
 */
function downvotePost(postid)
{
    var postDiv = $("#pv" + postid);
    $.post("/ajax/vote/down",
    {
        post: postid
    },
        replaceContent(postDiv)
    );
}

/**
 * This function reverts a previous vote for a specific post and reloads the updated karma counter.
 * @param {int} postid The ID of the post which whos vote needs to be reverted.
 * @returns {undefined}
 */
function unvotePost(postid)
{
    var postDiv = $("#pv" + postid);
    $.post("/ajax/vote/remove",
    {
        post: postid
    },
        replaceContent(postDiv)
    );
}

/**
 * This function deletes a specific post.
 * @param {int} postid The post which needs to be deleted.
 * @returns {undefined}
 */
function removePost(postid)
{
    var postDiv = $("#p" + postid);
    $.post("/ajax/removePost",
    {
        post: postid
    },
        replaceContent(postDiv)
    );
}

/**
 * This function upvotes a specific comment and reloads the updated karma counter.
 * @param {int} commentid The ID of the comment which needs to be upvoted.
 * @returns {undefined}
 */
function upvoteComment(commentid)
{
    var commentDiv = $("#cv" + commentid);
    $.post("/ajax/vote/up",
    {
        comment: commentid
    },
        replaceContent(commentDiv)
    );
}

/**
 * This function downvotes a specific comment and reloads the updated karma counter.
 * @param {int} commentid The ID of the comment which needs to be downvoted.
 * @returns {undefined}
 */
function downvoteComment(commentid)
{
    var commentDiv = $("#cv" + commentid);
    $.post("/ajax/vote/down",
    {
        comment: commentid
    },
        replaceContent(commentDiv)
    );
}

/**
 * This function reverts a previous vote for a specific comment and reloads the updated karma counter.
 * @param {int} commentid The ID of the comment which whos vote needs to be reverted.
 * @returns {undefined}
 */
function unvoteComment(commentid)
{
    var commentDiv = $("#cv" + commentid);
    $.post("/ajax/vote/remove",
    {
        comment: commentid
    },
        replaceContent(commentDiv)
    );
}

/**
 * This function delets a specific comment.
 * @param {int} commentid The comment which needs to be deleted.
 * @returns {undefined}
 */
function removeComment(commentid)
{
    var commentDiv = $("#c" + commentid);
    $.post("/ajax/removeComment",
    {
        comment: commentid
    },
        replaceContent(commentDiv)
    );
}

/**
 * This function sends a friend request to a friend.
 * @param {type} friendid The id of the user who will receive the request.
 * @returns {undefined}
 */
function addFriend(friendid)
{
    var buttonDiv = $("#profileButtons");
    $.post("/ajax/user/add",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
            
    );
}

/**
 * This function removes a friend .
 * @param {type} friendid The id of the user who will be removed.
 * @returns {undefined}
 */
function removeFriend(friendid)
{
    var buttonDiv = $('#profileButtons');
    $.post("/ajax/user/remove",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function accepts a friend request.
 * @param {type} friendid The id of the user who requested the friendship.
 * @returns {undefined}
 */
function acceptFriend(friendid)
{
    var buttonDiv = $('#profileButtons');
    $.post("/ajax/user/accept",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function rejects a friend request.
 * @param {int} friendid The id of the user who requested the friendship.
 * @returns {undefined}
 */
function rejectFriend(friendid)
{
    var buttonDiv = $('#profileButtons');
    $.post("/ajax/user/reject",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function follows a page.
 * @param {int} pageid The id of the page which is followed.
 * @returns {undefined}
 */
function followPage(pageid)
{
    var buttonDiv = $('#pageButtons');
    $.post("/ajax/page/follow",
    {
        page: pageid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function unfollows a page.
 * @param {int} pageid The id of the page which is unfollowed.
 * @returns {undefined}
 */
function unfollowPage(pageid)
{
    var buttonDiv = $('#pageButtons');
    $.post("/ajax/page/unfollow",
    {
        page: pageid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function is called after a jquery post command. The function replaces the content of a div container with the response data from the post.
 * @param {type} content The jquery div object of the content which needs to be replaced.
 * @returns {unresolved}
 */
var replaceContent = function(content) {
    return function(data, textStatus) {
        $(content).replaceWith($(data).fadeIn("slow"));
    };
};

/**
 * This function removes a friend from the friend list.
 * @param {type} friendid The id of the user who will be removed.
 * @returns {undefined}
 */
function removeFriendList(friendid)
{
    var buttonDiv = $("#u" + friendid);
    $.post("/ajax/user/list/remove",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function accepts a friend request from the friend list.
 * @param {type} friendid The id of the user who requested the friendship.
 * @returns {undefined}
 */
function acceptFriendList(friendid)
{
    var buttonDiv = $("#u" + friendid);
    $.post("/ajax/user/list/accept",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function rejects a friend request from the friend list.
 * @param {int} friendid The id of the user who requested the friendship.
 * @returns {undefined}
 */
function rejectFriendList(friendid)
{
    var buttonDiv = $("#u" + friendid);
    $.post("/ajax/user/list/reject",
    {
        friend: friendid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function retrieves an amount of user, depending on last loaded user.
 * @param {int} amountOfUser The amount of user to load.
 * @returns {undefined}
 */
function moreUser(amountOfUser)
{
    $.post("/ajax/getFriends",
    {
      path: window.location.pathname,
      amount: amountOfUser,
    },
    function(data,status){
        $('#EOD').replaceWith($(data).fadeIn("slow"));
    });
};

/**
 * This function follows a page from the fanpage list.
 * @param {int} pageid The id of the page which is followed.
 * @returns {undefined}
 */
function followPageList(pageid)
{
    var buttonDiv = $("#f" + pageid);
    $.post("/ajax/page/list/follow",
    {
        page: pageid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function unfollows a page  from the fanpage list.
 * @param {int} pageid The id of the page which is unfollowed.
 * @returns {undefined}
 */
function unfollowPageList(pageid)
{
    var buttonDiv = $("#f" + pageid);
    $.post("/ajax/page/list/unfollow",
    {
        page: pageid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * This function retrieves an amount of pages, depending on last loaded page.
 * @param {int} amountOfPages The amount of pages to load.
 * @returns {undefined}
 */
function morePages(amountOfPages)
{
    $.post("/ajax/getPages",
    {
      path: window.location.pathname,
      amount: amountOfPages,
    },
    function(data,status){
        $('#EOD').replaceWith($(data).fadeIn("slow"));
    });
};

/**
 * This function sets the read flag on a specific post.
 * @param {int} postid The postID whos flag is going to be set.
 * @returns {undefined}
 */
function dismissNotification(postid)
{
    var buttonDiv = $("#" + postid);
    $.post("/notifications/dismiss",
    {
        dismissPost: postid
    },
        replaceContent(buttonDiv)
    );
}

/**
 * The following functions are not implemented yet, but shown in the view.
 */

function changeProfilePicture()
{
    alert("Changing the profile picture is not yet implemented!");
}

function changePagePicture()
{
    alert("Changing the fanpage picture is not yet implemented!");
}

function deleteProfilePicture()
{
    alert("Deleting the profile picture is not yet implemented!");
}

function deletePagePicture()
{
    alert("Deleting the fanpage picture is not yet implemented!");
}

function deleteFanpage()
{
    alert("Deleting the fanpage is not yet implemented!");
}

function deleteProfile()
{
    alert("Deleting the profile is not yet implemented!");
}

function resetPassword()
{
    alert("Resetting the password is not yet implemented!");
}