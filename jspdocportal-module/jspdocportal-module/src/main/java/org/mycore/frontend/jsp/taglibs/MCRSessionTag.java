package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;

public class MCRSessionTag extends SimpleTagSupport {
    private static Logger LOGGER = LogManager.getLogger(MCRSessionTag.class);

    private String method;
    private String var;
    private String type;
    private String key;

    public void setMethod(String inputMethod) {
        method = inputMethod;
        return;
    }

    public void setVar(String inputVar) {
        var = inputVar;
        return;
    }

    public void setType(String inputType) {
        type = inputType;
        return;
    }

    public void setKey(String inputKey) {
        key = inputKey;
        return;
    }

    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        //"HttpJspBase" is the name of the servlet that handles JSPs
        if (!method.equals("set") && !method.equals("get") && !method.equals("init")) {
            LOGGER.error("unknown method: " + method);
            return;
        }
        if (method.equals("init")) {
            return;
        }

        MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest) pageContext.getRequest());
        if (type != null && !type.equals("")) {
            if (type.equals("userID")) {
                if (method.equals("get"))
                    pageContext.setAttribute(var, mcrSession.getUserInformation().getUserID());

            } else if (type.equals("language")) {
                if (method.equals("get"))
                    pageContext.setAttribute(var, mcrSession.getCurrentLanguage());
                else
                    mcrSession.setCurrentLanguage((String) pageContext.getAttribute(var));
            } else if (type.equals("IP")) {
                if (method.equals("get"))
                    pageContext.setAttribute(var, mcrSession.getCurrentIP());
                else
                    mcrSession.setCurrentIP((String) pageContext.getAttribute(var));
            } else if (type.equals("ID")) {
                if (method.equals("get"))
                    pageContext.setAttribute(var, mcrSession.getID());
                else
                    LOGGER.error("set not possible for type ID!");
            } else {
                LOGGER.error("unknown type: " + type);
            }
            return;
        } else if (key != null && !key.equals("")) {
            if (method.equals("get"))
                pageContext.setAttribute(var, mcrSession.get(key));
            else
                mcrSession.put(key, pageContext.getAttribute(var));
            return;
        }
        return;
    }

}