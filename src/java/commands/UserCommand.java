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
import activeRecord.SysAdminActiveRecord;
import activeRecord.SysAdminActiveRecordFactory;
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
 * This class processes the request to view a user's profile, list of all friends and edit your own profile. This class processes all "/user" requests.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UserCommand implements Command {

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
    public UserCommand(HttpServletRequest request, HttpServletResponse response)
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
        String viewPage;
        if(request.getSession().getAttribute("userID") != null)
        {
            String viewingUser = (String)request.getSession().getAttribute("userID");
            String uri = request.getRequestURI().substring(5);
            int viewingUserID = Integer.valueOf(viewingUser.substring(1));
            if(viewingUser.startsWith("u"))
            {                
                if(uri.isEmpty())
                {
                    //Show friends list (user accesses '/user/')
                    ArrayList<NormalUserActiveRecord> friendArray = NormalUserActiveRecordFactory.findAllFriends(viewingUserID, 10);
                    request.setAttribute("friendArray", friendArray);
                    if(!friendArray.isEmpty())
                    {
                        request.getSession().setAttribute("lastFriend", friendArray.get(friendArray.size()-1).getDisplayName());
                    }
                    else
                    {
                        request.getSession().removeAttribute("lastFriend");
                    }
                    ArrayList<NormalUserActiveRecord> requestingArray = NormalUserActiveRecordFactory.findAllRequestingFriends(viewingUserID);
                    request.setAttribute("requestingArray", requestingArray);
                    viewPage = "/listFriends.jsp";
                }
                else if(uri.startsWith("/edit"))
                {
                    //Showing user editing page (User accesses '/user/edit')
                    ArrayList<NormalUserActiveRecord> userArray = NormalUserActiveRecordFactory.findUserByID(viewingUserID);
                    if(userArray.size() == 1)
                    {
                        viewPage = "/userEditProfile.jsp";
                        ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostByUserIDAndAmount(viewingUserID, 10, viewingUser);
                        request.setAttribute("user", userArray.get(0));
                        request.setAttribute("postArray", postArray);
                        if(!postArray.isEmpty())
                        {
                            request.getSession().setAttribute("lastItemTimestamp", postArray.get(postArray.size()-1).getPostTimestamp());
                            request.getSession().setAttribute("firstItemTimestamp", postArray.get(0).getPostTimestamp());
                            request.setAttribute("older", true);
                        }
                        else
                        {
                            request.getSession().removeAttribute("lastItemTimestamp");
                        }
                    }
                    else
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "Problem loading your profile information.");
                    }
                }
                else if(uri.startsWith("/submit"))
                {
                    //User submits changes to his profile (User accesses '/user/submit')
                    if(validate(viewingUserID))
                    {
                        ArrayList<NormalUserActiveRecord> users = NormalUserActiveRecordFactory.findUserByID(viewingUserID);
                        if(users.size() == 1)
                        {
                            try 
                            {
                                NormalUserActiveRecord userRec = users.get(0);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                Date d = sdf.parse(request.getParameter("DoB"));
                                userRec.setDateOfBirth(new java.sql.Date(d.getTime()));
                                userRec.setDisplayName(request.getParameter("displayName"));
                                userRec.setFirstName(request.getParameter("firstName"));
                                userRec.setGender(null);
                                userRec.setHouseNr(Integer.valueOf(request.getParameter("streetNr")));
                                userRec.setLastName(request.getParameter("lastName"));
                                if(!request.getParameter("oldPassword").isEmpty())
                                {
                                    PasswordEncryptionService passwd = new PasswordEncryptionService();
                                    String encryptedPassword = passwd.getEncryptedPassword(request.getParameter("newPassword"), userRec.getSalt());
                                    if(encryptedPassword == null)
                                    {
                                        throw new IOException();
                                    }
                                    else
                                    {
                                        userRec.setPassword(encryptedPassword);
                                    }
                                }
                                userRec.setPremium(false);
                                userRec.setStreet(request.getParameter("street"));
                                userRec.setTown(request.getParameter("town"));
                                userRec.setZip(request.getParameter("zip"));
                                userRec.setEmail(request.getParameter("newEmail"));
                                userRec.setGender(request.getParameter("gender"));
                                userRec.setRelationshipStatus(request.getParameter("relationshipStatus"));
                                if(userRec.update())
                                {
                                    request.setAttribute("message", "<b>Your profile information have been updated successfully.</b>");
                                    request.setAttribute("messageSuccess", true);
                                }
                                else
                                {
                                    request.setAttribute("message", "<b>There has been an error saving your profile informaion.</b> Please try again.");
                                    request.setAttribute("messageError", true);
                                }
                                viewPage = "/user/edit";
                            } 
                            catch (IOException | NumberFormatException | ParseException e) 
                            {
                                viewPage = "/error.jsp";
                                request.setAttribute("errorCode", "Problem loading your profile information.");
                            }
                        }
                        else
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "Problem loading your profile information.");
                        }
                    }
                    else
                    {
                        request.setAttribute("message", "<b>Your input was invalid.</b> Please provide only correct input.");
                        request.setAttribute("messageError", true);
                        viewPage = "/user/edit";
                    }
                }
                else if(uri.startsWith("/connect") && uri.length() > 8)
                {
                    String user = uri.substring(9);
                    try
                    {
                        int userID = Integer.valueOf(user.substring(1));
                        if(user.startsWith("f"))
                        {
                            ArrayList<FanpageActiveRecord> page = FanpageActiveRecordFactory.findPageByID(userID);
                            if(page.size() == 1)
                            {
                                if(page.get(0).getAdministratingUser() == viewingUserID)
                                {
                                    page.get(0).setAdministratingUser(0);
                                    if(page.get(0).update())
                                    {
                                        request.setAttribute("message", "<b>The connection to the profile was successfully removed.</b>");
                                        request.setAttribute("messageSuccess", true);
                                    }
                                    else
                                    {
                                        request.setAttribute("message", "<b>There has been a problem removing the connection.</b> Please try again.");
                                        request.setAttribute("messageError", true);
                                    }
                                    viewPage = "/user/edit";
                                }
                                else
                                {
                                    viewPage = "/error.jsp";
                                    request.setAttribute("errorCode", "Insufficient rights to perfom this action.");
                                }
                            }
                            else
                            {
                                viewPage = "/error.jsp";
                                request.setAttribute("errorCode", "Problem loading the connection.");
                            }
                        }
                        else if(user.startsWith("a"))
                        {
                            ArrayList<SysAdminActiveRecord> admin = SysAdminActiveRecordFactory.findAdminByID(userID);
                            if(admin.size() == 1)
                            {
                                if(admin.get(0).getConnectedUser() == viewingUserID)
                                {
                                    admin.get(0).setConnectedUser(0);
                                    if(admin.get(0).update())
                                    {
                                        request.setAttribute("message", "<b>The connection to the profile was successfully removed.</b>");
                                        request.setAttribute("messageSuccess", true);
                                    }
                                    else
                                    {
                                        request.setAttribute("message", "<b>There has been a problem removing the connection.</b> Please try again.");
                                        request.setAttribute("messageError", true);
                                    }
                                    viewPage = "/user/edit";
                                }
                                else
                                {
                                    viewPage = "/error.jsp";
                                    request.setAttribute("errorCode", "Insufficient rights to perfom this action.");
                                }
                            }
                            else
                            {
                                viewPage = "/error.jsp";
                                request.setAttribute("errorCode", "Problem loading the connection.");
                            }
                        }
                        else
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "There has been a problem with your request.");
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "There has been a problem with your request.");
                    }
                }
                else 
                {
                    try
                    {
                        int userID = Integer.valueOf(uri.substring(1));
                        ArrayList<NormalUserActiveRecord> userArray = NormalUserActiveRecordFactory.findUserByID(userID);
                        if(userArray.size() != 1)
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "Problem loading the profile.");
                        }
                        else
                        {
                            viewPage = "/userProfile.jsp";
                            request.setAttribute("user", userArray.get(0));
                            ArrayList<PostActiveRecord> postArray = PostActiveRecordFactory.findAllPostByUserIDAndAmount(userID, 10, viewingUser);
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
            else if(viewingUser.startsWith("a"))
            {
                if(uri.startsWith("/connect"))
                {
                    String user = uri.substring(9);
                    int userID = Integer.valueOf(user.substring(1));
                    if(user.startsWith("u"))
                    {
                        ArrayList<SysAdminActiveRecord> admin = SysAdminActiveRecordFactory.findAdminByID(viewingUserID);
                        if(admin.size() == 1)
                        {
                            admin.get(0).setConnectedUser(userID);
                            if(admin.get(0).update())
                            {
                                request.setAttribute("message", "<b>The user was successfully connected to your profile.</b>");
                                request.setAttribute("messageSuccess", true);
                            }
                            else
                            {
                                request.setAttribute("message", "<b>There has been a problem saving the connection.</b> Please try again.");
                                request.setAttribute("messageError", true);
                            }
                            viewPage = "/user";
                        }
                        else
                        {
                            viewPage = "/error.jsp";
                            request.setAttribute("errorCode", "Problem loading administrator's profile.");
                        }
                    }
                    else
                    {
                        viewPage = "/error.jsp";
                        request.setAttribute("errorCode", "There has been a problem with your request.");
                    }   
                }
                else
                {
                    //Show user list
                    ArrayList<NormalUserActiveRecord> userArray = NormalUserActiveRecordFactory.findAllUser(10);
                    request.setAttribute("userArray", userArray);
                    if(!userArray.isEmpty())
                    {
                        request.getSession().setAttribute("lastUser", userArray.get(userArray.size()-1).getDisplayName());
                    }
                    else
                    {
                        request.getSession().removeAttribute("lastUser");
                    }
                    viewPage = "/listUser.jsp";
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
     * @param userID The ID of the user which is going to be updated.
     * @return True if the input is valid, false otherwise.
     */
    private boolean validate(int userID)
    {
        boolean valid = true;
        
        if(request.getParameter("newEmail") != null)
        {
            //Regular expression to check if the email address has the right format.
            String emailRegEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            if(!request.getParameter("newEmail").matches(emailRegEx))
            {
                request.setAttribute("emailError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("emailError", true);
            valid = false;
        }
        if(request.getParameter("firstName") != null)
        {
            if(request.getParameter("firstName").isEmpty())
            {
                request.setAttribute("firstNameError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("firstNameError", true);
            valid = false;
        }
        if(request.getParameter("lastName") != null)
        {
            if(request.getParameter("lastName").isEmpty())
            {
                request.setAttribute("lastNameError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("lastNameError", true);
            valid = false;
        }
        if(request.getParameter("displayName") != null)
        {
            if(request.getParameter("displayName").length() > 8 || request.getParameter("displayName").isEmpty())
            {
                request.setAttribute("displayNameError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("displayNameError", true);
            valid = false;
        }
        if(request.getParameter("street") != null)
        {
            if(request.getParameter("street").isEmpty())
            {
                request.setAttribute("streetError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("streetError", true);
            valid = false;
        }
        if(request.getParameter("streetNr") != null)
        {
            try
            {
                Integer.valueOf(request.getParameter("streetNr"));
            }
            catch (NumberFormatException e)
            {
                request.setAttribute("streetNrError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("streetError", true);
            valid = false;
        }
        if(request.getParameter("zip") != null)
        {
            if(request.getParameter("zip").isEmpty())
            {
                request.setAttribute("zipError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("zipError", true);
            valid = false;
        }
        if(request.getParameter("town") != null)
        {
            if(request.getParameter("town").isEmpty())
            {
                request.setAttribute("townError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("townError", true);
            valid = false;
        }
        if(request.getParameter("DoB") != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try
            {
                Date d = sdf.parse(request.getParameter("DoB"));
            }
            catch(ParseException e)
            {
                request.setAttribute("DoBError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("DoBError", true);
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
                    ArrayList<NormalUserActiveRecord> user = NormalUserActiveRecordFactory.findUserByID(userID);
                    if(user.size() == 1)
                    {
                        if(!passwd.authenticate(request.getParameter("oldPassword"), user.get(0).getPassword(), user.get(0).getSalt()))
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
        }
        return valid;
    }
}