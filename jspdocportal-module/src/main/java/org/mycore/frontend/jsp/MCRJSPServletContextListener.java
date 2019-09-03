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

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRConstants;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.jsp.navigation.model.Navigations;
import org.mycore.services.i18n.MCRTranslation;

/**
 * This class implements a ServletContextListener. After the web app has
 * started, some basic initialisation will be done. - loading the navigation as
 * DOM tree into memory - load some constants into sessionContext /
 * applicationScope - create some permissions for admin interface, if they do
 * not exist
 * 
 * 
 * @author Robert Stephan
 * @version $Revision: 1.8 $ $Date: 2008/05/28 13:43:31 $
 * 
 */
public class MCRJSPServletContextListener implements ServletContextListener {
    private static Logger LOGGER = LogManager.getLogger(MCRJSPServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	MCRSessionMgr.unlock();
        
        LOGGER.debug("Application " + sce.getServletContext().getServletContextName() + " started");
        MCRNavigationUtil.loadNavigation(sce.getServletContext());
        Navigations.loadNavigation(sce.getServletContext());
        createNonExistingAdminPermissions();
        registerDefaultMessageBundle(sce.getServletContext());
        sce.getServletContext().setAttribute("WebApplicationBaseURL", MCRFrontendUtil.getBaseURL());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.debug("Application " + sce.getServletContext().getServletContextName() + " stopped");

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // Loop through all drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                // This driver was registered by the webapp's ClassLoader, so deregister it:
                try {
                    LOGGER.info("Deregistering JDBC driver: " + driver);
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    LOGGER.error("Error deregistering JDBC driver " + driver, ex);
                }
            } else {
                LOGGER.trace("Not deregistering JDBC driver " + driver
                        + " as it does not belong to this webapp's ClassLoader");
            }
        }
    }

    /**
     * sets default-rules for the use of the admin functions
     * 
     * @param objid
     * @param userid
     * @return boolean false if there was an Exception
     */

    private boolean createNonExistingAdminPermissions() {
        try {
            Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction();
            Collection<String> savedPermissions = MCRAccessManager.getAccessImpl().getPermissions();
            String permissions = MCRConfiguration.instance().getString("MCR.AccessAdminInterfacePermissions",
                    "admininterface-access,admininterface-user,admininterface-accessrules");
            for (Iterator<?> it = Arrays.asList(permissions.split(",")).iterator(); it.hasNext();) {
                String permission = ((String) it.next()).trim().toLowerCase(Locale.GERMAN);
                if (!permission.equals("") && !savedPermissions.contains(permission)) {
                    MCRAccessManager.getAccessImpl().addRule(permission, MCRAccessManager.getFalseRule(), "");
                }
            }
            tx.commit();
        } catch (MCRException e) {
            LOGGER.error("could not create admin interface permissions", e);
            return false;
        }
        return true;
    }

    private void registerDefaultMessageBundle(ServletContext sc) {
        Locale loc = new Locale(
                MCRConfiguration.instance().getString("MCR.Metadata.DefaultLang", MCRConstants.DEFAULT_LANG));
        Config.set(sc, Config.FMT_LOCALE, loc);
        LocalizationContext locCtxt = new LocalizationContext(MCRTranslation.getResourceBundle("messages", loc));
        Config.set(sc, Config.FMT_LOCALIZATION_CONTEXT, locCtxt);
    }
}