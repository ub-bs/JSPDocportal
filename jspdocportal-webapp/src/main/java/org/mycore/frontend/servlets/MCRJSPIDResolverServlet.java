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

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRDirectoryXML;
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
import org.mycore.services.fieldquery.MCRQueryParser;
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
	protected enum OpenBy{page, nr, part, empty};

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
    	String xmlextended = request.getParameter("xmlextended");
    	String img = request.getParameter("img");
    	String html = request.getParameter("html");

    	String queryString = "";
    	String[] keys =  new String[]{"id", "ppn", "urn"};
    	for(String key: keys){
    		if(request.getParameterMap().containsKey(key)){
    			String value = request.getParameter(key);
    			if(key.equals("id")){
    				value = recalculateMCRObjectID(value);
    			}
    			if(value!=null){
    			    queryString = "("+key+" = "+value+")";
    			}
    			break;
    		}
    	}
    	if(queryString.length()==0){
    		getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
    	}
    	else{
            MCRQuery query = new MCRQuery((new MCRQueryParser()).parse(queryString));
            MCRResults result = MCRQueryManager.search(query);
    		
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
    			else if(xmlextended!=null){
    				response.setContentType("text/xml");
    				outputExtendedXML(mcrID,  response.getOutputStream());
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
    				String part = request.getParameter("part");
                    if(part!=null){
                        url = createURLForDFGViewer(request, mcrID, OpenBy.part, part);
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
	
	protected String recalculateMCRObjectID(String oldID){
	    String result = oldID.replace("cpr_staff_0000", "cpr_person_").replace("cpr_professor_0000", "cpr_person_");
	    result = result.replace("_series_", "_bundle_");
	    try{
	        MCRObjectID mcrID = MCRObjectID.getInstance(result);
	        return mcrID.toString();
	    }
	    catch(MCRException ex){
	        LOGGER.error(ex.getMessage(), ex);
	    }
	    
	    return null;
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
				sbPath.append("file/").append(mcrID).append("/").append(myfiles[0].getPath());
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
			if(der.getXLinkTitle().equals("HTML")){
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
			sbPath.append("file/").append(mcrID).append("/").append(der.getId().toString()).append("/").append(mainDoc);
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
			if("METS".equals(derMetaLink.getXLinkTitle()) || "DV_METS".equals(derMetaLink.getXLinkTitle())){
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
						if(!nr.isEmpty()){
						    if(openBy == OpenBy.page){
						        eMETSPhysDiv = XPathFactory.instance().compile("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
									"/mets:div[@TYPE='physSequence']/mets:div[starts-with(@ORDERLABEL, '" +nr+"')]", Filters.element(), null, nsMets).evaluateFirst(docMETS);
						    }
						    else if (openBy == OpenBy.nr){
						        eMETSPhysDiv = XPathFactory.instance().compile("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
									"/mets:div[@TYPE='physSequence']/mets:div[@ORDER='" +nr+"']", Filters.element(), null, nsMets).evaluateFirst(docMETS);
						    }
						    else if (openBy == OpenBy.part){
						        eMETSPhysDiv = XPathFactory.instance().compile("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
                                    "//mets:div[@ID='" +nr+"']", Filters.element(), null, nsMets).evaluateFirst(docMETS);
						        if(eMETSPhysDiv == null){
						            Element eMETSLogDiv = XPathFactory.instance().compile("/mets:mets/mets:structMap[@TYPE='LOGICAL']" +
	                                    "//mets:div[@ID='" +nr+"']", Filters.element(), null, nsMets).evaluateFirst(docMETS);
						            if(eMETSLogDiv!=null){
						                Element eMETSSmLink = XPathFactory.instance().compile("/mets:mets/mets:structLink" +
	                                        "//mets:smLink[@xlink:from='" +eMETSLogDiv.getAttributeValue("ID")+"']", Filters.element(), null, nsMets, nsXlink).evaluateFirst(docMETS);
						                if(eMETSSmLink!=null){
						                    eMETSPhysDiv = XPathFactory.instance().compile("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
			                                    "//mets:div[@ID='" +eMETSSmLink.getAttributeValue("to", nsXlink)+"']", Filters.element(), null, nsMets).evaluateFirst(docMETS);
						                }
						            }
						        }
						    }
						}
						
						if(thumb == null){
							//display "first page" in DFG-Viewer
							sbURL = new StringBuffer(MCRConfiguration.instance().getString("MCR.JSPDocportal.DFG-Viewer.BaseURL").trim());
							sbURL.append("?set[mets]=");
							sbURL.append(URLEncoder.encode(getBaseURL()+"file/"+mcrID+"/"+f.getPath(), "UTF-8"));
							if(eMETSPhysDiv!=null){
							    String order = eMETSPhysDiv.getAttributeValue("ORDER");
							    if(order!=null){
							        sbURL.append("&set[image]=").append(order);
							    }
							    //else: phys_000 -> goto first page
							}							
						}
						else if(eMETSPhysDiv!=null){
							//return thumb image    										
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
								Element eFLocat = XPathFactory.instance().compile("//mets:file[@ID='"+fileid+"']/mets:FLocat", Filters.element(), null, nsMets).evaluateFirst(docMETS);
								if(eFLocat!=null){
									sbURL = new StringBuffer(eFLocat.getAttributeValue("href", nsXlink));
								}
							}
						}
					} //end if
				} //end for
	            break;
			} //end [if("METS" ...)]
		}
		}catch(Exception e){
			LOGGER.error("Error creating URL for DFG Viewer", e);
			return "";
		}
		LOGGER.debug("created DFG-ViewerURL: "+request.getContextPath()+ " -> "+sbURL.toString());
		return sbURL.toString();
	}
	
	/**
	 * <mycoreobject >
  	 * 	<structure>
     *   <derobjects class="MCRMetaLinkID">
	 * @param mcrID
	 * @param out
	 */
		protected void outputExtendedXML(String mcrID, ServletOutputStream out) throws Exception{
		Namespace nsXlink=Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
		Document doc = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID)).createXML();    	    		 
		Element eStructure = doc.getRootElement().getChild("structure");
		if(eStructure==null) return;
		Element eDerObjects = eStructure.getChild("derobjects");
		if(eDerObjects != null){
			for(Element eDer: (List<Element>)eDerObjects.getChildren("derobject")){
				String derID = eDer.getAttributeValue("href", nsXlink);
				Document docDer = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derID)).createXML();
				eDer.addContent(docDer.getRootElement().detach());
			
				//<mycorederivate xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:noNamespaceSchemaLocation="datamodel-derivate.xsd" ID="cpr_derivate_00003760" label="display_image" version="1.3">
				//  <derivate display="true">

				eDer = eDer.getChild("mycorederivate").getChild("derivate");
				Document fileDoc = MCRDirectoryXML.getInstance().getDirectory("/"+derID, false);
				eDer.addContent(fileDoc.getRootElement().detach());			
			}
		}
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(doc, out);
	}		
}
