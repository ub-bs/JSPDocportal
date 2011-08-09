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

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRResults;


/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Robert Stephan
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPIDResolverServlet extends MCRServlet {
	protected enum OpenBy{page, nr, empty};

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCRJSPObjectServlet.class);


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
     * @param job
     *            the MCRServletJob instance
     */
	public void doGetPost(MCRServletJob job) throws ServletException, Exception {
        // the urn with information about the MCRObjectID
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    
    	String pdf = request.getParameter("pdf");
    	String xml = request.getParameter("xml");
    	String img = request.getParameter("img");
    	String html = request.getParameter("html");

    	String queryString = "";
    	String[] keys =  new String[]{"id", "ppn", "urn"};
    	for(String key: keys){
    		if(request.getParameterMap().containsKey(key)){
    			queryString = createQuery(key, request.getParameter(key));
    			break;
    		}
    	}
    	if(queryString.length()==0){
    		getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
    	}
    	else{
    		StringReader stringReader=new StringReader(queryString.toString());
    		SAXBuilder builder = new SAXBuilder();
    		Document input = builder.build(stringReader);
    		MCRResults result = MCRQueryManager.search(MCRQuery.parseXML(input));
    		if(result.getNumHits()>0){
    			String mcrID = result.getHit(0).getID();
    			if(pdf!=null){
    				String page= request.getParameter("page");
    				String nr = request.getParameter("nr");
    				String url = createURLForPDF(request, mcrID, page, nr);
    				if(url.length()>0){
    					response.sendRedirect(url);
    					return;
    				}    				
    			}
    			else if(html!=null){
    				String url = createURLForHTML(request, mcrID);
    				//this.getServletContext().getRequestDispatcher("/nav?path=~showHTML&url=" +url).forward(request, response);
    				response.sendRedirect(url);
    			}
    			else if(xml!=null){
    				Document doc = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID)).createXML();    	    		 
    				response.setContentType("text/xml");
    				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
    				xout.output(doc, response.getOutputStream());
    				return;
    			}
    			else if(img!=null){
    				String url="";
    				String page= request.getParameter("page");
    				if(page!=null){
    					url = createURLForDFGViewer(request, mcrID, OpenBy.page, page);
    				}
    				String nr = request.getParameter("nr");
    				if(nr!=null){
    					url = createURLForDFGViewer(request, mcrID, OpenBy.nr, nr);
    				}
    				if(url.length()>0){
    					LOGGER.debug("DFGViewer URL: "+url);
    					response.sendRedirect(url);
    				}
    			} //end [if(img!=null)]
    			else{
    				this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +mcrID).forward(request, response);
    			}
    		} //end [if(result.getNumHits()>0)]
    	}	
	}	
	
	
	
	protected String createQuery(String key, String value) {
		StringBuffer queryString = new StringBuffer();
		queryString = new StringBuffer();
		queryString.append("<query>");
		queryString.append("   <conditions format=\"xml\">");
		queryString.append("      <boolean operator=\"and\">");
		// queryString.append("       <condition field=\"objectType\" operator=\"=\" value=\"professor\" />");
		queryString.append("         <condition field=\""+key+"\" operator=\"=\" value=\"" + value + "\" />");
		queryString.append("      </boolean>");
		queryString.append("   </conditions>");
		queryString.append("</query>");
		
		return queryString.toString();
	}
	
	protected String createURLForPDF(HttpServletRequest request, String mcrID, String page, String nr){
				
	    MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
		MCRObjectStructure structure = o.getStructure(); 
		MCRMetaLinkID derMetaLink = structure.getDerivates().get(0);
		MCRObjectID derID = derMetaLink.getXLinkHrefID();
		MCRDirectory root;
		root = MCRDirectory.getRootDirectory(derID.toString());
		MCRFilesystemNode[] myfiles = root.getChildren();
		if(myfiles.length==1){        				
			/* the following code does not change the URL in the browser, but I cannot set additional parameter to open the pdf */
			/*
			response.setContentType( "application/pdf" );
			response.setHeader("Content-Disposition", "attachment; filename=" + myfiles[0].getName());
			if (myfiles[0] instanceof MCRFile) {
				((MCRFile)myfiles[0]).getContentTo(response.getOutputStream());
			}*/
			if(myfiles[0] instanceof MCRFile && myfiles[0].getAbsolutePath().endsWith(".pdf")){
				StringBuffer sbPath = new StringBuffer(getBaseURL());
				sbPath.append("file/").append(myfiles[0].getPath());
				if(page!=null){
					sbPath.append("#page=").append(page);
				}
				else if(nr!=null){
					sbPath.append("#page=").append(nr);
				}
				return sbPath.toString();
			}
		} 
		return "";
	}

	//createURL for HTML Page
	protected String createURLForHTML(HttpServletRequest request, String mcrID){
		String anchor= request.getParameter("anchor");
	    		
	    MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
		MCRObjectStructure structure = o.getStructure();
		MCRMetaLinkID derMetaLink= null;
		for(MCRMetaLinkID der: structure.getDerivates()){
			if(der.getXLinkLabel().equals("HTML")){
				derMetaLink = der;
			}
		}
		if(derMetaLink==null){
			return "";
		}
		MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derMetaLink.getXLinkHrefID());
		String mainDoc = der.getDerivate().getInternals().getMainDoc();
		if(mainDoc!=null && mainDoc.length()>0){
			StringBuffer sbPath = new StringBuffer(getBaseURL());
			sbPath.append("file/").append(der.getId().toString()).append("/").append(mainDoc);
			if(anchor!=null){
				sbPath.append("#").append(anchor);
			}
			return sbPath.toString();
			
		} 
		return "";
	}
	
	//Create URL for DFG ImageViewer and Forward to it
	//http://dfg-viewer.de/v1/?set%5Bmets%5D=http%3A%2F%2Frosdok.uni-rostock.de%2Fdata%2Fetwas%2Fetwas1737%2Fetwas1737.mets.xml&set%5Bzoom%5D=min
	protected String createURLForDFGViewer(HttpServletRequest request, String mcrID, OpenBy openBy, String nr){

		String thumb = request.getParameter("thumb");
		
		StringBuffer sbURL = new StringBuffer("");
		try{
		MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
		for(MCRMetaLinkID derMetaLink: o.getStructure().getDerivates()){    					 
			if("METS".equals(derMetaLink.getXLinkLabel())){
				MCRObjectID derID = derMetaLink.getXLinkHrefID();
				MCRDirectory root = MCRDirectory.getRootDirectory(derID.toString());
				MCRFilesystemNode[] myfiles = root.getChildren();
				for (MCRFilesystemNode f: myfiles){
					if((f instanceof MCRFile) && ((MCRFile) f).getAbsolutePath().endsWith(".mets.xml")){
						Namespace nsMets=Namespace.getNamespace("mets", "http://www.loc.gov/METS/");
						Namespace nsXlink=Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
						Document docMETS = ((MCRFile)f).getContentAsJDOM();
						Element eMETSPhysDiv = null;
						while (nr.startsWith("0")){
							nr=nr.substring(1);
						}
						if(openBy == OpenBy.page){
							XPath xpID = XPath.newInstance("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
									"/mets:div[@TYPE='physSequence']/mets:div[starts-with(@ORDERLABEL, '" +nr+"')]");
							xpID.addNamespace(nsMets);
							eMETSPhysDiv = (Element)xpID.selectSingleNode(docMETS);
						}
						else if (openBy == OpenBy.nr){
							XPath xpID = XPath.newInstance("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
									"/mets:div[@TYPE='physSequence']/mets:div[@ORDER='" +nr+"']");
							xpID.addNamespace(nsMets);
							eMETSPhysDiv = (Element)xpID.selectSingleNode(docMETS);
						}
						
						if(thumb == null){
								//display in DFG-Viewer
							sbURL = new StringBuffer("http://dfg-viewer.de/v1/");
							sbURL.append("?set%5Bmets%5D=");
							sbURL.append(URLEncoder.encode(getBaseURL()+"file/"+f.getPath(), "UTF-8"));
							if(eMETSPhysDiv!=null){
								sbURL.append("&set[image]=").append(eMETSPhysDiv.getAttributeValue("ORDER"));
							}
							sbURL.append("&set[zoom]=min");
						}
						else if(eMETSPhysDiv!=null){
							//return thumb image    										
							@SuppressWarnings("unchecked")
							List<Element> l = (List<Element>) eMETSPhysDiv.getChildren();
							String fileid = null;
							for(Element e: l){
								if(e.getAttributeValue("FILEID").startsWith("THUMB")){
									fileid = e.getAttributeValue("FILEID");
								}
							}
							if(fileid !=null){
							// <mets:file MIMETYPE="image/jpeg" ID="THUMBS.matrikel1760-1789-Buetzow_c0001">
							//		<mets:FLocat LOCTYPE="URL" xlink:href="http://rosdok.uni-rostock.de/data/matrikel_handschriften/matrikel1760-1789-Buetzow/THUMBS/matrikel1760-1789-Buetzow_c0001.jpg" />
							//  </mets:file>
								
								XPath xpFLocat = XPath.newInstance("//mets:file[@ID='"+fileid+"']/mets:FLocat");
								xpFLocat.addNamespace(nsMets);
								Element eFLocat = (Element)xpFLocat.selectSingleNode(docMETS);
								if(eFLocat!=null){
									sbURL = new StringBuffer(eFLocat.getAttributeValue("href", nsXlink));
								}
							}
						}
					} //end if
				} //end for			
			} //end [if("METS" ...)]
		}
		}catch(Exception e){
			LOGGER.error("Error creating URL for DFG Viewer", e);
			return "";
		}
		return sbURL.toString();
	}
}
