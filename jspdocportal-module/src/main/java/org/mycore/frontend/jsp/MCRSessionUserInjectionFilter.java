package org.mycore.frontend.jsp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUserInformation;
import org.mycore.frontend.jsp.stripes.actions.MCRLoginAction;

public class MCRSessionUserInjectionFilter implements Filter {
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(httpRequest.getSession().getAttribute(MCRLoginAction.SESSION_ATTR_MCR_USER_INFORMATION)!=null) {
            MCRSessionMgr.getCurrentSession().setUserInformation((MCRUserInformation)httpRequest.getSession().getAttribute(MCRLoginAction.SESSION_ATTR_MCR_USER_INFORMATION));
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}

}
