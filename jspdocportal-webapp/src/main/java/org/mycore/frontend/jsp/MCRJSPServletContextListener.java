/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
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
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 * 
 */
package org.mycore.frontend.jsp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

/**
 * This class implements a ServletContextListener.
 * After the web app has started, some basic initialisation will be done.
 * - loading the navigation as DOM tree into memory
 * - load some constants into sessionContext / applicationScope
 * - create some permissions for admin interface, if they do not exist
 * 
 * 
 * @author Robert Stephan
 * @version $Revision: 1.8 $ $Date: 2008/05/28 13:43:31 $
 *
 */
public class MCRJSPServletContextListener implements ServletContextListener
{
    private static Logger LOGGER = Logger.getLogger(MCRJSPServletContextListener.class);
   
    @Override
	public void contextInitialized(ServletContextEvent sce) {
    	LOGGER.debug("Application " + sce.getServletContext().getServletContextName()+" started");
		MCRNavigationUtil.loadNavigation(sce.getServletContext());
		loadConstants(sce.getServletContext());
		createNonExistingAdminPermissions();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.debug("Application " + sce.getServletContext().getServletContextName()+" stopped");
		
	}     
		
	 /**
     * sets application scope attributes
     * 	nav-attributes are set in the nav servlet
     * used in dissertationData
     * 
     * TODO REFACTORING
     */
    public final void loadConstants(ServletContext context){
    	context.setAttribute( "constants", new MCRWorkflowConstants() );
    }
	
	/**
	 * sets default-rules for the use of the admin functions
	 * 
	 * @param objid
	 * @param userid
	 * @return boolean  false if there was an Exception
	 */
    
	private boolean createNonExistingAdminPermissions() {
		try{
			Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction();
			MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
			Collection<String> savedPermissions = AI.getPermissions();
			String permissions = MCRConfiguration.instance().getString("MCR.AccessAdminInterfacePermissions","admininterface-access,admininterface-user,admininterface-accessrules");
			for (Iterator<?> it = Arrays.asList(permissions.split(",")).iterator(); it.hasNext();) {
				String permission = ((String) it.next()).trim().toLowerCase();
				if(!permission.equals("") && !savedPermissions.contains(permission)) {
					AI.addRule(permission, MCRAccessManager.getFalseRule(), "");
				}
			}
			tx.commit();
		}catch(MCRException e) {
			LOGGER.error("could not create admin interface permissions", e);
			return false;
		}
		return true;
	}
}