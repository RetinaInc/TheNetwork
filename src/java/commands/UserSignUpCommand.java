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
import activeRecord.NormalUserActiveRecord;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import assets.PasswordEncryptionService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class processes the request to create a new user.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class UserSignUpCommand implements Command{

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
    public UserSignUpCommand(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }
    
    /**
     * This function executes the process of creating a new user. It validates the input and creates a new user if the input is valid.
     * @return The appropriate viewpage.
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
                NormalUserActiveRecord user = new NormalUserActiveRecord();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date d = sdf.parse(request.getParameter("DoB"));
                user.setDateOfBirth(new java.sql.Date(d.getTime()));
                user.setDisplayName(request.getParameter("displayName"));
                user.setFirstName(request.getParameter("firstName"));
                user.setGender(null);
                user.setHouseNr(Integer.valueOf(request.getParameter("streetNr")));
                user.setLastName(request.getParameter("lastName"));
                
                PasswordEncryptionService passwd = new PasswordEncryptionService();
                String salt = passwd.generateSalt();
                if (salt == null)
                {
                    throw new IOException();
                }
                else
                {
                    user.setSalt(salt);
                }
                String encryptedPassword = passwd.getEncryptedPassword(request.getParameter("inputPassword"), user.getSalt());
                if(encryptedPassword == null)
                {
                    throw new IOException();
                }
                else
                {
                    user.setPassword(encryptedPassword);
                }
                user.setPremium(false);
                user.setRelationshipStatus(null);
                user.setStreet(request.getParameter("street"));
                user.setTown(request.getParameter("town"));
                user.setZip(request.getParameter("zip"));
                user.setEmail(request.getParameter("inputEmail"));
                
                if(user.insert())
                {
                    request.getSession().setAttribute("userID", user.getUserIDString());
                    viewpage = "/index";
                }
                else
                {
                    request.setAttribute("errorCode", "There has been a problem while updating the database.");
                    viewpage = "/error.jsp";
                }
            }
            catch(Exception e)
            {
                request.setAttribute("errorCode", "There has been a problem with the creation of a new user.");
                viewpage = "/error.jsp";
            }
        }
        else
        {
            request.setAttribute("messageError", true);
            request.setAttribute("message", "<strong>The provided information has been wrong</strong>, please revise your input.");
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
        
        if(request.getParameter("inputEmail") != null)
        {
            //Regular expression to check if the email address has the right format.
            String emailRegEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            if(!request.getParameter("inputEmail").matches(emailRegEx))
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
        if(request.getParameter("inputPassword") != null || request.getParameter("inputPasswordRe") != null)
        {
            if(request.getParameter("inputPassword").isEmpty() || request.getParameter("inputPasswordRe").isEmpty())
            {
                request.setAttribute("inputPasswordError", true);
                valid = false;
            }
            else if (!request.getParameter("inputPassword").equals(request.getParameter("inputPasswordRe")))
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
        if(request.getParameter("gbt") != null)
        {
            if(request.getParameter("gbt").equals("false"))
            {
                request.setAttribute("gbtError", true);
                valid = false;
            }
        }
        else
        {
            request.setAttribute("gbtError", true);
            valid = false;
        }
        
        return valid;
    }
    
}
