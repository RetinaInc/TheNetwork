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

package customTags;

import javax.servlet.jsp.tagext.*; 
import java.util.ArrayList;

/**
 * This class is a JSP custom tag handler iterating through an ArrayList. The item which can be used in the body is stored in the PageContext as attribute named according to the itemName variable.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class ArrayListJSP extends BodyTagSupport {
    
    private ArrayList itemList = null;
    private String itemName = "item";
    private int count = 0;
    
    /**
     * This function is executed at the beginning of the tag. 
     * @return If the body needs to be evaluated (if the ArrayList is not empty) or not.
     */
    @Override
    public int doStartTag() 
    { 
        count = 0;
        if(itemList != null)
        {
            if(!itemList.isEmpty())
            {
                pageContext.setAttribute(itemName, itemList.get(count));
                return EVAL_BODY_INCLUDE;
            }
        }
        return SKIP_BODY;
    } 
    
    /**
     * This function is executed after the body was evaluated. This function checks if every item of the list was accessed. Otherwise it will take the next item and restart the process.
     * @return If the body needs to be evaluated again.
     */
    @Override
    public int doAfterBody() 
    { 
        count++;
        if(itemList.size() > count)
        {
            pageContext.setAttribute(itemName, itemList.get(count));
            return EVAL_BODY_AGAIN;
        }
        return SKIP_BODY;
    } 
    
    /**
     * This function is executed when the end tag is reached. This function exits the tag and advises the jsp to evaluate the rest of the page.
     * @return If the rest of the page needs to be skipped or evaluated.
     */
    @Override
    public int doEndTag() 
    { 
        return EVAL_PAGE;
    } 
    
    /**
     * Releases all variables.
     */
    @Override
    public void release() 
    { 
        itemList = null; 
        itemName = null;  
        super.release(); 
    } 
    
    /**
     * Sets the itemList which needs to be used as iterator.
     * @param itemList The item list as object (direct input from request.getAttribute() possible.)
     */
    public void setItemList(java.lang.Object itemList) {
        this.itemList = (ArrayList)itemList;
    }

    /**
     * Sets the name of the item in the page context. 
     * @param itemName Name of the current item.
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
