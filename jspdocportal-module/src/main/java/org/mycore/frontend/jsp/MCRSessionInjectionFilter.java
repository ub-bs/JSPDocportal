package org.mycore.frontend.jsp;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;

public class MCRSessionInjectionFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(MCRSessionInjectionFilter.class);
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        MCRSession session = MCRServlet.getSession((HttpServletRequest) request);
        MCRSessionMgr.setCurrentSession(session);
        LOGGER.info(MessageFormat.format("{0} ip={1} mcr={2} user={3}", httpRequest.getPathTranslated(),
            MCRFrontendUtil.getRemoteAddr(httpRequest), session.getID(), session.getUserInformation().getUserID()));
        MCRFrontendUtil.configureSession(session, httpRequest, httpResponse);
        
        chain.doFilter(request,  response);
            
        MCRSessionMgr.releaseCurrentSession();
    }

    @Override
    public void destroy() {

    }
}
