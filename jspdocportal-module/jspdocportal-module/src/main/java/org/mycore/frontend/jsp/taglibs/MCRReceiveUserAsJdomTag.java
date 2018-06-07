package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.mycore.common.MCRException;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;
import org.mycore.user2.utils.MCRUserTransformer;

public class MCRReceiveUserAsJdomTag extends SimpleTagSupport {
    private static Logger LOGGER = LogManager.getLogger(MCRReceiveUserAsJdomTag.class);

    private String userID;
    private String var;

    public void setVar(String var) {
        this.var = var;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void doTag() throws JspException, IOException {
        try {
            if (userID == null || userID.length() == 0) {
                userID = MCRUserManager.getCurrentUser().getUserID();
            }
            MCRUser u = new MCRUser(userID);
            String sNoUser = "User with ID " + userID + " dos'nt exit";
            try {
                u = MCRUserManager.getUser(userID);
            } catch (MCRException noUser) {
                LOGGER.warn(sNoUser);
                u.getAttributes().put("description", sNoUser);
            }

            Element eUser = MCRUserTransformer.buildExportableSafeXML(u).getRootElement();
            PageContext pageContext = (PageContext) getJspContext();
            //org.w3c.dom.Document domDoc = null;
            //domDoc =  new DOMOutputter().output( jUser);
            //pageContext.setAttribute(var , domDoc);
            pageContext.setAttribute(var, new Document(eUser));

        } catch (Exception e) {
            LOGGER.error("error in receiving user for jdom ", e);
        }
    }

}