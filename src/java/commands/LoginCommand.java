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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import assets.PasswordEncryptionService;
import activeRecord.NormalUserActiveRecord;
import activeRecord.FanpageActiveRecord;
import activeRecord.SysAdminActiveRecord;
import java.util.ArrayList;

/**
 * This class processes the request to log the user into his account.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class LoginCommand implements Command{
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    /**
     * Create a new command.
     * @param request The servlet request.
     * @param response The servlet response.
     */
    public LoginCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    /**
     * This function executes the process of logging a user into the system.
     * @return The appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException {
        
        String user;
        if(request.getAttribute("userLogin") != null)
        {
            user = (String)request.getAttribute("userLogin");
        }
        else if (request.getParameter("userLogin") != null)
        {
            user = request.getParameter("userLogin");
        } else
        {
            throw new ServletException();
        }
        String attemptedPassword = request.getParameter("passwordLogin");
        String viewPage = "/error.jsp";
        PasswordEncryptionService passwordService = new PasswordEncryptionService();
        if(!user.isEmpty())
        {
            boolean check = true;
            int userID = 0;
            try 
            {
                userID = Integer.valueOf(user.substring(1));
            } 
            catch (NumberFormatException e) 
            {
                check = false;
            }

            if(check && ((user.startsWith("u") || user.startsWith("f") || user.startsWith("a"))))
            {
                //User provided an ID as identifier, that means only one account is possible.
                if(user.startsWith("u"))
                {
                    ArrayList<NormalUserActiveRecord> normalUser = NormalUserActiveRecord.findUserByID(userID);
                    if(normalUser.size() != 1)
                    {
                        throw new ServletException();
                    }
                    if(passwordService.authenticate(attemptedPassword, normalUser.get(0).getPassword(), normalUser.get(0).getSalt()))
                    {
                        request.getSession().setAttribute("userID", "u" + normalUser.get(0).getUserID());
                        viewPage = "/index";
                    }
                    else
                    {
                        request.setAttribute("failure", true);
                        request.setAttribute("user", normalUser);
                        viewPage = "/loginSecondStep.jsp";
                    }
                }
                else if(user.startsWith("f"))
                {
                    ArrayList<FanpageActiveRecord> fanpage = FanpageActiveRecord.findPageByID(userID);
                    if(fanpage.size() != 1)
                    {
                        throw new ServletException();
                    }
                    if(passwordService.authenticate(attemptedPassword, fanpage.get(0).getPassword(), fanpage.get(0).getSalt()))
                    {
                        request.getSession().setAttribute("userID", "f" + fanpage.get(0).getPageID());
                        viewPage = "/index";
                    }
                    else
                    {
                        request.setAttribute("failure", true);
                        request.setAttribute("page", fanpage);
                        viewPage = "/loginSecondStep.jsp";
                    }
                }
                else if(user.startsWith("a"))
                {
                    ArrayList<SysAdminActiveRecord> admin = SysAdminActiveRecord.findAdminByID(userID);
                    if(admin.size() > 1)
                    {
                        throw new ServletException();
                    }
                    if(passwordService.authenticate(attemptedPassword, admin.get(0).getPassword(), admin.get(0).getSalt()))
                    {
                        request.getSession().setAttribute("userID", "a" + admin.get(0).getAdminID());
                        viewPage = "/index";
                    }
                    else
                    {
                        request.setAttribute("failure", true);
                        request.setAttribute("admin", admin);
                        viewPage = "/loginSecondStep.jsp";
                    }
                }
            }
            else
            {
                //User provided an eMail as identifier, that means nore than one account is possible.
                ArrayList<NormalUserActiveRecord> normalUser = NormalUserActiveRecord.findUserByEmail(user);
                ArrayList<SysAdminActiveRecord> sysAdmin = SysAdminActiveRecord.findAdminByEmail(user);
                ArrayList<FanpageActiveRecord> page = FanpageActiveRecord.findPageByEmail(user);
                //Checks if the email is used multiple times
                //Checks if there is only 1 array containing data (a XOR b XOR c) or any of the containing more than one entry.
                if ((((!normalUser.isEmpty() ^ !sysAdmin.isEmpty() ^ !page.isEmpty())) && !(!normalUser.isEmpty() && !page.isEmpty() && !sysAdmin.isEmpty())) && normalUser.size() <= 1 && sysAdmin.size() <= 1 && page.size() <= 1)
                {
                    if(!normalUser.isEmpty())
                    {
                        request.setAttribute("userLogin", "u" + normalUser.get(0).getUserID()); 
                    } 
                    else if (!sysAdmin.isEmpty())
                    {
                        request.setAttribute("userLogin", "a" + sysAdmin.get(0).getAdminID());
                    }
                    else if (!page.isEmpty())
                    {
                        request.setAttribute("userLogin", "f" + page.get(0).getPageID());
                    }
                    viewPage = "/login";
                }
                else
                {
                    //Email is used multiple times or not at all. 
                    if(normalUser.isEmpty() && sysAdmin.isEmpty() && page.isEmpty())
                    {
                        request.setAttribute("failure", false);
                        viewPage = "/loginSecondStep.jsp";
                    }
                    else
                    {
                        //Redirect to select appropriate account.
                        request.setAttribute("admin", sysAdmin);
                        request.setAttribute("page", page);
                        request.setAttribute("user", normalUser);
                        viewPage = "/loginSecondStep.jsp";
                    }
                }
            }
        }
        else
        {
            request.setAttribute("failure", false);
            viewPage = "/loginSecondStep.jsp";
        }
        return viewPage;
    }
    
}
