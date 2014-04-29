/*
 * Copyright (C) 2014 Frank Steiler <frank@steiler.eu>
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

package commands;

import activeRecord.FanpageActiveRecord;
import activeRecord.FanpageActiveRecordFactory;
import activeRecord.NormalUserActiveRecord;
import activeRecord.NormalUserActiveRecordFactory;
import activeRecord.PostActiveRecord;
import activeRecord.PostActiveRecordFactory;
import assets.PasswordEncryptionService;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class processes the request view a fanpage, edit your own fanpage and list all of a users followed fanpages. This class processes all "/page" requests.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class PageCommand implements Command{

    /**
     * The servlet request.
     */
    private HttpServletRequest request;
    /**
     * The servlet response.
     */
    private HttpServletResponse response;
    
    /**
     * Create a new command.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public PageCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of gathering the right data providing the appropriate viewpage.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
        String viewPage = "/error.jsp";
        if(request.getSession().getAttribute("userID") != null)
        {
            String viewingUser = (String)request.getSession().getAttribute("userID");
            String uri = request.getRequestURI().substring(5);
            if(viewingUser.startsWith("a") || viewingUser.startsWith("u") || viewingUser.equals("f" + uri.substring(1)) || viewingUser.startsWith("f") && (uri.startsWith("/edit") || uri.startsWith("/submit") || uri.startsWith("/connect")))
            {
                int viewingUserID = Integer.valueOf(viewingUser.substring(1));
                
                if(uri.isEmpty() && viewingUser.startsWith("u"))
                {
                    //Show fanpage list
                    ArrayList<FanpageActiveRecord> fanpage = FanpageActiveRecordFactory.findAllFollowingPages(viewingUserID, 10);
                    request.setAttribute("pageArray", fanpage);
                    if(!fanpage.isEmpty())
                    {
                        request.getSession().setAttribute("lastPage", fanpage.get(fanpage.size()-1).getDisplayName());
                    }
                    else
                    {
                        request.getSession().removeAttribute("lastPage");
                    }
                    viewPage = "/listPages.jsp";
                } 
                else if(uri.isEmpty() && viewingUser.startsWith("a"))
                {
                    //Show fanpage list
                    ArrayList<FanpageActiveRecord> fanpage = FanpageActiveRecordFactory.findAllPages(10);
                    request.setAttribute("pageArray", fanpage);
                    if(!fanpage.isEmpty())
                    {
                        request.getSession().setAttribute("lastPage", fanpage.get(fanpage.size()-1).getDisplayName());
                    }
                    else
                    {
                        request.getSession().removeAttribute("lastPage");
                    }
                    viewPage = "/listPages_admin.jsp";
                }
                else if(uri.startsWith("/edit") && viewingUser.startsWith("f"))
                {
                    ArrayList<FanpageActiveRecord> pageArray = FanpageActiveRecordFactory.findPageByID(viewingUserID);
                    if(pageArray.size() != 1)
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "Problem loading your fanpage information.");
                    }
                    else
                    {
                        viewPage = "/pageEditProfile.jsp";
                        request.setAttribute("page", pageArray.get(0));
                        ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostByPageIDAndAmount(viewingUserID, 10, viewingUser);
                        request.setAttribute("postArray", postArray);
                        if(!postArray.isEmpty())
                        {
                            request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
                            request.getSession().setAttribute("firstItemTimestamp", postArray.get(0).getPostTimestamp());
                        }
                        else
                        {
                            request.getSession().removeAttribute("lastItemTimestamp");
                        }
                        request.setAttribute("older", true);
                    }
                }
                //User submitted updated profile information
                else if(uri.startsWith("/submit") && viewingUser.startsWith("f"))
                {
                    if(validate(viewingUserID))
                    {
                        ArrayList<FanpageActiveRecord> pages = FanpageActiveRecordFactory.findPageByID(viewingUserID);
                        if(pages.size() == 1)
                        {
                            try 
                            {
                                FanpageActiveRecord pageRec = pages.get(0);

                                pageRec.setDisplayName(request.getParameter("displayNamePage"));
                                pageRec.setPageName(request.getParameter("fanpageName"));
                                if(request.getParameter("subjectPage").isEmpty())
                                {
                                    pageRec.setSubject(null);
                                }
                                else
                                {
                                    pageRec.setSubject(request.getParameter("subjectPage"));
                                }
                                pageRec.setEmail(request.getParameter("newEmailPage"));
                                if(!request.getParameter("oldPassword").isEmpty())
                                {
                                    PasswordEncryptionService passwd = new PasswordEncryptionService();
                                    String encryptedPassword = passwd.getEncryptedPassword(request.getParameter("newPassword"), pageRec.getSalt());
                                    if(encryptedPassword == null)
                                    {
                                        throw new IOException();
                                    }
                                    else
                                    {
                                        pageRec.setPassword(encryptedPassword);
                                    }
                                }
                                
                                if(pageRec.update())
                                {
                                    request.setAttribute("message", "<b>Your fanpage information have been updated successfully.</b>");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    request.setAttribute("message", "<b>There has been an error saving your fanpage information.</b> Please try again.");
                                    request.setAttribute("messageError", true);
                                }
                                viewPage = "/page/edit";
                            } 
                            catch (Exception e) 
                            {
                                viewPage = "/error.jsp";
                                request.setAttribute("errorCode", "Problem loading your fanpage information.");
                            }
                        }
                        else
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "Problem loading your fanpage information.");
                        }
                    }
                    else
                    {
                        request.setAttribute("message", "<b>Your input was invalid.</b> Please provide only correct input.");
                        request.setAttribute("messageError", true);
                        viewPage = "/page/edit";
                    }
                }
                else if(uri.startsWith("/connect") && viewingUser.startsWith("f"))
                {
                    if(request.getParameter("AdministratingUser") != null)
                    {
                        String administratingUser = request.getParameter("AdministratingUser");
                        if(!administratingUser.isEmpty())
                        {
                            try
                            {
                                int administratingUserID = Integer.valueOf(administratingUser.substring(1));
                                ArrayList<NormalUserActiveRecord> user = NormalUserActiveRecordFactory.findUserByID(administratingUserID);
                                if(user != null & user.size() == 1)
                                {
                                    ArrayList<FanpageActiveRecord> pages = FanpageActiveRecordFactory.findPageByID(viewingUserID);
                                    if(pages.size() == 1)
                                    {
                                        pages.get(0).setAdministratingUser(administratingUserID);
                                        if(pages.get(0).update())
                                        {
                                            request.setAttribute("message", "<b>The user was added successfully as administrator for this page.</b>");
                                            request.setAttribute("messageSuccess", true);
                                        }
                                        else
                                        {
                                            request.setAttribute("message", "<b>There has been an error saving your new fanpage administrator.</b> Please try again.");
                                            request.setAttribute("messageError", true);
                                        }
                                        viewPage = "/page/edit";
                                    }
                                    else
                                    {
                                        viewPage = "/error.jsp";
                                        request.setAttribute("errorCode", "Problem loading your fanpage information.");
                                    }
                                }
                                else
                                {
                                    viewPage = "/page/edit";
                                    request.setAttribute("message", "<b>Unable finding the user you want connect to.</b> Please try again.");
                                    request.setAttribute("ConnectionError", true);
                                    request.setAttribute("messageError", true);
                                }
                            }
                            catch(NumberFormatException e)
                            {
                                viewPage = "/page/edit";
                                request.setAttribute("message", "<b>The provided userID was wrong.</b> Please try again.");
                                request.setAttribute("ConnectionError", true);
                                request.setAttribute("messageError", true);
                            }
                        }
                        else
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "There is allready an user attached to that account.");
                        }
                    }
                    else
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "Problem receiving your provided information.");
                    }
                }
                else 
                {
                    try
                    {
                        int pageID = Integer.valueOf(uri.substring(1));
                        ArrayList<FanpageActiveRecord> pageArray = FanpageActiveRecordFactory.findPageByID(pageID);
                        if(pageArray.size() != 1)
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "Problem loading the profile.");
                        }
                        else
                        {
                            viewPage = "/pageProfile.jsp";
                            request.setAttribute("page", pageArray.get(0));
                            ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostByPageIDAndAmount(pageID, 10, viewingUser);
                            request.setAttribute("postArray", postArray);
                            if(!postArray.isEmpty())
                            {
                                request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
                                request.getSession().setAttribute("firstItemTimestamp", postArray.get(0).getPostTimestamp());
                            }
                            else
                            {
                                request.getSession().removeAttribute("lastItemTimestamp");
                            }
                            request.setAttribute("older", true);
                        }
                    }
                    catch(NumberFormatException e)
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "Problem loading the profile.");
                    }
                }
            }
            else
            {
                viewPage = "/error.jsp";
                request.setAttribute("errorCode", "You have insufficient rights to view this page.");
            }
        }
        else
        {
            viewPage = "/error.jsp";
            request.setAttribute("errorCode", "You have insufficient rights to view this page.");
        }
        return viewPage;
    }
    
    /**
     * This function checks if the input made by the user is valid.
     * @param pageID The ID of the page which is going to be updated.
     * @return True if the input is valid, false otherwise.
     */
    private boolean validate(int pageID)
    {
        boolean valid = true;
        
        if(request.getParameter("newEmailPage") != null)
        {
            //Regular expression to check if the email address has the right format.
            String emailRegEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            if(!request.getParameter("newEmailPage").matches(emailRegEx))
            {
                request.setAttribute("emailErrorPage", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("emailError", true);
            valid = false;
        }
        
        if(request.getParameter("fanpageName") != null)
        {
            if(request.getParameter("fanpageName").isEmpty())
            {
                request.setAttribute("fanpageNameError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("fanpageNameError", true);
            valid = false;
        }
        if(request.getParameter("displayNamePage") != null)
        {
            if(request.getParameter("displayNamePage").length() > 8 || request.getParameter("displayNamePage").isEmpty())
            {
                request.setAttribute("displayNameErrorPage", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("displayNameErrorPage", true);
            valid = false;
        }
        if(request.getParameter("subjectPage") != null)
        {
            if(request.getParameter("subjectPage").length() > 500)
            {
                request.setAttribute("subjectErrorPage", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("subjectErrorPage", true);
            valid = false;
        }
        if(request.getParameter("oldPassword") != null && request.getParameter("newPassword") != null && request.getParameter("newPasswordRe") != null)
        {
            if(!(request.getParameter("newPassword").isEmpty() && request.getParameter("newPasswordRe").isEmpty() && request.getParameter("oldPassword").isEmpty()))
            {
                PasswordEncryptionService passwd = new PasswordEncryptionService();

                if(request.getParameter("newPassword").isEmpty() || request.getParameter("newPasswordRe").isEmpty() || request.getParameter("oldPassword").isEmpty())
                {
                    request.setAttribute("inputPasswordError", true);
                    valid = false;
                }
                else if (!request.getParameter("newPassword").equals(request.getParameter("newPasswordRe")))
                {
                    request.setAttribute("inputPasswordError", true);
                    valid = false;
                }
                else
                {
                    ArrayList<FanpageActiveRecord> page = FanpageActiveRecordFactory.findPageByID(pageID);
                    if(page.size() == 1)
                    {  
                        if(!passwd.authenticate(request.getParameter("oldPassword"), page.get(0).getPassword(), page.get(0).getSalt()))
                        {
                            request.setAttribute("inputPasswordError", true);
                            valid = false;
                        }
                    }
                    else
                    {
                        request.setAttribute("inputPasswordError", true);
                        valid = false;
                    }
                }
            }
            
        }
        else
        {
            request.setAttribute("inputPasswordError", true);
            valid = false;
        }
        return valid;
    }
}