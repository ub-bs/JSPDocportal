/*
 * $RCSfile$
 * $Revision$ $Date$
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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * This Servlet overides only the output methods of mcrfilenodservlet for jsp docportal use 
 * @author Anja Schaar
 * 
 *  
 *  */
public class MCRJSPFileNodeServlet extends MCRFileNodeServlet {
    private static final long serialVersionUID = 1L;
    // The Log4J logger
    private static Logger LOGGER = Logger.getLogger(MCRJSPFileNodeServlet.class.getName());
    private MCRFileNodeServlet mcrFileNodeServlet = new MCRFileNodeServlet();

    public void doGetPost(MCRServletJob job) throws IOException {

        HttpServletRequest request = job.getRequest();
        HttpServletResponse response = job.getResponse();

        // local node to be retrieved
        MCRFilesystemNode root;

        if (request.getPathInfo() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: HTTP request path is null");
            return;
        }

        String ownerID = getOwnerID(request);

        try {
            root = MCRFilesystemNode.getRootNode(ownerID);
        } catch (org.mycore.common.MCRPersistenceException e) {
            // Could not get value from JDBC result set
            LOGGER.error("MCRFileNodeServlet: Error while getting root node!", e);
            root = null;
        }

        if (root == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No root node found for owner ID " + ownerID);
            return;
        }

        if (root instanceof MCRFile) {
            if (request.getPathInfo().length() > ownerID.length() + 1) {
                // request path is too long
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Error: No such file or directory " + request.getPathInfo());
                return;
            }
            sendFile(job, (MCRFile) root);
            return;
        }

        // root node is a directory
        MCRDirectory dir = (MCRDirectory) root;
        String path = getPath(request);
        MCRFilesystemNode node = dir.getChildByPath(path);

        if (node == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error: No such file or directory " + path);
            return;
        } else if (node instanceof MCRFile) {
            sendFile(job, (MCRFile) node);
            return;
        } else {
            sendDirectory(request, response, (MCRDirectory) node);
            return;
        }
    }

    /**
     *  retrieves the derivate ID of the owning derivate from request path.
     *  @param request - the http request object
     */
    protected static String getOwnerID(HttpServletRequest request) {
        String[] path = StringUtils.split(request.getPathInfo(), "/");
        //apache commons StringUtils.split ignores leading and trailing separators and removes empty parts
        
        if(path.length>1){
            //path beginns with object id followed by derivate id 
            if(path[1].contains("_derivate_")){
                return path[1];
            }
        }
        
        if(path.length>0){
            return path[0];
        }

        return "";
    }

    /**
     *  Retrieves the path of the file to display from request path.
     *  @param request - the http request object
     */
    protected static String getPath(HttpServletRequest request) {
        String[] path = StringUtils.split(request.getPathInfo(), "/");
        //Apache Commons StringUtils.split ignores leading and trailing separators and remove empty parts
        int pos = 0;
        if(path.length>0){
            pos=1;
        }
        if(path.length>1){
            if(path[1].contains("_derivate_")){
                pos=2;
            }
        }
        return StringUtils.join(path, "/", pos, path.length);
    }

    /**
     * Sends the contents of an MCRFile to the client. If the MCRFile provides
     * an MCRAudioVideoExtender, the file's content is NOT sended to the client,
     * instead the stream that starts the associated streaming player is sended
     * to the client. The HTTP request may then contain StartPos and StopPos
     * parameters that contain the timecodes where to start and/or stop
     * streaming.
     */
    private void sendFile(MCRServletJob job, MCRFile file) throws IOException {
        @SuppressWarnings("rawtypes")
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = job.getClass();
        parameterTypes[1] = file.getClass();

        try {
            Method m = mcrFileNodeServlet.getClass().getDeclaredMethod("sendFile", parameterTypes);
            m.setAccessible(true);

            Object[] parameters = new Object[2];
            parameters[0] = job;
            parameters[1] = file;

            m.invoke(mcrFileNodeServlet, parameters);
        } catch (Exception e) {
            LOGGER.error("Error executing sendFile", e);
        }
    }

    /**
     * Sends the contents of an MCRDirectory as XML data to the client
     */
    private void sendDirectory(HttpServletRequest req, HttpServletResponse res, MCRDirectory dir) throws IOException {
        @SuppressWarnings("rawtypes")
        Class[] parameterTypes = new Class[3];
        parameterTypes[0] = req.getClass();
        parameterTypes[1] = res.getClass();
        parameterTypes[2] = dir.getClass();

        try {
            Method m = mcrFileNodeServlet.getClass().getDeclaredMethod("sendDirectory", parameterTypes);
            m.setAccessible(true);

            Object[] parameters = new Object[3];
            parameters[0] = req;
            parameters[1] = res;
            parameters[2] = dir;

            m.invoke(mcrFileNodeServlet, parameters);
        } catch (Exception e) {
            LOGGER.error("Error executing sendDirectory", e);
        }
    }

    protected void layoutDirectory(HttpServletRequest req, HttpServletResponse res, Document jdom) throws IOException {
        //the derivate
        String derid = jdom.getRootElement().getChild("ownerID").getText();
        MCRDerivate mcr_der = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derid));

        String mainDoc = mcr_der.getDerivate().getInternals().getMainDoc();
        if (mainDoc.length() < 1)
            mainDoc = jdom.getRootElement().getChild("children").getChild("child").getChildText("name");

        String mcrid = mcr_der.getDerivate().getMetaLink().getXLinkHref();
        //MCRObject mcr_obj = new MCRObject();
        Document jmcr_obj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid)).createXML();
        String objTitle = jmcr_obj.getRootElement().getAttributeValue("label");

        if (jmcr_obj.getRootElement().getChild("metadata").getChild("titles") != null)
            objTitle = jmcr_obj.getRootElement().getChild("metadata").getChild("titles").getChildText("title");

        Element addons = new Element("details");
        addons.setAttribute("mainDoc", mainDoc);
        addons.setAttribute("mcrid", mcrid);
        addons.setAttribute("objTitle", objTitle);

        jdom.getRootElement().addContent(addons);

        org.w3c.dom.Document domDoc = null;
        try {
            domDoc = new DOMOutputter().output(jdom);
        } catch (JDOMException e) {
            LOGGER.error("Domoutput failed: ", e);
        }

        req.setAttribute("jDomMcrDir", domDoc);
        String style;
        style = req.getParameter("XSL.Style");
        if (style != null && style.equals("xml")) {
            res.setContentType("text/xml");
            OutputStream out = res.getOutputStream();
            if (jdom != null) {
                new org.jdom2.output.XMLOutputter().output(jdom, out);
            }
            out.close();
            return;
        }
        try {
            getServletContext().getRequestDispatcher("/nav?path=~derivatedetails").forward(req, res);
        } catch (ServletException se) {
            IOException ioe = new IOException("Error showing derivate details");
            ioe.initCause(se);
            throw ioe;
        }
        return;
    }

    protected void errorPage(HttpServletRequest req, HttpServletResponse res, int error, String msg, Exception ex,
            boolean xmlstyle) throws IOException {
        String path = "/nav?path=~mycore-error&messageKey=MCRJSPFileNodeServlet.error." + error + "&message=" + msg;
        try {
            getServletContext().getRequestDispatcher(path).forward(req, res);
        } catch (ServletException se) {
            LOGGER.error("Error on forwarding errorpage", se);

        }
        return;
    }

}