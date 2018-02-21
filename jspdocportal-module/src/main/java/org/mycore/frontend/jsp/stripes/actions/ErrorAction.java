package org.mycore.frontend.jsp.stripes.actions;

import org.mycore.frontend.jsp.stripes.error.MCRJSPErrorInfo;
import org.mycore.services.i18n.MCRTranslation;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/error.action")
public class ErrorAction extends MCRAbstractStripesAction implements ActionBean {
    MCRJSPErrorInfo errorInfo;
    
    public ErrorAction() {

    }

    @DefaultHandler
    public Resolution defaultRes() {
        errorInfo = new MCRJSPErrorInfo();
        String i18nKey = getContext().getRequest().getParameter("i18n");
        if(i18nKey!=null) {
            errorInfo.setHeadline(MCRTranslation.translate(i18nKey));
        }
        
        String msg = (String) getContext().getRequest().getAttribute("javax.servlet.error.message");
        if(msg!=null) {
            errorInfo.setMessage(msg);
        }
        
        //try to use default:    Throwable exception = (Throwable) req.getAttribute("javax.servlet.error.exception");
        Throwable thr = (Throwable) getContext().getRequest().getSession().getAttribute("mcr_exception");
        if (thr != null) {
            if(errorInfo.getMessage()==null) {
                errorInfo.setMessage("Exeption:");
            }
            errorInfo.setException(thr);
            getContext().getRequest().getSession().removeAttribute("mcr_exception");
        }
        int status = 500;
        if(getContext().getRequest().getParameter("status")!=null) {
            try {
                status = Integer.parseInt(getContext().getRequest().getParameter("status"));
                errorInfo.setStatus(status);
            }
            catch(NumberFormatException nfe) {
                // do nothing, use default
            }
        }
        if(status==404 || status == 500 || status == 410) {
            errorInfo.setHeadline(MCRTranslation.translate("Resolver.error.code."+status));
        }

        ForwardResolution fw = new ForwardResolution("/content/error.jsp");
        fw.setStatus(status);
        return fw;
    }
    
    public MCRJSPErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
