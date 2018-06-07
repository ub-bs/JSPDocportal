package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.DOMOutputter;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.jsp.MCRHibernateTransactionWrapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport {
    private static Logger logger = LogManager.getLogger(MCRReceiveMcrObjAsJdomTag.class);

    private static DOMOutputter domOutputter = new DOMOutputter();

    private static LoadingCache<String, Document> mcrObjXMLCache = CacheBuilder.newBuilder().maximumSize(300)
            .expireAfterWrite(3, TimeUnit.MINUTES).expireAfterAccess(15, TimeUnit.SECONDS).build(new CacheLoader<String, Document>() {
                @Override
                public Document load(String mcrid) throws Exception {
                    try (MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()) {
                        MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));
                        return mcrObj.createXML();
                    }
                }
            });

    private String mcrid;
    private String var;
    private String varDom;
    private boolean fromWF = false;

    public void setMcrid(String mcrid) {
        this.mcrid = mcrid;
    }

    public void setVarDom(String varDom) {
        this.varDom = varDom;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setFromWF(boolean b) {
        fromWF = b;
    }

    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        try {
            Document docJdom = null;
            if (fromWF) {
                MCRObject mcrObj = MCRActivitiUtils.getWorkflowObject(MCRObjectID.getInstance(mcrid));
                docJdom = mcrObj.createXML();
            } else {
                if("clear".equals(pageContext.getRequest().getParameter("_cache"))){
                    mcrObjXMLCache.invalidate(mcrid);
                }
                
                docJdom = mcrObjXMLCache.get(mcrid);
            }
            if (var != null && !var.equals("")) {
                pageContext.setAttribute(var, docJdom);
            }
            if (varDom != null && !varDom.equals("")) {
                org.w3c.dom.Document domDoc = domOutputter.output(docJdom);
                pageContext.setAttribute(varDom, domDoc);
            }
        } catch (Exception e) {
            logger.error("error in receiving mcr_obj as jdom or dom for " + mcrid, e);
        }
    }
}