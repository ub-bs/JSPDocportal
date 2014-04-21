/*
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 */
package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * This class looks into the properties for a link that belongs to a given groupID
 * If such a link exists the link will be generated 
 *  otherwise only the group description will be displayed. 
 *
 * @author Robert Stephan
 */
public class MCRLogin_StartLinkTag extends SimpleTagSupport
{
	private String g_id;
	private String g_description;	
		
	public void setGroup_id(String g_id) {
		this.g_id = g_id;		
	}
	public void setGroup_description(String descr) {
		this.g_description = descr;
	}	
	
	public void doTag() throws JspException, IOException {		
		String link = MCRConfiguration.instance().getString("MCR.Application.Login.StartLink."+g_id, "").trim();
		JspWriter out = getJspContext().getOut();
		if(!link.equals("")){
			out.write("<a href=\""+MCRServlet.getBaseURL()+link+"\">"+g_description+" ("+g_id+") </a>");
		}
		else{
			out.write(g_description+" ("+g_id+")");
		}
	}
}