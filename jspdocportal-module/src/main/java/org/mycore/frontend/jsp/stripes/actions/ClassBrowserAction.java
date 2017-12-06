package org.mycore.frontend.jsp.stripes.actions;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.config.MCRConfigurationException;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/classbrowser/{modus}")
public class ClassBrowserAction extends MCRAbstractStripesAction implements ActionBean {

    private String modus = "";

    public ClassBrowserAction() {

    }

    @DefaultHandler
    public Resolution defaultRes() {
        MCRConfiguration config = MCRConfiguration.instance();
        try {
            config.getString("MCR.ClassBrowser." + modus + ".Classification");
        } catch (MCRConfigurationException e) {
            return new RedirectResolution("/");
        }
        return new ForwardResolution("/content/classbrowser.jsp");
    }

    public String getModus() {
        return modus;
    }

    public void setModus(String modus) {
        this.modus = modus;
    }
}
