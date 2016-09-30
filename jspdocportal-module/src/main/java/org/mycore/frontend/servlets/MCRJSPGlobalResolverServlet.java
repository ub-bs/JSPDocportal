/*
 * $RCSfile$
 * $Revision: 19974 $ $Date: 2011-02-20 12:23:20 +0100 (So, 20 Feb 2011) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

package org.mycore.frontend.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.solr.MCRSolrClientFactory;

/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Robert Stephan
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPGlobalResolverServlet extends MCRJSPIDResolverServlet {

    private static final long serialVersionUID = 1L;

    private static Logger LOGGER = Logger.getLogger(MCRJSPGlobalResolverServlet.class);

    /** static compiled transformer stylesheets */
    private static Hashtable<String, javax.xml.transform.Transformer> translist = new Hashtable<String, javax.xml.transform.Transformer>();

    /**
     * The initalization of the servlet.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
    }

    /**
     * The method replace the default form MCRSearchServlet and redirect the
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String uri = request.getRequestURI();
        String path[] = uri.substring(uri.indexOf("/resolve/") + 9).split("/", -1);
        if (path.length < 2) {
            getServletContext()
                .getRequestDispatcher("/nav?path=~mycore-error&messageKey=Resolver.error.unknownUrlSchema")
                .forward(request, response);
            return;
        }
        String key = path[0];
        String value = path[1];

        String mcrID = null;
        if ("id".equals(key)) {
            mcrID = recalculateMCRObjectID(value);
        } else {
            try {
                value = URLDecoder.decode(URLDecoder.decode(value, "UTF-8"), "UTF-8");

                SolrClient solrClient = MCRSolrClientFactory.getSolrClient();
                SolrQuery solrQuery = new SolrQuery(key + ":" + ClientUtils.escapeQueryChars(value));
                solrQuery.setRows(1);
                QueryResponse solrQueryResponse = solrClient.query(solrQuery);
                SolrDocumentList solrResults = solrQueryResponse.getResults();
                if (solrResults.getNumFound() > 0) {
                    mcrID = String.valueOf(solrResults.get(0).getFirstValue("returnId"));
                }

            } catch (SolrServerException | IOException e) {
                LOGGER.error(e);
            }
        }

        if (path.length == 2) {
            if ("xml".equals(request.getParameter("open"))) {
                Document doc = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID)).createXML();
                response.setContentType("text/xml");
                XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                xout.output(doc, response.getOutputStream());
            } else if ("solrdocument".equals(request.getParameter("open"))
                || "solr".equals(request.getParameter("open"))) {
                String style = request.getParameter("open");
                //String xslfile = style + "-object.xsl";
                String xslfile = "xsl/mycoreobject-" + style + ".xsl";

                Document doc = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID)).createXML();
                response.setContentType("text/xml");

                Transformer trans = translist.get(xslfile);
                if (trans == null) {

                    LOGGER.debug("Will load transformer stylesheet " + xslfile + "for export.");

                    InputStream in = getClass().getResourceAsStream("/" + xslfile);

                    try {
                        if (in != null) {
                            StreamSource source = new StreamSource(in);
                            TransformerFactory transfakt = TransformerFactory.newInstance();
                            transfakt.setURIResolver(MCRURIResolver.instance());
                            trans = transfakt.newTransformer(source);
                            translist.put(xslfile, trans);
                        }
                        if (trans != null) {
                            StreamResult sr = new StreamResult(response.getOutputStream());
                            trans.transform(new org.jdom2.transform.JDOMSource(doc), sr);
                        }

                    } catch (Exception e) {
                        LOGGER.warn("Error while load transformer ressource " + xslfile + ".");
                        if (LOGGER.isDebugEnabled()) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // show metadata as docdetails view
                this.getServletContext().getRequestDispatcher("/content/docdetails.jsp?id=" + mcrID).forward(request,
                    response);
            }
            return;
        }
        String action = path[2];
        if (action.equals("image") || action.equals("dfgviewer")) {
            String url = "";
            if (path.length == 3 || path.length == 4) {
                url = createURLForDFGViewer(request, mcrID, OpenBy.empty, "");
            }
            if (path.length > 4 && path[3].equals("page")) {
                url = createURLForDFGViewer(request, mcrID, OpenBy.page, path[4]);
            }
            if (path.length > 4 && path[3].equals("nr")) {
                url = createURLForDFGViewer(request, mcrID, OpenBy.nr, path[4]);
            }
            if (path.length > 4 && path[3].equals("part")) {
                url = createURLForDFGViewer(request, mcrID, OpenBy.part, path[4]);
            }
            if (url.length() > 0) {
                LOGGER.debug("DFGViewer URL: " + url);
                response.sendRedirect(url);
            }
            return;
        }
      
        if (action.equals("pdf")) {
            String url = "";
            if (path.length > 4) {
                if (path[3].equals("page")) {
                    url = createURLForPDF(request, mcrID, path[4], null);
                }
                if (path[3].equals("nr")) {
                    url = createURLForPDF(request, mcrID, null, path[4]);
                }
            } else {
                url = createURLForPDF(request, mcrID, null, null);
            }
            if (url.length() > 0) {
                LOGGER.debug("PDF URL: " + url);
                response.sendRedirect(url);
            }
            return;
        }

        if (action.equals("pdfdownload")) {
            // "pdf download is beeing implemented" page
            // this.getServletContext().getRequestDispatcher("/content/pdfdownload.jsp?id="
            // +mcrID).forward(request, response);
            if (key.equals("recordIdentifier")) {
                response.sendRedirect(
                    response.encodeRedirectURL(request.getContextPath() + "/pdfdownload/recordIdentifier/" + value));
            }
            // TODO - SOLR Migration
            // allow mcrids and other identifiers which can be looked up in SOLR
            // index
            else {
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath()));
            }
        }

        if (action.equals("cover")) {
            String url = createURLForCover(request, mcrID);
            if (url.length() > 0) {
                LOGGER.debug("Cover URL: " + url);
                response.sendRedirect(url);
            }
        }

        if (action.equals("fulltext")) {
            if (mcrID.startsWith("mvdok")) {
                String url = MCRFrontendUtil.getBaseURL() + "mjbrenderer?id=" + mcrID;
                response.sendRedirect(url);
            }
        }

        if (action.equals("dv_mets")) {
            // shall not be used, use "/file/DV_METS" with redirect to MCR file
            // instead
            String label = "DV_METS";
            MCRObjectID mcrDerID = null;
            MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
            MCRObjectStructure structure = o.getStructure();
            for (MCRMetaLinkID der : structure.getDerivates()) {
                if (label.equals(der.getXLinkHref()) || label.equals(der.getXLinkLabel())
                    || label.equals(der.getXLinkTitle())) {
                    mcrDerID = der.getXLinkHrefID();
                    break;
                }
            }
            if (mcrDerID != null) {
                StringBuffer filepath = new StringBuffer();
                // display main document
                MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(mcrDerID);
                String mainDoc = der.getDerivate().getInternals().getMainDoc();
                if (mainDoc != null && mainDoc.length() > 0) {
                    filepath.append("/").append(mainDoc);
                }
                MCRDirectory root = MCRDirectory.getRootDirectory(mcrDerID.toString());
                MCRFilesystemNode mainFile = root.getChildByPath(mainDoc);
                sendFile(request, response, (MCRFile) mainFile);
            }
        }

        if (action.equals("file") && path.length > 3) {
            String label = path[3];
            long id = -1;
            try {
                id = Integer.parseInt(label);
            } catch (NumberFormatException nfe) {
                // do nothing -> id = -1;
            }

            MCRObjectID mcrDerID = null;
            if (id == -1) {
                MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
                MCRObjectStructure structure = o.getStructure();
                for (MCRMetaLinkID der : structure.getDerivates()) {
                    if (label.equals(der.getXLinkHref()) || label.equals(der.getXLinkLabel())
                        || label.equals(der.getXLinkTitle())) {
                        mcrDerID = der.getXLinkHrefID();
                        break;
                    }
                }
            } else {
                MCRObjectID mcrMetaID = MCRObjectID.getInstance(mcrID);
                mcrDerID = MCRObjectID.getInstance(mcrMetaID.getProjectId() + "_derivate_" + label);
            }

            if (mcrDerID != null) {
                StringBuffer filepath = new StringBuffer();
                if (path.length == 4) {
                    // display main document
                    MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(mcrDerID);
                    String mainDoc = der.getDerivate().getInternals().getMainDoc();
                    if (mainDoc != null && mainDoc.length() > 0) {
                        filepath.append("/").append(mainDoc);
                    }
                } else {
                    // display file on remaining path
                    for (int i = 4; i < path.length; i++) {
                        filepath.append("/").append(path[i]);
                    }
                }
                StringBuffer url = new StringBuffer();
                url.append(MCRFrontendUtil.getBaseURL()).append("file/").append(mcrID).append("/")
                    .append(mcrDerID.toString()).append(filepath);
                response.sendRedirect(url.toString());
            }
        }
    }

    // CODE under development - try to solve the "Open Large PDF file" problem
    @SuppressWarnings("unused")
    private void showDerivateFile(MCRObjectID mcrID, MCRObjectID mcrDerID, String path, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
        // OLD CODE
        // the urn with information about the MCRObjectID
        MCRFilesystemNode mainFile = null;
        if (mcrDerID != null) {
            MCRDirectory root = MCRDirectory.getRootDirectory(mcrDerID.toString());
            if (path != null) {
                mainFile = root.getChildByPath(path);
            } else {
                MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(mcrDerID);
                String mainDoc = der.getDerivate().getInternals().getMainDoc();
                if (mainDoc != null) {
                    mainFile = root.getChildByPath(mainDoc);
                }
                if (mainFile == null) {
                    MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
                    if (myfiles.length == 1) {
                        mainFile = myfiles[0];
                    }
                }
            }
        }
        if (mainFile != null) {
            if (!MCRAccessManager.checkPermissionForReadingDerivate(mainFile.getOwnerID())) {
                LOGGER.info("MCRFileNodeServlet: AccessForbidden to " + mainFile.getName());
                response.sendRedirect(response.encodeRedirectURL(MCRFrontendUtil.getBaseURL() + accessErrorPage));
                return;
            }

            if (mainFile.getPath().endsWith(".pdf")) {
                openPDF(request, response, mcrID.toString(), (MCRFile) mainFile);
                return;
            }
            sendFile(request, response, (MCRFile) mainFile);
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        getServletContext()
            .getRequestDispatcher("/nav?path=~mycore-error&messageKey=MCRJSPGlobalResolver.error.notfound")
            .forward(request, response);
    }

    // openPDF
    private void openPDF(HttpServletRequest request, HttpServletResponse response, String mcrid, MCRFile mcrFile)
        throws IOException {
        String page = request.getParameter("page");
        String nr = request.getParameter("nr");

        StringBuffer sbURL = new StringBuffer(MCRFrontendUtil.getBaseURL());
        sbURL.append("file/").append(mcrid).append("/").append(mcrFile.getPath());
        if (page != null) {
            sbURL.append("#page=").append(page);
        } else if (nr != null) {
            sbURL.append("#page=").append(nr);
        }
        String url = sbURL.toString();
        if (url.length() > 0) {
            response.sendRedirect(url);
        }

    }

    private static String accessErrorPage = MCRConfiguration.instance().getString("MCR.Access.Page.Error", "");

    /**
     * Sends the contents of an MCRFile to the client.
     * 
     * @see MCRFileNodeServlet for implementation details
     * 
     * 
     */
    private void sendFile(HttpServletRequest req, HttpServletResponse res, MCRFile file) throws IOException {
        LOGGER.info("Sending file " + file.getName());

        res.setContentType(file.getContentType().getMimeType());
        res.setContentLength((int) file.getSize());
        res.addHeader("Accept-Ranges", "none"); // Advice client not to attempt
                                                // range requests

        // no transaction needed to copy long streams over slow connections

        try(OutputStream out = new BufferedOutputStream(res.getOutputStream())){
            file.getContentTo(out);
        }
    }
}
