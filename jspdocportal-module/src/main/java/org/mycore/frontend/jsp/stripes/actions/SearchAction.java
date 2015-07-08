package org.mycore.frontend.jsp.stripes.actions;

import java.io.IOException;
import java.util.Enumeration;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.frontend.xeditor.MCREditorSession;
import org.mycore.frontend.xeditor.MCREditorSessionStore;
import org.mycore.frontend.xeditor.MCREditorSessionStoreFactory;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.solr.MCRSolrClientFactory;
import org.mycore.solr.search.MCRQLSearchUtils;

@UrlBinding("/search.action")
public class SearchAction extends MCRAbstractStripesAction implements ActionBean {
    private static Logger LOGGER = Logger.getLogger(SearchAction.class);

    public static int DEFAULT_ROWS = Integer.MAX_VALUE;

    ForwardResolution fwdResolutionForm = new ForwardResolution("/content/search/search.jsp");

    private String mask;
    
    private boolean hideMask;

    private String pageURL;

    private int start = 0;

    private int rows = DEFAULT_ROWS;

    //private MCRSearchResultDataBean result;
    private QueryResponse solrResponse;

    private SolrClient solrClient = MCRSolrClientFactory.getSolrClient();

    public SearchAction() {

    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void rehydrate() {
        super.rehydrate();
        if (getContext().getRequest().getParameter("mask") != null) {
            mask = getContext().getRequest().getParameter("mask");
        }

        if (getContext().getRequest().getParameter("rows") != null) {
            try {
                rows = Integer.valueOf(getContext().getRequest().getParameter("rows"));
            } catch (NumberFormatException nfe) {
                rows = DEFAULT_ROWS;
            }
        }
        if (getContext().getRequest().getParameter("start") != null) {
            try {
                start = Integer.valueOf(getContext().getRequest().getParameter("start"));
            } catch (NumberFormatException nfe) {
                start = 0;
            }
        }
    }

    @DefaultHandler
    public Resolution defaultRes() {
        getContext().getResponse().setCharacterEncoding("UTF-8");
        getContext().getResponse().setContentType("application/xhtml+xml; charset=UTF-8");
        setPageURL(getContext().getRequest().getRequestURI());

        Document queryDoc = (Document) getContext().getRequest().getAttribute("MCRXEditorSubmission");
        if (queryDoc == null) {
            String sessionID = getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM);
            if (sessionID != null) {
                if (sessionID.contains("-")) {
                    sessionID = sessionID.split("-")[0];
                }

                MCREditorSession session = MCREditorSessionStoreFactory.getSessionStore().getSession(sessionID);
                if (session != null) {
                    queryDoc = session.getXMLCleaner().clean(session.getEditedXML());
                }
            }
        }
        
        if (queryDoc != null) {
            XMLOutputter xml = new XMLOutputter(Format.getPrettyFormat());
            LOGGER.debug(xml.outputString(queryDoc));
            if (queryDoc.getRootElement().getChild("conditions").getChildren().size() > 0) {
                MCRQuery query = MCRQLSearchUtils.buildFormQuery(queryDoc.getRootElement());

                SolrQuery solrQuery = MCRQLSearchUtils.getSolrQuery(query, queryDoc, getContext().getRequest());

                solrQuery.setRows(rows);
                solrQuery.setStart(start);
                //solrQquery.setSort(SortClause.create(x[0],  x[1]));

                try {
                    solrResponse = solrClient.query(solrQuery);
                } catch (SolrServerException | IOException e) {
                    LOGGER.error(e);
                }
            }
        } else {
            queryDoc = new Document(new Element("query").setAttribute("mask", getMask()));
        }
        if (getMask() == null) {
            setMask(queryDoc.getRootElement().getAttributeValue("mask"));
        }
        
        hideMask = false;
        Enumeration<String> enumParamNames = getContext().getRequest().getParameterNames();
        while(enumParamNames.hasMoreElements()){
            if(enumParamNames.nextElement().startsWith("_xed_submit_")){
                hideMask = true;
                break;
            }
        }

        if (getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM) != null) {
            getContext().getRequest().getSession()
                .removeAttribute(MCREditorSessionStore.XEDITOR_SESSION_PARAM + "_" + getMask());
            getContext()
                .getRequest()
                .getSession()
                .setAttribute(MCREditorSessionStore.XEDITOR_SESSION_PARAM + "_" + getMask(),
                    getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM));
        }

        fwdResolutionForm.getParameters().remove(MCREditorSessionStore.XEDITOR_SESSION_PARAM);
        if (getContext().getRequest().getSession()
            .getAttribute(MCREditorSessionStore.XEDITOR_SESSION_PARAM + "_" + getMask()) != null) {

            fwdResolutionForm.addParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM, getContext().getRequest()
                .getSession().getAttribute(MCREditorSessionStore.XEDITOR_SESSION_PARAM + "_" + getMask()));
        }
        /*
        result = new MCRSearchResultDataBean();
        if (q.isEmpty()) {
        if(StringUtils.isNotEmpty(searchfieldName) && StringUtils.isNotEmpty(searchfieldValue)){
        	q = searchfieldName + ":" + searchfieldValue;
        }
        }
        if(q.length()==0){
        return fwdResolutionForm;
        }
        if (sort.isEmpty()) {
        if(StringUtils.isNotEmpty(sortfieldName) && StringUtils.isNotEmpty(sortfieldDirection)){
        	sort = sortfieldName + " " + sortfieldDirection;
        }
        }
        result.setQuery(q);
        result.setSort(sort);
        result.setStart(start);
        result.setRows(rows);
        MCRSearchResultDataBean.addSearchresultToSession(getContext().getRequest(), result);
        result.doSearch();
        return new ForwardResolution("/searchresult.action?_search="+result.getId());
        */
        fwdResolutionForm.addParameter("mask", getMask());

        return fwdResolutionForm;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getEditorPath() {
        if (getMask() != null) {
            return "editor/search/" + getMask();
        } else {
            return "editor/search/default.xed";
        }
    }

    public QueryResponse getSolrResponse() {
        return solrResponse;
    }

    public long getNumFound() {
        return solrResponse.getResults().getNumFound();
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public boolean isHideMask() {
        return hideMask;
    }
}
