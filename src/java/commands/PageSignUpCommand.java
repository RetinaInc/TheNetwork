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

package commands;

import java.io.IOException;
import javax.servlet.ServletException;
import activeRecord.FanpageActiveRecord;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import assets.PasswordEncryptionService;
import java.security.NoSuchAlgorithmException;

/**
 * This class processes the request to create a new fanpage.
 * @author Frank Steiler <frank.steiler@steilerdev.de>
 */
public class PageSignUpCommand implements Command{

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
    public PageSignUpCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of creating a new fanpage. It validates the input and creates a new fanpage if the input is valid, otherwise it returns an error.
     * @return Returns the appropriate viewpage.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute() throws ServletException, IOException 
    {
        String viewpage;
        if(validateInput())
        {
            try
            {
                FanpageActiveRecord page = new FanpageActiveRecord();

                page.setDisplayName(request.getParameter("displayNamePage"));
                page.setPageName(request.getParameter("fanpageName"));
                
                PasswordEncryptionService passwd = new PasswordEncryptionService();
                String salt = passwd.generateSalt();
                if (salt == null)
                {
                    throw new ServletException();
                }
                else
                {
                    page.setSalt(salt);
                }
                String encryptedPassword = passwd.getEncryptedPassword(request.getParameter("inputPasswordPage"), page.getSalt());
                if(encryptedPassword == null)
                {
                    throw new ServletException();
                }
                else
                {
                    page.setPassword(encryptedPassword);
                }
                
                page.setPremium(false);
                page.setEmail(request.getParameter("inputEmailPage"));
                
                if(page.insert())
                {
                    request.getSession().setAttribute("userID", page.getPageIDString());
                    viewpage = "/index";
                }
                else
                {
                    request.setAttribute("errorCode", "There has been a problem while updating the database.");
                    viewpage = "/error.jsp";
                }
            }
            catch(NoSuchAlgorithmException | ServletException e)
            {
                request.setAttribute("errorCode", "There has been a problem with the creation of a new user.");
                viewpage = "/error.jsp";
            }
        }
        else
        {
            request.setAttribute("messageError", true);
            request.setAttribute("message", "<strong>The provided information have been invalid</strong>, please revise your input.");
            viewpage = "/login.jsp";
        }
        return viewpage;
    }
    
    /**
     * This function validates the user input and sets error flags, so it is possible to identify the problem easily.
     * @return True if input was valid, false if input was invalid.
     */
    private boolean validateInput()
    {
        boolean valid = true;
        
        if(request.getParameter("inputEmailPage") != null)
        {
            //Regular expression to check if the email address has the right format.
            String emailRegEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            if(!request.getParameter("inputEmailPage").matches(emailRegEx))
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
        if(request.getParameter("inputPasswordPage") != null || request.getParameter("inputPasswordRePage") != null)
        {
            if(request.getParameter("inputPasswordPage").isEmpty() || request.getParameter("inputPasswordRePage").isEmpty())
            {
                request.setAttribute("inputPasswordErrorPage", true);
                valid = false;
            }
            else if (!request.getParameter("inputPasswordPage").equals(request.getParameter("inputPasswordRePage")))
            {
                request.setAttribute("inputPasswordErrorPage", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("inputPasswordErrorPage", true);
            valid = false;
        }
        if(request.getParameter("gbtPage") != null)
        {
            if(request.getParameter("gbtPage").equals("false"))
            {
                request.setAttribute("gbtErrorPage", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("gbtErrorPage", true);
            valid = false;
        }
        return valid;
    }
}
