package org.mycore.frontend.jsp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;

import org.mycore.common.MCRConstants;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUserInformation;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.stripes.actions.MCRLoginAction;

public class MCRSessionUserInjectionFilter implements Filter {
    private static final String SESSION_ATTR_LANG = "mcr.jspdocportal.language";
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        initUser(httpRequest);
        initLanguage(httpRequest);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private void initUser(HttpServletRequest httpRequest) {
        if(httpRequest.getSession().getAttribute(MCRLoginAction.SESSION_ATTR_MCR_USER)!=null) {
            MCRSessionMgr.getCurrentSession().setUserInformation((MCRUserInformation)httpRequest.getSession().getAttribute(MCRLoginAction.SESSION_ATTR_MCR_USER));
        }
    }
    
    private void initLanguage(HttpServletRequest httpRequest){
        MCRSession mcrSession = MCRSessionMgr.getCurrentSession();

        //MCR.JSPDocportal.Languages.Available=de,en
        //MCR.JSPDocportal.Languages.Default=de
        httpRequest.getSession().setAttribute(SESSION_ATTR_LANG,  MCRConfiguration.instance().getString("MCR.JSPDocportal.Languages.Default", MCRConstants.DEFAULT_LANG));
        String requestParamLang = httpRequest.getParameter("lang");
        if (requestParamLang != null) {
            List<String>allowedLanguages = MCRConfiguration.instance().getStrings("MCR.JSPDocportal.Languages.Available", Arrays.asList(new String[] {"de,en"}));
            if (allowedLanguages.contains(requestParamLang)) {
                httpRequest.getSession().setAttribute(SESSION_ATTR_LANG,  requestParamLang);
            }
        }
        String lang = (String) httpRequest.getSession().getAttribute(SESSION_ATTR_LANG);
        
        mcrSession.setCurrentLanguage(lang);
        Locale loc = new Locale(lang);
        Config.set(httpRequest.getSession(), Config.FMT_LOCALE, loc);
    }
}
