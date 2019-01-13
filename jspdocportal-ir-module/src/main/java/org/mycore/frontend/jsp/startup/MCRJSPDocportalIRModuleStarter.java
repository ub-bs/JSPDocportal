package org.mycore.frontend.jsp.startup;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.common.events.MCRStartupHandler;
import org.mycore.iview2.services.MCRImageTiler;


public class MCRJSPDocportalIRModuleStarter  implements MCRStartupHandler.AutoExecutable {
    private static Logger LOGGER = LogManager.getLogger(MCRJSPDocportalIRModuleStarter.class);

    @Override
    public String getName() {
        return "JSPDocportal IR Module Starter";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void startUp(ServletContext servletContext) {
        if (servletContext != null && MCRImageTiler.isRunning()) {
            LOGGER.info("JSPDocportal is going to shutdown Image Viewer Tiling Thread.");
            MCRImageTiler.getInstance().prepareClose();
            MCRImageTiler.getInstance().close();
        }
    }


}
